package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;
import io.xeros.net.packets.action.EnterStringInput;
import io.xeros.net.packets.action.InterfaceAction;
import io.xeros.net.packets.action.KeyboardShortcutAction;
import io.xeros.net.packets.action.ReceiveString;
import io.xeros.net.packets.itemoptions.ItemOptionOne;
import io.xeros.net.packets.itemoptions.ItemOptionThree;
import io.xeros.net.packets.itemoptions.ItemOptionTwo;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHandler {

	private static final Logger logger = LoggerFactory.getLogger(PacketHandler.class);
	private static final PacketType[] packetId = new PacketType[255];
	public static final int OPCODE_OUT_OF_RANGE_SIZE = -5000;

	public static void processPacket(Player c, int packetType, int packetSize) {
		if (packetType >= packetId.length) {
			invalidPacket(c, "packet opcode out of range.", packetType, packetSize, true, null);
			return;
		}

		PacketType p = packetId[packetType];

		if (p == null) {
			invalidPacket(c, "packet does not exist", packetType, packetSize, true, null);
			return;
		}

		try {
			p.processPacket(c, packetType, packetSize);
		} catch (Exception e) {
			invalidPacket(c, "exception during processing", packetType, packetSize, false, e);
			e.printStackTrace(System.err);
		}
	}

	private static void invalidPacket(Player c, String message, int packetType, int packetSize, boolean logout, Exception exception) {
		String previous = Misc.reverse(c.getPreviousPackets()).toString();
		if (exception == null) {
			logger.error("{} [player={}, opcode={}, size={}, previous={}]", message, c.getStateDescription(), packetType, packetSize, previous);
		} else {
			logger.error("Exception during packet handling: {} [player={}, opcode={}, size={}, previous={}]", message, c.getStateDescription(), packetType, packetSize, previous, exception);
		}

		if (logout) {
			c.forceLogout();
		}
	}

	public static int getPacketSize(int opcode) {
		if (opcode < 0 || opcode >= PACKET_SIZES.length)
			return OPCODE_OUT_OF_RANGE_SIZE;
		return PACKET_SIZES[opcode];
	}

	public static final int[] PACKET_SIZES = {
			0, 0, 0, 1, -1, 3, 0, 0, 4, 0, //0 - 9
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0, //10 - 19
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, //20 - 29
			0, 0, 0, 0, 0, 10, 4, 0, 0, 2, //30 - 39
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, //40 - 49
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, //50 - 59
			-1, 8, 0, 0, 0, 0, 0, 0, 0, 0, //60 - 69
			8, 0, 2, 2, 8, 6, 0, -1, 0, 6, //70 - 79
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, //80 - 89
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, //90 - 99
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, //100 - 109
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, //110 - 119
			1, 0, 6, 0, 16, 0, -1, -1, 2, 6, //120 - 129
			0, 4, 8, 8, 8, 6, 0, 2, 2, 2, //130 - 139
			6, 10, -1, 0, 0, 6, 0, 0, 0, 0, //140 - 149
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, //150 - 159
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, //160 - 169
			0, 0, 0, 0, 0, 0, 0, 2, 0, 0, //170 - 179
			0, 8, 0, 3, 2, 2, 2, 0, 8, 1, //180 - 189
			0, 0, 14, 0, 0, 0, 0, 0, 0, 1, //190 - 199
			2, 1, 0, 0, 0, 0, 0, 0, 4, 2, //200 - 209
			4, 0, 0, 4, 7, 8, 0, 0, 10, 0, //210 - 219
			0, 0, 0, 0, 0, 0, -1, 0, 8, 0, //220 - 229
			1, 0, 4, 0, 8, 0, 6, 8, 1, 0, //230 - 239
			0, 4, 9, 4, 0, 0, -1, 0, -1, 4, //240 - 249
			0, 0, 8, 6, 0, 0, 0, //250 - 255
	};

	static {
		SilentPacket u = new SilentPacket();
		packetId[3] = u;
		packetId[202] = u;
		packetId[77] = u;
		packetId[86] = u;
		packetId[78] = u;
		packetId[36] = u;
		packetId[226] = u;
		packetId[246] = u;
		packetId[148] = u;
		packetId[183] = u;
		packetId[230] = u;
		packetId[136] = u;
		packetId[189] = u;
		packetId[152] = u;
		packetId[200] = u;
		packetId[85] = u;
		packetId[165] = u;
		packetId[238] = u;
		packetId[150] = u;
		packetId[74] = u;
		packetId[34] = u;
		packetId[68] = u;
		packetId[79] = u;
		packetId[140] = u;
		// packetId[18] = u;
		packetId[223] = u;
		packetId[AutocastSpell.PACKET_OPCODE] = new AutocastSpell();
		packetId[142] = new InputField();
		packetId[253] = new ItemOptionTwoGroundItem();
		packetId[218] = new Report();
		packetId[40] = new Dialogue();
		packetId[232] = new OperateItem();
		ClickObject co = new ClickObject();
		packetId[234] = co; // Object option 4
		packetId[132] = co;
		packetId[252] = co;
		packetId[70] = co;
		packetId[228] = co;
		packetId[57] = new ItemOnNpc();
		ClickNPC cn = new ClickNPC();
		packetId[72] = cn;
		packetId[131] = cn;
		packetId[155] = cn;
		packetId[17] = cn;
		packetId[21] = cn;
		packetId[18] = cn;
		packetId[124] = new SelectItemOnInterface();
		packetId[16] = new ItemOptionTwo();
		packetId[75] = new ItemOptionThree();
		packetId[122] = new ItemOptionOne();
		packetId[241] = new ClickingInGame();
		packetId[4] = new Chat();
		packetId[236] = new PickupItem();
		packetId[87] = new DropItem();
		packetId[185] = new ClickingButtons();
		packetId[ClickingButtonsNew.CLICKING_BUTTONS_NEW] = new ClickingButtonsNew();
		packetId[130] = new CloseInterfaces();
		packetId[103] = new Commands();
		packetId[237] = new MagicOnItems();
		packetId[181] = new MagicOnFloorItems();
		packetId[202] = new IdleLogout();
		AttackPlayer ap = new AttackPlayer();
		packetId[73] = ap;
		packetId[249] = ap;
		PlayerOptionsHandler poHandler = new PlayerOptionsHandler();
		packetId[128] = poHandler;
		packetId[138] = poHandler;
		packetId[39] = new Trade();
		packetId[139] = new FollowPlayer();
		packetId[ExaminePacketHandler.EXAMINE_ITEM] = new ExaminePacketHandler();
		packetId[ExaminePacketHandler.EXAMINE_NPC] = new ExaminePacketHandler();
		packetId[41] = new WearItem();
		packetId[145] = new ContainerAction1();
		packetId[117] = new ContainerAction2();
		packetId[43] = new ContainerAction3();
		packetId[129] = new ContainerAction4();
		packetId[135] = new ContainerAction5();
		packetId[141] = new ContainerAction6();
		packetId[140] = new ContainerAction7();
		packetId[208] = new EnterAmountInput();

		MoveItems moveItems = new MoveItems();
		packetId[MoveItems.MOVE_ITEMS_IN_SAME_CONTAINER] = moveItems;
		packetId[MoveItems.MOVE_ITEMS_BETWEEN_CONTAINERS] = moveItems;
		packetId[MoveItems.MOVE_FROM_SEARCH_TO_TAB] = moveItems;

		packetId[101] = new ChangeAppearance();
		PrivateMessaging pm = new PrivateMessaging();
		packetId[188] = pm;
		packetId[126] = pm;
		packetId[215] = pm;
		packetId[74] = pm;
		packetId[95] = pm;
		packetId[133] = pm;

		Walking w = new Walking();
		packetId[98] = w;
		packetId[164] = w;
		packetId[248] = w;
		packetId[53] = new ItemOnItem();
		packetId[192] = new ItemOnObject();
		packetId[25] = new ItemOptionOneGroundItem();
		ChangeRegions cr = new ChangeRegions();
		packetId[60] = new EnterStringInput();
		packetId[127] = new ReceiveString();
		packetId[213] = new InterfaceAction();
		packetId[14] = new ItemOnPlayer();
		packetId[121] = new MapRegionFinish();
		packetId[210] = new MapRegionChange();
		packetId[35] = new MagicOnObject();
		MouseMovement ye = new MouseMovement();
		packetId[187] = ye;
		packetId[199] = new BroadcastHandler();
		packetId[201] = new KeyboardShortcutAction();
		packetId[177] = new ClickableLinkHandler();
		packetId[209] = new SpecialAttackHandler();
	}

//	public static void main(String...args) {
//		int last = 0;
//		for (int index = 0; index < PACKET_SIZES.length; index++) {
//			if (index % 10 == 0 && index != 0) {
//				System.out.print("//" + last + " - " + (index - 1));
//				System.out.println();
//				last = index;
//			}
//			System.out.print(PACKET_SIZES[index] + ", ");
//			if (index == PACKET_SIZES.length - 1) {
//				System.out.print("//" + last + " - " + (index - 1));
//			}
//		}
//	}
}