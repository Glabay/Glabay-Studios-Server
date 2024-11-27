package io.xeros.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Misc {

	public static String KEYBOARD_CHARACTERS_REGEX = "[a-zA-Z0-9 .,/<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]*";
	public static String KEYBOARD_CHARACTERS_LOWERCASE_REGEX = "[a-z0-9 .,/<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]*";

	public static String booleanToString(boolean bool) {
		return bool ? "enabled" : "disabled";
	}

	public static String convertLocalDateTimeToString(LocalDateTime date) {
		return date.getYear() + ":" + date.getMonth().getValue() + ":" + date.getDayOfMonth() + ":" + date.getHour() + ":"+ date.getMinute();
	}

	public static boolean isInDuelSession(Player c) {
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
			&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return true;
		}
		return false;
	}

	public static Random rand = new Random();

	/**
	 * A boolean method to give access once it has reached a date using format e.g "2021/07/15"
	 * @param dateFormat
	 * @return
	 */
	public static boolean givePermissionWhenPastDate(String dateFormat) {
		/*
		  Safe
		 */
		boolean canUseFeature = false;
		/*
		  Needs a catch block
		 */
		Date allowed = null;
		try {
			/*
			  Gets allowed date in simple format
			 */
			allowed = new SimpleDateFormat("yyyy/MM/dd").parse(dateFormat);
			/*
			  If date >= allowed then returns a value
			 */
			canUseFeature = new Date().after(allowed);
		} catch (ParseException e) {
			e.printStackTrace(System.err);
		}
		/*
		  In case of the catching error
		 */
		return canUseFeature;
	}

	public static String getPriceFormat(int price) {
		Double amount = (double) price;
		boolean useFormat = true;
		String format = "";
		String synx = "";
		if (amount > 1_000_000_000) {
			amount /= 1_000_000_000;
			synx = "B";
		} else if (amount > 1_000_000) {
			amount /= 1_000_000;
			synx = "M";
		} else if (amount > 1_000) {
			amount /= 1_000;
			synx = "K";
		} else {
			synx = "GP";
			useFormat = false;
		}
		format = useFormat ? new DecimalFormat("0.00").format(amount) : ""+amount;
		return format+synx;
	}

	public static LocalDateTime convertStringToLocalDateTime(String string) {
		List<Integer> timeData = Arrays.stream(string.split(":")).map(Integer::parseInt).collect(Collectors.toList());
		return LocalDateTime.of(timeData.get(0), Month.of(timeData.get(1)), timeData.get(2), timeData.get(3), timeData.get(4));
	}

	public static String formatDoubleToDecimal(double number) {
		return new DecimalFormat("0.00").format(number);
	}

	public static String convertTicksToShortTime(long ms) {
		ms *= 600;
		if (ms >= 600000)
			return String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(ms),
					TimeUnit.MILLISECONDS.toMinutes(ms) -
							TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)), // The change is in this line
					TimeUnit.MILLISECONDS.toSeconds(ms) -
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)), TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
	}

	public static String createFileNameSmallDate(LocalDateTime date) {
		return (date.getYear() + "_" + date.getMonth() + "_" + date.getDayOfMonth()).toLowerCase();
	}

	public static boolean isNumberEven(int number) {
		return number % 2 == 0;
	}

	public static String createFileNameFullDate(LocalDateTime date) {
		return (date.getYear()
				+ "_" + date.getMonth()
				+ "_" + date.getDayOfMonth()
				+ "_" + date.getHour()
				+ "_" + date.getMinute()
				+ "_" + date.getSecond()
				+ "_" + date.getNano()).toLowerCase();
	}

	public static void createDirectory(String...directories) {
		for (String directory : directories) {
			if (!new File(directory).exists()) {
				Preconditions.checkState(new File(directory).mkdirs());
			}
		}
	}

	public static int getDayOfTheMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static Month getMonth() {
		return Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1);
	}

	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static boolean isBefore(Month inputMonth, int inputYear) {
		return isBefore(inputMonth, inputYear, getMonth(), getYear());
	}

	public static boolean isBefore(Month inputMonth, int inputYear, Month currentMonth, int currentYear) {
		return currentYear < inputYear || currentYear <= inputYear && currentMonth.ordinal() < inputMonth.ordinal();
	}

	public static boolean isLeapYear() {
		int year = LocalDate.now().getYear();
		if ((year % 4 == 0) && year % 100 != 0) {
			return true;
		} else if ((year % 4 == 0) && (year % 100 == 0) && (year % 400 == 0)) {
			return true;
		} else {
			return false;
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public static double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}


	/**
	 * Creates a new {@link String} array with the specified length with non-null empty elements.
	 * 
	 * @param length the length of the array
	 * @return a new array with all empty elements
	 */
	public static String[] nullToEmpty(int length) {
		String[] output = new String[length];
		Arrays.fill(output, 0, length, "");
		return output;
	}

	/**
	 * Used to determine if the value of the input is a non-negative value. This does permit the value zero as valid input as zero is neither positive nor negative.
	 * 
	 * @param input the input we're trying to determine is a non-negative or not
	 * @return {@code} true if the value is greater than negative one, otherwise {@code false}.
	 */
	public static boolean isNonNegative(int input) {
		return input > -1;
	}

	/**
	 * Determines if the random value returned from range is greater than or equal to the inclusive value of checkpoint.
	 * 
	 * @param range the range of integer values.
	 * 
	 * @param checkpoint the checkpoint to be made.
	 * 
	 * @return {@code true} if checkpoint is greater than the. random value returned,otherwise {@code false}.
	 * 
	 * @throws IllegalStateException thrown if the checkpoint value is less than the minimum range, or greater than the maximum range.
	 */
	public static boolean passedProbability(Range<Integer> range, int checkpoint, boolean ignoreMaximum) throws IllegalStateException {
		if (checkpoint < range.getMinimum() || checkpoint > range.getMaximum() && !ignoreMaximum) {
			throw new IllegalStateException();
		}
		return random(range) >= checkpoint;
	}

	public static boolean passedProbability(Range<Integer> range, int checkpoint) throws IllegalStateException {
		return passedProbability(range, checkpoint, false);
	}

	public static int random(Range<Integer> range) {
		int minimum = range.getMinimum();
		return minimum + random(range.getMaximum() - minimum);
	}

	public static int randomSearch(int[] elements, int inclusive, int exclusiveLength) {
		Preconditions.checkArgument(exclusiveLength <= elements.length, "The length specified is greater than the length of the array.");
		return elements[RandomUtils.nextInt(inclusive, exclusiveLength)];
	}

	public static <T> T randomSearch(T[] elements, int inclusive, int exclusiveLength) {
		Preconditions.checkArgument(exclusiveLength <= elements.length, "The length specified is greater than the length of the array.");
		return elements[RandomUtils.nextInt(inclusive, exclusiveLength)];
	}
	
	public static <T> T randomTypeOfList(List<T> list) {
		return list.get(new SecureRandom().nextInt(list.size()));
	}

	public static <T> List<T> jsonArrayToList(Path path, Class<T[]> clazz) {
		try {
			T[] collection = new Gson().fromJson(Files.newBufferedReader(path), clazz);
			return new ArrayList<T>(Arrays.asList(collection));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static GameItem getItemFromList(List<GameItem> list) {
		if (list.isEmpty()) {
			return null;
		}

		return list.get(RandomUtils.nextInt(0, list.size()));

	}

	public static double preciseRandom(Range<Double> range) {
		Preconditions.checkArgument(range.getMinimum() <= range.getMaximum(), "The maximum range cannot be less than the minimum range.");
		return range.getMinimum() + (new Random().nextDouble() * (range.getMaximum() - range.getMinimum()));
	}

	public static String toFormattedMS(long time) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
				TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
	}

	/**
	 * A linear search is conducted by looping through the elements array and looking for the index that contains the value specified.
	 * 
	 * @param elements the elements we're searching through
	 * @param value the value we're searching for
	 * @return -1 if the value cannot be found in the array, otherwise the index of the value.
	 */
	public static int linearSearch(int[] elements, int value) {
		for (int index = 0; index < elements.length; index++) {
			if (elements[index] == value) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Determines if a given String is a valid IPv4 Address
	 * 
	 * @param input The String which is to be checked
	 * @return
	 */
	public static boolean isIPv4Address(String input) {
		String[] segments = input.split("\\.");
		if (segments.length != 4) {
			return false;
		}
		for (String token : segments) {
			if (!NumberUtils.isNumber(token)) {
				return false;
			}
			int value = Integer.parseInt(token);
			if (value < 0 || value > 255) {
				return false;
			}
		}
		return true;
	}

	public static int toCycles(long time, TimeUnit unit) {
		Preconditions.checkState(time <= Integer.MAX_VALUE, "just cause");
		int ticks = (int) (TimeUnit.MILLISECONDS.convert(time, unit) / 600);
		Preconditions.checkState(ticks >= 0);
		return ticks;
	}

	public static GameItem getRandomItem(List<? extends GameItem> itemArray) {
		return itemArray.get(random(itemArray.size() - 1));
	}

	public static <T> List<T> randoms(List<T> list, int amount) {
		List<T> randoms = Lists.newArrayList();
		if (list.isEmpty() || amount == 0)
			return randoms;
		if (list.size() <= amount) {
			randoms.addAll(list);
			return randoms;
		}

		while (true) {
			T rand = random(list);
			if (randoms.contains(rand))
				continue;
			randoms.add(rand);
			if (randoms.size() >= amount)
				break;
		}

		return randoms;
	}

	public static <T> T random(List<T> list) {
		if (list.isEmpty())
			return null;
		int index = trueRand(list.size());
		return list.get(index);
	}

	public static int combatDifference(Player player, Player player2) {
		if (player.combatLevel > player2.combatLevel)
			return player.combatLevel - player2.combatLevel;
		else if (player.combatLevel < player2.combatLevel)
			return player2.combatLevel - player.combatLevel;
		return 0;
	}

	public static String getRS2String(final ByteBuf buf) {
		final StringBuilder bldr = new StringBuilder();
		byte b;
		while (buf.isReadable() && (b = buf.readByte()) != 10)
			bldr.append((char) b);
		return bldr.toString();
	}

	public static String insertCommas(int str) {
		return insertCommas("" + str);
	}

	public static String insertCommas(String str) {
		if (str.length() < 4) {
			return str;
		}
		return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3);
	}

	public static String getValueRepresentation(long amount) {
		StringBuilder bldr = new StringBuilder();
		if (amount < 1_000) {
			bldr.append(amount);
		} else if (amount >= 1_000 && amount < 1_000_000) {
			bldr.append("@cya@" + amount / 1_000 + "K @whi@(" + insertCommas(Long.toString(amount)) + ")");
		} else if (amount >= 1_000_000) {
			bldr.append("@gre@" + amount / 1_000_000 + "M @whi@(" + insertCommas(Long.toString(amount)) + ")");
		}
		return bldr.toString();
	}

	public static String getValueWithoutRepresentation(long amount) {
		StringBuilder bldr = new StringBuilder();
		if (amount < 1_000) {
			bldr.append(amount);
		} else if (amount >= 1_000 && amount < 1_000_000) {
			bldr.append(amount / 1_000 + "K");
		} else if (amount >= 1_000_000) {
			bldr.append(amount / 1_000_000 + "M");
		}
		return bldr.toString();
	}

	public static String secondsToFormattedCountdown(long seconds) {
		long minutes = seconds % 3600 / 60;
		long hours = seconds % 86400 / 3600;
		long days = seconds / 86400;

		if (days > 0) {
			return days + "d " + hours + "h " + minutes + "m";
		} else if (hours > 0) {
			return hours + "h " + minutes + "m";
		} else {
			return minutes + "m";
		}
	}

	public static String cyclesToDottedTime(long cycles) {
		long minutes = cycles / 100;
		int seconds = (int) ((cycles % 100) * 0.6);
		long hours = minutes / 60;
		if (hours > 0) {
			minutes -= hours * 60;
			if (seconds > 0) {
				return String.format("%d:%02d:%02d", hours, minutes, seconds);
			} else {
				return String.format("%d:%02d", hours, minutes);
			}
		} else {
			return String.format("%d:%02d", minutes, seconds);
		}
	}

	public static String cyclesToTime(long cycles) {
		long minutes = cycles / 100;
		int seconds = (int) ((cycles % 100) * 0.6);
		long hours = minutes / 60;
		if (hours > 0) {
			minutes -= hours * 60;
			if (seconds > 0) {
				return String.format("%dh %02dm %02ds", hours, minutes, seconds);
			} else if (minutes > 0) {
				return String.format("%dh %02dm", hours, minutes);
			} else {
				return String.format("%dh", hours);
			}
		} else {
			if (seconds > 0) {
				return String.format("%dm %02ds", minutes, seconds);
			} else {
				return String.format("%dm", minutes);
			}
		}
	}

	public static String formatPlayerName(String str) {
		str = capitalizeJustFirst(str);
		str.replace("_", " ");
		return str;
	}

	public static String md5Hash(String md5) {
		try {

			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
			}

			return sb.toString();

		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static String capitalize(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
				}
			}
		}
		return s;
	}

	public static String findCommand(String message) {
		if (message == null)
			return "";
		if (!message.contains(" ") && !message.contains("-")) {
			return message;
		} else if (!message.contains(" ")) {
			return message.substring(0, message.indexOf("-"));
		} else if (!message.contains("-")) {
			return message.substring(0, message.indexOf(" "));
		}
		int seperatorIndex = message.indexOf(" ") < message.indexOf("-") ? message.indexOf(" ") : message.indexOf("-");
		return message.substring(0, seperatorIndex);
	}

	public static String findInput(String message) {
		if (!message.contains(" ") && !message.contains("-")) {
			return "";
		} else if (!message.contains(" ")) {
			return message.substring(message.indexOf("-") + 1);
		} else if (!message.contains("-")) {
			return message.substring(message.indexOf(" ") + 1);
		}
		int seperatorIndex = message.indexOf(" ") < message.indexOf("-") ? message.indexOf(" ") : message.indexOf("-");
		return message.substring(seperatorIndex + 1);
	}

	public static int stringToInt(String value) throws NumberFormatException {
		value = value.toLowerCase();
		value = value.replaceAll("k", "000");
		value = value.replaceAll("m", "000000");
		value = value.replaceAll("b", "000000000");
		BigInteger bi = new BigInteger(value);
		if (bi.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			return Integer.MAX_VALUE;
		} else if (bi.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
			return Integer.MIN_VALUE;
		} else {
			return bi.intValue();
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY - playerY <= distance && objectY - playerY >= -distance));
	}
	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int width, int length) {
		return ((objectX - playerX <= width && objectX - playerX >= -width) && (objectY - playerY <= length && objectY - playerY >= -length));
	}

	public static String longToReportPlayerName(long l) {
		int i = 0;
		final char[] ac = new char[12];
		while (l != 0L) {
			final long l1 = l;
			l /= 37L;
			ac[11 - i++] = Misc.playerNameXlateTable[(int) (l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}

	public static final char[] playerNameXlateTable = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', ']', '/', '-', ' ' };

	/**
	 * Calls {@link Misc#longToPlayerName2} on the long and then {@link Misc#fixName(String)}
	 * on the string. Will replace any underscores with spaces.
	 */
	public static String convertLongToFixedName(long l) {
		return fixName(longToPlayerName2(l));
	}

	public static String longToPlayerName2(long l) {
		int i = 0;
		char[] ac = new char[99];
		while (l != 0L) {
			long l1 = l;
			l /= 37L;
			ac[11 - i++] = playerNameXlateTable[(int) (l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}

	public static String fixName(String s) {
		if (s.length() > 0) {
			char ac[] = s.toCharArray();
			for (int j = 0; j < ac.length; j++)
				if (ac[j] == '_') {
					ac[j] = ' ';
					if (j + 1 < ac.length && ac[j + 1] >= 'a'
							&& ac[j + 1] <= 'z')
						ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
				}

			if (ac[0] >= 'a' && ac[0] <= 'z')
				ac[0] = (char) ((ac[0] + 65) - 97);
			return new String(ac);
		} else {
			return s;
		}
	}

	public static String format(long moneyCollectable) {
		return NumberFormat.getInstance().format(moneyCollectable);
	}

	private static final DecimalFormat coinFormat = new DecimalFormat("#.##");

	public static String formatCoins(long amount) {
		if (amount >= 1_000_000_000)
			return coinFormat.format(((double) amount) / 1_000_000_000d) + "B";
		else if (amount >= 1_000_000)
			return coinFormat.format(((double) amount) / 1_000_000d) + "M";
		else if (amount >= 1_000)
			return (amount / 1_000) + "K";
		return "" + amount;
	}

	public static String colorWrap(String color, String message) {
		return "<col=" + color + ">" + message + "</col>";
	}

	public static String getCoinColour(long amount) {
		if (amount >= 10_000_000)
			return "<col=00FF80>";
		if (amount >= 100_000)
			return "<col=FFFFFF>";
		return "<col=FFFF00>";
	}

	public static String capitalizeJustFirst(String str) {
		str = str.toLowerCase();
		if (str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			return str.toUpperCase();
		}
		return str;
	}

	public static void println(String str) {
		System.out.println(str);
	}

	public static String Hex(byte[] data, int offset, int len) {
		String temp = "";
		for (int cntr = 0; cntr < len; cntr++) {
			int num = data[offset + cntr] & 0xFF;
			String myStr;
			if (num < 16)
				myStr = "0";
			else
				myStr = "";
			temp += myStr + Integer.toHexString(num) + " ";
		}
		return temp.toUpperCase().trim();
	}

	public static int hexToInt(byte[] data, int offset, int len) {
		int temp = 0;
		int i = 1000;
		for (int cntr = 0; cntr < len; cntr++) {
			int num = (data[offset + cntr] & 0xFF) * i;
			temp += num;
			if (i > 1)
				i = i / 1000;
		}
		return temp;
	}

	public static String basicEncrypt(String s) {
		String toReturn = "";
		for (int j = 0; j < s.length(); j++) {
			toReturn += (int) s.charAt(j);
		}
		// System.out.println("Encrypt: " + toReturn);
		return toReturn;
	}

	public static int random(int min , int max) {
	    Random rand = new Random();
	    return rand.nextInt((max - min) + 1) + min;
	}

	long seed = System.nanoTime();

	public long randomLong() {
		seed ^= (seed << 21);
		seed ^= (seed >>> 35);
		seed ^= (seed << 4);
		return seed;
	}
	
	public static int random(int range) {
		return (int) (Math.random() * (range + 1));
	}

	/**
	 * Rolls a random number from [1-100] determines if it is lucky
	 * @param chance The chance of rolling lucky
	 * @return True if the roll is lucky
	 */
	public static boolean isLucky(int chance) {
		return random(100) < chance;
	}

	/**
	 * Get a random number between 0 inclusive and {@param range} exclusive.
	 * @param range the range
	 * @return the random number generated.
	 */
	public static int trueRand(int range) {
		return (int) (Math.random() * (range));
	}

	public static long playerNameToInt64(String s) {
		long l = 0L;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}

	public static final char[] validChars = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[',
			']', '>', '<', '^', '`', '~', '_', '/' };

	public static final Pattern VALID_CHAT_PATTERN = Pattern.compile("[\\sa-zA-Z0-9.,/<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]+");

	public static String decodeMessage(byte[] message, int size) {
		try {
			StringBuilder sb = new StringBuilder();
			boolean capitalizeNext = true;
			if (size > message.length) {
				size = message.length;
			}
			for (int i = 0; i < size; i++) {
				char c = validChars[message[i] & 0xff];
				if (capitalizeNext && c >= 'a' && c <= 'z') {
					sb.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					sb.append(c);
				}
				if (c == '.' || c == '!' || c == '?') {
					capitalizeNext = true;
				}

			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isValidChatMessage(String message) {
		return VALID_CHAT_PATTERN.matcher(message).matches();
	}

	public static boolean isSpam(String message) {
		char[] chars = message.toCharArray();
		int matches;
		for (int i = 0; i < chars.length; i++) {
			matches = 0;
			for (int k = i; k < chars.length; k++) {
				if (chars[i] == chars[k])
					matches++;
				else
					break;
				if (matches >= 18) {
					return true;
				}
			}
		}

		return false;
	}

	private static final char[] decodeBuf = new char[4096];

	public static String optimizeText(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
				}
			}
		}
		return s;
	}

	public static char[] xlateTable = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[',
			']' };

	public static int direction(int srcX, int srcY, int x, int y) {
		double dx = (double) x - srcX, dy = (double) y - srcY;
		double angle = Math.atan(dy / dx);
		angle = Math.toDegrees(angle);
		if (Double.isNaN(angle))
			return -1;
		if (Math.signum(dx) < 0)
			angle += 180.0;
		return (int) ((((90 - angle) / 22.5) + 16) % 16);
	}

	public static int distanceBetween(Player c1, Player c2) {
		int x = (int) Math.pow(c1.getX() - c2.getX(), 2.0D);
		int y = (int) Math.pow(c1.getY() - c2.getY(), 2.0D);
		return (int) Math.floor(Math.sqrt(x + y));
	}

	public static int distanceToPoint(int x1, int y1, int x2, int y2) {
		int x = (int) Math.pow(x1 - x2, 2.0D);
		int y = (int) Math.pow(y1 - y2, 2.0D);
		return (int) Math.floor(Math.sqrt(x + y));
	}

	public static byte[] directionDeltaX = { 0, 1, 1, 1, 0, -1, -1, -1 };
	public static byte[] directionDeltaY = { 1, 1, 0, -1, -1, -1, 0, 1 };
	public static byte[] xlateDirectionToClient = { 1, 2, 4, 7, 6, 5, 3, 0 };

	public static String anOrA(String s) {
		s = s.toLowerCase();
		if(s.equalsIgnoreCase("anchovies") || s.equalsIgnoreCase("soft clay") || s.equalsIgnoreCase("cheese") || s.equalsIgnoreCase("ball of wool") || s.equalsIgnoreCase("spice") || s.equalsIgnoreCase("steel nails") || s.equalsIgnoreCase("snape grass") || s.equalsIgnoreCase("coal"))
			return "some";
		if(s.startsWith("a") || s.startsWith("e") || s.startsWith("i") || s.startsWith("o") || s.startsWith("u")) 
			return "an";
		return "a";
	}
	public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
		List<Class<?>> foundClasses = Lists.newArrayList();
		ClassPath classpath = ClassPath.from(Thread.currentThread().getContextClassLoader());
		for (ClassInfo classInfo : classpath.getTopLevelClassesRecursive(packageName)) {
			foundClasses.add(Class.forName(classInfo.getName()));
		}
		System.out.println(packageName + ": found " + foundClasses.size());
		return foundClasses;
	}
	public static <T> List<T> shuffle(List<T> list) {
		List<T> copy = Lists.newArrayList(list);
		Collections.shuffle(copy);
		return copy;
 	}

	public static <T> Queue<T> reverse(Queue<T> queue) {
		List<T> collect = new ArrayList<>(queue);
		Collections.reverse(collect);
		return new LinkedList<T>(collect);
	}

	public static List<String> toNumberedText(List<String> list) {
		List<String> newList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++)
			newList.add((i + 1) + ". " + list.get(i));
		return newList;
	}

	public static String replaceBracketsWithArguments(String string, Object...args) {
		for (Object arg : args) {
			int index = string.indexOf("{}");
			Preconditions.checkState(index != -1, "Invalid number of parameters for string replace.");
			string = string.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
		}
		return string;
	}

	/**
	 * Capitalizes the first letter of every word.
	 */
	public static String capitalizeEveryWord(String string) {
		return WordUtils.capitalize(string);
	}

	public static String formatMemory(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
