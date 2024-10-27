package io.xeros.net.login;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.google.common.hash.Hashing;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.Censor;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.model.entity.player.LoadGameResult;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.net.PacketBuilder;
import io.xeros.net.login.captcha.CaptchaRequirement;
import io.xeros.net.login.captcha.LoginCaptcha;
import io.xeros.punishments.PunishmentType;
import io.xeros.punishments.Punishments;
import io.xeros.sql.displayname.GetDisplayNameSqlQuery;
import io.xeros.sql.displayname.SetDisplayNameSqlQuery;
import io.xeros.util.ISAACCipher;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.logging.global.InvalidLoginIpLog;
import io.xeros.util.logging.global.LoginLog;
import io.xeros.util.logging.global.LoginRequestLog;
import io.xeros.util.logging.global.SuccessfulLoginIpLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RS2LoginProtocol extends ByteToMessageDecoder {

	private static final Random random = new SecureRandom();
	public static final HashSet<String> ADDRESS_WHITELIST = new HashSet<>();
	private static final Logger logger = LoggerFactory.getLogger(RS2LoginProtocol.class);
	private static final AtomicLong HANDSHAKE_REQUESTS = new AtomicLong();

	private static final BigInteger RSA_MODULUS = new BigInteger("91520827044808581871318118254770120611343888611033050838722939781067880678552781697572245594439341402118233490664238364235358342012694177068230893936750633213888618825951425602731544513980715835301977356001573144440585484179765317637775760229380331179714685593753856711452802805126498363795384945303137663457");
	private static final BigInteger RSA_EXPONENT = new BigInteger("57766613234288292074537212257607470729646631617010134443056794283895417125425551485447510738355094014679079704210093293482457744109390227331193664015558018855984670635316544204147703177358156614235718608836349724714295434200079732563994422062926282344625359040224314349662029531040894284495752158812148861473");

	private static final Pattern[] INVALID_USERNAMES_PATTERNS = {
			Pattern.compile("(?:[Ii][lL]){2}|(?:[lL][Ii]){2}|[lL]{4}|[iI]{4}|(?:[Ii][lL][lL]){2}|(?:[Ii][lL][Ii]){2}"), // ...
	};

	private static final int POW_REQUEST_OPCODE = 19;
	private static final int POW_CHECK_OPCODE = 20;
	private static final int DISCONNECTED = -1;
	private static final int POW_REQUEST = 1;
	private static final int POW_CHECK = 2;
	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 3;
	private int state = CONNECTED;
	/**
	 * The difficulty level for proof of work.
	 * OSRS has this set to 16 for mobile and scales this from 16 to 22 on desktop.
	 */
	private final int powDifficulty = 16;
	private String seed;
	private int randomUnknownValue;
	private String macAddress;
	private String uuid;

	public static LoginReturnCode checkUsername(String name) {
		if (!name.matches("[A-Za-z0-9 ]+")) {
			return LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
		}
		if (name.length() > 12) {
			return LoginReturnCode.USERNAME_TOO_LONG;
		}

		String lowercaseName = name.toLowerCase();
		if (lowercaseName.contains("admin") && !Server.isDebug()) {
			return LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
		}

		if (name.startsWith(" ") || name.endsWith(" ") || name.contains("  ")) {
			return LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
		}

		return LoginReturnCode.SUCCESS;
	}

	public static boolean isValidNewName(String name) {
		String nameLower = name.toLowerCase();
		if (nameLower.chars().filter(it -> it == 'i').count() >= 5
				|| nameLower.chars().filter(it -> it == 'l').count() >= 5
				|| nameLower.chars().filter(it -> it == 'v').count() >= 6
				|| nameLower.chars().filter(it -> it == 'w').count() >= 6
		) {
			return false;
		}

		if (Arrays.stream(INVALID_USERNAMES_PATTERNS).anyMatch(it -> it.matcher(name).find())) {
			return false;
		}

		return true;
	}

	public static boolean isValidUUID(String uuid) {
		return uuid.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
	}

	public static boolean isValidMacAddress(String macAddress) {
		Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
		return p.matcher(macAddress).find();
	}


	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
		if (state == DISCONNECTED) {
			ctx.close();
			return;
		}

		ByteBuf rsaBuffer = null;
		Channel channel = ctx.channel();
		String ipAddress = getIP(channel);

		try {
			switch (state) {
			case CONNECTED:
				HANDSHAKE_REQUESTS.getAndIncrement();
				log(ipAddress, "Handshake");

				if (buffer.readableBytes() < 2) {
					log(ipAddress, "Handshake less than 2 bytes");
					return;
				}

				int request = buffer.readUnsignedByte();
				if (request != 14) {
					log(ipAddress, "Invalid login request " + request);
					Server.getLogging().batchWrite(new InvalidLoginIpLog(ipAddress));
					LoginRequestLimit.addInvalidLogin(ipAddress);
					ctx.close();
					state = DISCONNECTED;
					return;
				}

				buffer.readUnsignedByte();
				channel.writeAndFlush(new PacketBuilder().putLong(0).put((byte) 0).putLong(new SecureRandom().nextLong()).toPacket());
				state = POW_REQUEST;
				log(ipAddress, "Passed handshake");
				return;
			case POW_REQUEST:
				decodePowRequest(ctx, buffer);
				return;
			case POW_CHECK:
				decodeProofOfWork(ctx, buffer);
				return;

			case LOGGING_IN:
				log(ipAddress, "Login block");
				long start = System.currentTimeMillis();
				int loginType = -1, loginPacketSize = -1, loginEncryptPacketSize = -1;
				if (2 <= buffer.capacity()) {
					loginType = buffer.readByte() & 0xff; // should be 16 or 18
					loginPacketSize = buffer.readByte() & 0xff;
					loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2);
					log(ipAddress, "loginType=" + loginType + ", loginPacketSize=" + loginPacketSize + ", loginEncryptPackSize=" + loginEncryptPacketSize);
					if (loginPacketSize <= 0 || loginEncryptPacketSize <= 0) {
						sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Zero or negative login size.");
						ctx.close();
						state = DISCONNECTED;
						return;
					}
				}

				/*
				  Read the magic id.
				 */
				if (loginPacketSize <= buffer.capacity()) {
					int magic = buffer.readByte() & 0xff;
					int version = buffer.readUnsignedShort();
					if (magic != 255) {
						sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Invalid magic id.");
						ctx.close();
						state = DISCONNECTED;
						return;
					}
					int lowMem = buffer.readByte() & 0xff;

					/*
					  Pass the CRC keys.
					 */
					for (int i = 0; i < 9; i++) {
						buffer.readInt();
					}
					loginEncryptPacketSize--;
					if (loginEncryptPacketSize != (buffer.readByte() & 0xff)) {
						sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Encrypted size mismatch.");
						ctx.close();
						state = DISCONNECTED;
						return;
					}

					rsaBuffer = buffer.readBytes(loginEncryptPacketSize);
					byte[] bytes = new byte[rsaBuffer.readableBytes()];
					rsaBuffer.duplicate().readBytes(bytes);
					BigInteger bigInteger = new BigInteger(bytes);
					bigInteger = bigInteger.modPow(RSA_EXPONENT, RSA_MODULUS);
					rsaBuffer = Unpooled.wrappedBuffer(bigInteger.toByteArray());
					if ((rsaBuffer.readByte() & 0xff) != 10) {
						sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Encrypted id != 10");
						ctx.close();
						state = DISCONNECTED;
						return;
					}
					final long clientHalf = rsaBuffer.readLong();
					final long serverHalf = rsaBuffer.readLong();

					int uid = rsaBuffer.readInt();

					if (uid == 0 || uid == 99735086) {
						sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Bad uid: " + uid);
						ctx.close();
						state = DISCONNECTED;
						return;
					}

					final String name = Misc.getRS2String(rsaBuffer);
					final String nameLower = name.toLowerCase();
					final String pass = Misc.getRS2String(rsaBuffer);

					String captcha = Misc.getRS2String(rsaBuffer);
					if (Configuration.LOWERCASE_CAPTCHA)
						captcha = captcha.toLowerCase();

					macAddress = Misc.getRS2String(rsaBuffer);
					uuid = Misc.getRS2String(rsaBuffer);

					String ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();

					if (!Configuration.DISABLE_ADDRESS_VERIFICATION && !ADDRESS_WHITELIST.contains(nameLower)) {
						if (!isValidMacAddress(macAddress)) {
							sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Bad mac address: " + macAddress);
							ctx.close();
							state = DISCONNECTED;
							return;
						}

						if (!isValidUUID(uuid)) {
							sendCode(channel, null, LoginReturnCode.UNABLE_TO_CONNECT, "Bad mac address: " + macAddress);
							ctx.close();
							state = DISCONNECTED;
							return;
						}
					}

					if (!LoginThrottler.canLoginAttempt(nameLower, ip, macAddress, uuid)) {
						if (Server.isDebug()) {
							logger.info("Login attempts are timed out, but skipping for debug purposes.");
						} else {
							sendCode(channel, null, LoginReturnCode.TOO_MANY_CONNECTION_ATTEMPTS,
									String.format("Too many connection attempts: user=%s, ip=%s, mac=%s, uuid=%s", name, ip, macAddress, uuid));
							ctx.close();
							state = DISCONNECTED;
							return;
						}
					}

					boolean passedCaptcha = false;
					CaptchaRequirement captchaRequirement = LoginCaptcha.get(nameLower);
					if (captchaRequirement != null) {
						logger.debug("Player {} has pending captcha {}, entered {}.", name, captchaRequirement.getCaptcha(), captcha);
						if (captchaRequirement.isIncorrect(captcha)) {
							captchaRequirement = LoginCaptcha.refresh(nameLower);
							LoginThrottler.addIncorrectLoginAttempt(nameLower, ip, macAddress, uuid);
							sendCaptcha(channel, LoginReturnCode.CAPTCHA_INCORRECT, captchaRequirement);
							logger.debug("Player failed captcha, sending again, name={}, captchaInput={}", name, captcha);
							return;
						}

						logger.debug("Player {} passed captcha.", name);
						LoginCaptcha.remove(nameLower);
						passedCaptcha = true;
					} else {
						logger.debug("Player {} has no pending captcha.", name);
					}

					if (!Configuration.DISABLE_CAPTCHA_EVERY_LOGIN && !passedCaptcha) {
						CaptchaRequirement captchaRequirement1 = LoginCaptcha.create(nameLower);
						sendCaptcha(channel, LoginReturnCode.CAPTCHA_INCORRECT, captchaRequirement1);
						logger.debug("Requiring captcha for every login, name={}, captchaInput={}", name, captcha);
						return;
					}

					final int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };
					final ISAACCipher inCipher = new ISAACCipher(isaacSeed);
					for (int i = 0; i < isaacSeed.length; i++)
						isaacSeed[i] += 50;
					final ISAACCipher outCipher = new ISAACCipher(isaacSeed);

					ctx.pipeline().replace("decoder", "decoder", new RS2Decoder(inCipher));

					Player login = login(channel, outCipher, version, name, pass, macAddress, uuid, start, passedCaptcha);

					StringJoiner log = new StringJoiner(", ");
					log.add("time=" + Misc.insertCommas("" + (System.currentTimeMillis() - start)) + "ms");
					log.add("mac=" + macAddress);
					log.add("uuid=" + uuid);

					if (login == null) {
						log(ipAddress, "Failure [" + log.toString() + "]");
						ctx.close();
						state = DISCONNECTED;
						return;
					}

					Server.getLogging().batchWrite(new SuccessfulLoginIpLog(ipAddress));
					log(ipAddress, "Success [" + log.toString() + "]");
					list.add(login);
				} else {
					log(ipAddress, "Login block not fully received");
				}
			}
		} catch (Exception e) {
			ctx.close();
			state = DISCONNECTED;
			logger.error("Error occurred while decoding the login block", e);
		} finally {
			if (rsaBuffer != null)
				rsaBuffer.release();
		}
	}

	private void decodePowRequest(ChannelHandlerContext ctx, ByteBuf buffer) {
		if (!buffer.isReadable()) {
			ctx.close();
			state = DISCONNECTED;
			return;
		}

		int request = buffer.readUnsignedByte();
		if (request != POW_REQUEST_OPCODE) {
			logger.info("Session rejected for bad login request id: " + request);
			sendCode(ctx.channel(), null, LoginReturnCode.BAD_SESSION_ID);
			return;
		}
		randomUnknownValue = random.nextInt(5000);
		this.seed = generateSeed();

		// Send information to the client
		int initialAllocation = Byte.BYTES + Short.BYTES; // To send our response w/ bytes to read
		int followingAllocation = Short.BYTES * 2; // The amount of bytes to read client side
		followingAllocation += (Byte.BYTES * this.seed.length()) + Byte.BYTES;

		var pb = new PacketBuilder()
				.put((byte) 60) // 60 = continue with pow decoding
				.putShort(followingAllocation) // to determine how many bytes to read
				.putShort(randomUnknownValue)
				.putShort(powDifficulty);

		for(char c: seed.toCharArray())
			pb.put((byte) c);
		pb.put((byte) 0);

		ctx.channel().writeAndFlush(pb.toPacket());

		log(getIP(ctx), "Passed POW request");
		state = POW_CHECK;
	}

	private void decodeProofOfWork(ChannelHandlerContext ctx, ByteBuf buffer) {
		if (!buffer.isReadable()) {
			ctx.close();
			state = DISCONNECTED;
			return;
		}

		int request = buffer.readUnsignedByte();
		if (request != POW_CHECK_OPCODE) {
			logger.info("Session rejected for bad login request id: " + request);
			sendCode(ctx.channel(), null, LoginReturnCode.BAD_SESSION_ID);
			return;
		}

		long response = buffer.readLong();

		// server checks response combined with the other data have >= difficulty of trailing bits
		String str = Integer.toHexString(randomUnknownValue) + Integer.toHexString(powDifficulty)
				+ seed + Integer.toHexString((int) response);

		byte[] hash = Hashing.sha256().hashBytes(str.getBytes()).asBytes();
		int trailingBits = getTrailingZeroBits(hash);
		boolean success = trailingBits >= this.powDifficulty;

		// Send information to the client
		int capacity = Byte.BYTES + (success ? Long.BYTES : 0);
		int responseCode = success ? 0 : 61;

		var pb = new PacketBuilder()
				.put((byte) responseCode);
		if (success) pb.putLong(random.nextLong());
		ctx.channel().writeAndFlush(pb.toPacket());

		if (!success) {
			log(getIP(ctx), "Failed POW check");
			state = POW_REQUEST;
			return;
		}

		log(getIP(ctx), "Passed POW check");
		state = LOGGING_IN;
	}


	private static Player login(Channel channel, ISAACCipher outCipher, int version, String name, String pass,
								String macAddress, String uuid, long startTime, boolean passedCaptcha)
			throws Exception {

		String lowercaseLoginName = name.toLowerCase();
		Player player = new Player(channel);
		player.setLoginName(lowercaseLoginName);
		player.playerPass = pass;
		player.setNameAsLong(Misc.playerNameToInt64(player.getLoginName()));
		player.outStream.packetEncryption = outCipher;
		player.connectedFrom = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
		player.setMacAddress(macAddress);
		player.setUUID(uuid);

		try {
			LoginReturnCode returnCode = checkUsername(lowercaseLoginName);

			if (returnCode != LoginReturnCode.SUCCESS) {
				logger.info("player with invalid name attempted to login: {}", name);
			}

			if (PlayerHandler.isLoggingOut(lowercaseLoginName)) {
				returnCode = LoginReturnCode.ACCOUNT_ALREADY_ONLINE;
			}

			Punishments punishments = Server.getPunishments();

			if (player.getMacAddress() == null || player.getMacAddress().isEmpty()) {
				Discord.writeServerSyncMessage(String.format("Player has logged in without a mac address, possibly using modified client or spoofing mac, loginName=%s, displayName=%s",
						player.getLoginName(), player.getDisplayName()));
				player.setMacAddress(player.getIpAddress());
			}

			if (Configuration.DISABLE_FRESH_LOGIN) {
				returnCode = LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
			}
			if (version != Configuration.CLIENT_VERSION) {
				returnCode = LoginReturnCode.CLIENT_OUT_OF_DATE;
				//System.out.println(Misc.formatPlayerName(player.playerName) + " was rejected because version is behind, master version: " + Configuration.CLIENT_VERSION + ", requested version: " + version);
			}

			if (Server.UpdateServer && PlayerHandler.updateRunning && PlayerHandler.kickAllPlayers) {
				returnCode = LoginReturnCode.SERVER_BEING_UPDATED;
			}

			if (returnCode == LoginReturnCode.SUCCESS) {
				returnCode = loadPlayer(player, name, returnCode, passedCaptcha);
			}

			if (player.getRights().isNot(Right.OWNER)) {
				if (punishments.contains(PunishmentType.BAN, player.getLoginNameLower())
						|| punishments.contains(PunishmentType.MAC_BAN, macAddress)
						|| punishments.contains(PunishmentType.MAC_BAN, player.getUUID())
						|| punishments.contains(PunishmentType.NET_BAN, player.connectedFrom)) {
					returnCode = LoginReturnCode.ACCOUNT_DISABLED;
				}
			}

			if (returnCode == LoginReturnCode.INVALID_USERNAME_OR_PASSWORD) {
				LoginThrottler.addIncorrectLoginAttempt(name, player.connectedFrom, macAddress, uuid);
			}

			if (returnCode == LoginReturnCode.CAPTCHA_REQUIRED) {
				sendCaptcha(channel, LoginReturnCode.CAPTCHA_REQUIRED, LoginCaptcha.create(name.toLowerCase()));
				return null;
			}

			long time = System.currentTimeMillis() - startTime;
			if (returnCode == LoginReturnCode.SUCCESS) {
				LoginThrottler.addSuccessfulLogin(name, player.connectedFrom, macAddress, uuid);
				Server.getLogging().batchWrite(new LoginLog("Queued, loginTime=" + time + "ms", player));
				logger.info("Player login queued [name=" + name + ", mac=" + macAddress + ", ip="
						+ channel.remoteAddress().toString().replace("/", "") + ", time=" + time + "ms]");
				player.saveCharacter = true;
				return player;
			} else {
				sendReturnCode(channel, player, returnCode);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			logger.error("Error during login finalization.", e);
			sendReturnCode(channel, player, LoginReturnCode.ERROR_OCCURRED_ON_PLAYER_LOAD);
			return null;
		}
	}

	public static LoginReturnCode loadPlayer(Player player, String name, LoginReturnCode returnCode, boolean passedCaptcha) throws Exception {
		LoadGameResult load = PlayerSave.loadGame(player, player.getLoginName(), player.playerPass, passedCaptcha);
		if (load == LoadGameResult.ERROR_OCCURRED) {
			returnCode = LoginReturnCode.ERROR_OCCURRED_ON_PLAYER_LOAD;
		} else if (load == LoadGameResult.REQUIRE_CAPTCHA) {
			logger.info("Requiring captcha for player because mac or uuid changed: {}", name);
			return LoginReturnCode.CAPTCHA_REQUIRED;
		} else {
			player.getCollectionLog().loadForPlayer(player);

			GroupIronmanGroup group = GroupIronmanRepository.getFromGroupList(player).orElse(null);
			if (group != null) {
				if (group.getCollectionLog() == null) {
					CollectionLog collectionLog = new CollectionLog();
					collectionLog.loadForGroupIronman(group);
					group.setCollectionLog(collectionLog);
				}

				CollectionLog.combineForGroupIronman(player, group);
				player.getCollectionLog().setLinked(group.getCollectionLog());
			}

			if (load == LoadGameResult.NEW_PLAYER) {
				if (!isValidNewName(name) || Censor.isCensoredName(name))
					return LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;

				if (Configuration.DISABLE_REGISTRATION) {
					return LoginReturnCode.UNABLE_TO_CONNECT;
				}

				if (!Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA && !passedCaptcha) {
					logger.debug("New player, requiring captcha: {}", name);
					return LoginReturnCode.CAPTCHA_REQUIRED;
				}

				logger.debug("Registered {}", player);
				logger.info("New player registered: {}", name);
				Discord.writeServerSyncMessage("New player logged in: '" + name + "'.");
			}

			if (load == LoadGameResult.INVALID_CREDENTIALS) {
				returnCode = LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
			}

			// Set display name on successful load
			if (returnCode == LoginReturnCode.SUCCESS) {
				String displayName = fetchDisplayName(player, name,load == LoadGameResult.NEW_PLAYER);
				if (displayName == null) {
					returnCode = load == LoadGameResult.NEW_PLAYER ? LoginReturnCode.INVALID_USERNAME_OR_PASSWORD
							: LoginReturnCode.ERROR_OCCURRED_ON_PLAYER_LOAD;
					logger.error("Could not load display name for {}, newPlayer={}", name, load == LoadGameResult.NEW_PLAYER);
				} else {
					if (!player.isBot())
						logger.debug("Set \"{}\" display name to \"{}\".", player.getLoginName(), displayName);
					player.setDisplayName(displayName);
				}
			}
		}

		return returnCode;
	}

	private static String fetchDisplayName(Player player, String loginName, boolean newPlayer) throws Exception {
		if (Server.getConfiguration().isDisplayNamesDisabled()) {
			logger.info("Display names are disabled, using login name as display name.");
			return loginName;
		}

		if (!Server.getConfiguration().isLocalDatabaseEnabled()) {
			logger.info("Local SQL server is offline or configuration isn't set, setting display name to login name.");
			return loginName;
		}

		if (newPlayer) {
			boolean result = Server.getDatabaseManager().executeImmediate(new SetDisplayNameSqlQuery(loginName, loginName));
			if (!result) {
				return null;
			}

			return loginName;
		} else {
			String displayName = Server.getDatabaseManager().executeImmediate(new GetDisplayNameSqlQuery(loginName));
			if (displayName == null) {
				logger.error("Existing user could not login because they don't have a display name {}", player);
				return null;
			}

			return displayName;
		}
	}

	public void sendCode(final Channel channel, Player player, LoginReturnCode code, String...message) {
		List<String> msgs = new ArrayList<>();

		String msg = "[" + getStateString();

		if (player == null) {
			if (macAddress != null)
				msg += ", mac=" + macAddress;
			if (uuid != null)
				msg += ", uuid=" + uuid;
		}

		msg += "]";
		msgs.add(msg);

		msgs.addAll(Arrays.asList(message));
		sendReturnCode(channel, player, code, msgs.toArray(new String[0]));
	}

	public static void sendReturnCode(final Channel channel, Player player, LoginReturnCode code, String...message) {
		if (channel != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("Login rejected with code ").append(code);
			if (player != null) {
				builder.append(" for player ").append(player.toString());
			}

			if (message != null) {
				builder.append(" ");
				Arrays.stream(message).forEach(it -> builder.append(it).append(", "));
			}

			logger.info(builder.toString());
			channel.write(new PacketBuilder().put((byte) code.getCode()).toPacket()).addListener(ChannelFutureListener.CLOSE);
			String msg = builder.toString();
			if (player != null) {
				log(player, msg);
			} else {
				log(getIP(channel), msg);
			}

			logger.debug(msg);
			channel.writeAndFlush(new PacketBuilder().put((byte) code.getCode()).toPacket()).addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static void sendCaptcha(Channel channel, LoginReturnCode code, CaptchaRequirement requirement) {
		log(getIP(channel), "Captcha");
		channel.writeAndFlush(new PacketBuilder().put((byte) code.getCode())
				.putShort(requirement.getImage().length)
				.put(requirement.getImage()).toPacket())
				.addListener(ChannelFutureListener.CLOSE);
	}

	private static int getTrailingZeroBits(byte[] bigNumber) {
		int bits = 0;
		for (byte var4 : bigNumber) {
			int n = getTrailingZeroBits(var4);
			bits += n;
			if (n != 8) {
				break;
			}
		}
		return bits;
	}

	private static int getTrailingZeroBits(byte v) {
		if (v == 0) {
			return 8;
		}
		int bits = 0;
		int t = v & 255;
		while ((t & 128) == 0) {
			bits++;
			t <<= 1;
		}
		return bits;
	}

	/**
	 * Generates a random seed for 'proof of work'
	 *
	 * @return A newly created seed
	 */
	private static String generateSeed() {

		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(10);

		for (int i = 0; i < 10; i++) {
			// generate a random number between 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());
			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

	public static String getIP(ChannelHandlerContext ctx) {
		return getIP(ctx.channel());
	}

	public static String getIP(Channel channel) {
		return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
	}

	private static void log(String ip, String message) {
		Server.getLogging().batchWrite(new LoginRequestLog(ip, message));
	}

	private static void log(String ip, String mac, String uuid, String message) {
		Server.getLogging().batchWrite(new LoginRequestLog(ip, mac, uuid, message));
	}

	private static void log(Player player, String message) {
		log(player.connectedFrom, player.getMacAddress(), player.getUUID(), message);
	}

	private String getStateString() {
        return switch (state) {
            case 1 -> "POW Request";
            case 2 -> "POW Check";
            case 3 -> "Logging in";
            case 0 -> "Connected";
            default -> "Unknown " + state;
        };
	}

	public static long getHandshakeRequests() {
		return HANDSHAKE_REQUESTS.get();
	}
}
