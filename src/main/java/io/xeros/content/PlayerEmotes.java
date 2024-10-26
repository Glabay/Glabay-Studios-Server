package io.xeros.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;

/**
 * Performing player available animations
 * @author Matt
 */

public class PlayerEmotes {
	
	/**
	 * Checks wether or not a player is able to perform an animation
	 * @param player
	 */
	public static boolean canPerform(final Player player) {
		if (player.underAttackByPlayer > 0 || player.underAttackByNpc > 0 || player.getPosition().inDuelArena() || player.getPosition().inPcGame()
				|| player.getPosition().inPcBoat() || player.getPosition().isInJail() || player.getInterfaceEvent().isActive()
				|| player.getPA().viewingOtherBank || player.isDead || player.viewingRunePouch) {
			return false;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return false;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			player.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		if (player.isStuck) {
			player.isStuck = false;
			player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return false;
		}
		
		return true;
	}

	public enum PLAYER_ANIMATION_DATA {
		YES(168, 855, -1),
		NO(169, 856, -1),
		BOW(164, 858, -1),
		ANGRY(165, 859, -1),
		THINK(162, 857, -1),
		WAVE(163, 863, -1),
		SHRUG(52058, 2113, -1),
		CHEER(171, 862, -1),
		BECKON(167, 864, -1),
		LAUGH(170, 861, -1),
		JUMP_FOR_JOY(52054, 2109, -1),
		YAWN(52056, 2111, -1),
		DANCE(166, 866, -1),
		JIG(52051, 2106, -1),
		SPIN(52052, 2107, -1),
		HEADBANG(52053, 2108, -1),
		CRY(161, 860, -1),
		BLOW_KISS(43092, 0x558, 574),
		PANIC(52050, 2105, -1),
		RASPBERRY(52055, 2110, -1),
		CLAP(172, 865, -1),
		SALUTE(52057, 2112, -1),
		GOBLIN_BOW(52071, 0x84F, -1),
		GOBLIN_SALUTE(52072, 0x850, -1),
		GLASS_BOX(2155, 0x46B, -1),
		CLIMB_ROPE(25103, 0x46A, -1),
		LEAN_ON_AIR(25106, 0x469, -1),
		GLASS_WALL(2154, 0x468, -1),
		ZOMBIE_WALK(72032, 3544, -1),
		ZOMBIE_DANCE(72033, 3543, -1),
		SCARED(59062, 2836, -1),
		RABBIT_HOP(72254, 6111, -1);
		
		private final int button;
		private final int animation;
		private final int graphic;
		
		PLAYER_ANIMATION_DATA(int button, int animation, int graphic) {
			this.button = button;
			this.animation = animation;
			this.graphic = graphic;
		}
		
		public int getButton() {
			return button;
		}

		public int getAnimation() {
			return animation;
		}

		public int getGraphic() {
			return graphic;
		}


	}

	public static void performEmote(final Player player, int button) {
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		if (!canPerform(player)) {
			return;
		}
		if (Boundary.isIn(player, Boundary.RAIDS_LOBBY) || Boundary.isIn(player, Boundary.RAIDS_LOBBY_ENTRANCE) || Boundary.isIn(player, Boundary.RAIDS_LOBBY)
				|| Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY)) {
			return;
		}
		for (PLAYER_ANIMATION_DATA animation : PLAYER_ANIMATION_DATA.values()) {
			String name = animation.name().toLowerCase().replaceAll("_", " ");
			if (animation.getButton() == button) {
				if (System.currentTimeMillis() - player.lastPerformedEmote < 3500)
					return;			
				if (animation.getButton() == 166) {
					if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
						player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.BECOME_A_DANCER);
					}
					if (Boundary.isIn(player, Boundary.KARAMJA_BOUNDARY)) {
						player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.DANCE_75_TIMES);
					}
					if (player.getItems().isWearingItem(10394, Player.playerLegs)) {
						player.startAnimation(5316);
						return;
					}
				}
				player.startAnimation(animation.getAnimation());
				player.gfx0(animation.getGraphic());
				player.lastPerformedEmote = System.currentTimeMillis();
				player.sendMessage("Performing emote: " + name + ".");
				player.stopMovement();
			}
		}
	}
	
	public enum SKILLCAPE_ANIMATION_DATA {
		ATTACK_CAPE(new int[] { 9747, 9748, 33033 }, 4959, 823, 7),
		DEFENCE_CAPE(new int[] { 9753, 9754, 33034 }, 4961, 824, 10),
		STRENGTH_CAPE(new int[] { 9750, 9751, 33035 }, 4981, 828, 25),
		HITPOINTS_CAPE(new int[] { 9768, 9769, 33036 }, 4971, 833, 12),
		RANGING_CAPE(new int[] { 9756, 9757, 33037 }, 4973, 832, 12),
		PRAYER_CAPE(new int[] { 9759, 9760, 33038 }, 4979, 829, 15),
		MAGIC_CAPE(new int[] { 9762, 9763, 33039 }, 4939, 813, 6),
		COOKING_CAPE(new int[] { 9801, 9802, 33040 }, 4955, 821, 36),
		WOODCUTTING_CAPE(new int[] { 9807, 9808, 33041 }, 4957, 822, 25),
		FLETCHING_CAPE(new int[] { 9783, 9784, 33042 }, 4937, 812, 20),
		FISHING_CAPE(new int[] { 9798, 9799, 33043 }, 4951, 819, 19),
		FIREMAKING_CAPE(new int[] { 9804, 9805, 33044 }, 4975, 831, 14),
		CRAFTING_CAPE(new int[] { 9780, 9781, 33045 }, 4949, 818, 15),
		SMITHING_CAPE(new int[] { 9795, 9796, 33046 }, 4943, 815, 23),
		MINING_CAPE(new int[] { 9792, 9793, 33047 }, 4941, 814, 8),
		HERBLORE_CAPE(new int[] { 9774, 9775, 33048 }, 4969, 835, 16),
		AGILITY_CAPE(new int[] { 9771, 9772, 33049 }, 4977, 830, 8),
		THIEVING_CAPE(new int[] { 9777, 9778, 33050 }, 4965, 826, 16),
		SLAYER_CAPE(new int[] { 9786, 9787,33051 }, 4967, 827, 8),
		FARMING_CAPE(new int[] { 9810, 9811, 33052 }, 4963, 825, 16),
		RUNECRAFTING_CAPE(new int[] { 9765, 9766, 33053 }, 4947, 817, 10),
		HUNTER_CAPE(new int[] { 9948, 9949, 33054 }, 5158, 907, 14),
		CONSTRUCTION_CAPE(new int[] { 9789, 9790}, 4953, 820, 16),
		QUEST_CAPE(new int[] { 9813 }, 4945, 816, 19),
		MAX_CAPE(new int[] {13280, 13329, 13337, 13331, 13333, 13335, 20760, 21898, 21285, 21776, 21780, 21784 }, 7121, 1286, 35),
		ACHIEVEMENT_CAPE(new int[] { 13069 }, 2709, 323, 35);
	
		
		private final GameItem[] cape;
		private final int animation;
		private final int graphic;
		private final int delay;
		
		SKILLCAPE_ANIMATION_DATA(int[] skillcape, int animation, int graphic, int delay) {
			cape = new GameItem[skillcape.length];
			for (int i = 0; i < skillcape.length; i++) {
				cape[i] = new GameItem(skillcape[i]);
			}
			this.animation = animation;
			this.graphic = graphic;
			this.delay = delay;
		}
		
		private static final Map<Integer, SKILLCAPE_ANIMATION_DATA> SKILLCAPE_DATA = new HashMap<Integer, SKILLCAPE_ANIMATION_DATA>();

		static {
			for (SKILLCAPE_ANIMATION_DATA animations : values()) {
				for (GameItem item : animations.cape) {
					SKILLCAPE_DATA.put(item.id(), animations);
				}
			}
		}
	}

	public static void performSkillcapeAnimation(final Player player, final GameItem skillcape) {
		if (!canPerform(player)) {
			return;
		}
		GameItem cape = skillcape;
		SKILLCAPE_ANIMATION_DATA data = SKILLCAPE_ANIMATION_DATA.SKILLCAPE_DATA.get(cape.id());
		if (data != null) {
			String name = data.name().toLowerCase().replaceAll("_", " ");
			if (System.currentTimeMillis() - player.lastPerformedEmote < data.delay * 500)
				return;
			if (name.contains("max")) {
				if (Boundary.isIn(player, Boundary.DRAYNOR_BOUNDARY)) {
					player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.MAX_EMOTE);
				}
			}
			player.startAnimation(data.animation);
			player.gfx0(data.graphic);
			player.lastPerformedEmote = System.currentTimeMillis();
			player.sendMessage("Performing emote: " + name + ".");
			player.stopMovement();
		} else {
			player.sendMessage("You must be wearing a skillcape in order to do this emote.");
			return;
		}
	}
}
