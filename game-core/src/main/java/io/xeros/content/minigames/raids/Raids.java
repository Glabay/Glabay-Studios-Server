package io.xeros.content.minigames.raids;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Raids {

    private static final Logger logger = LoggerFactory.getLogger(Raids.class);

    private static final String RAIDS_DAMAGE_ATTRIBUTE_KEY = "cox_damage";
    private static final int RAIDS_DAMAGE_FOR_REWARD = 550;

    public static int COMMON_KEY = 3456;
    public static int RARE_KEY = 3464;
    public long lastActivity = -1;
    private final Map<String, Long> playerLeftAt = Maps.newConcurrentMap();
    private final Map<String, Integer> raidPlayers = Maps.newConcurrentMap();
    private final Map<String, Integer> activeRoom = Maps.newConcurrentMap();
    private List<RaidsRank> ranks = null;
    private int groupPoints;

    public static boolean isMissingRequirements(Player c) {
        if (c.totalLevel < c.getMode().getTotalLevelNeededForRaids()) {
            c.sendMessage("You need a total level of at least " + c.getMode().getTotalLevelNeededForRaids() + " to join this raid!");
            return true;
        }

        return false;
    }

    public void filterPlayers() {
        raidPlayers.entrySet().stream().filter(entry -> !PlayerHandler.getOptionalPlayerByLoginName(entry.getKey()).isPresent()).forEach(entry -> raidPlayers.remove(entry.getKey()));
    }

    public void removePlayer(Player player) {
        raidPlayers.remove(player.getLoginNameLower());
        groupPoints = raidPlayers.entrySet().stream().mapToInt(val -> val.getValue()).sum();
        if (raidPlayers.isEmpty()) {
            lastActivity = System.currentTimeMillis();
        }
    }

    public List<Player> getPlayers() {
        List<Player> activePlayers = Lists.newArrayList();
        filterPlayers();
        raidPlayers.keySet().stream().forEach(playerName -> {
            PlayerHandler.getOptionalPlayerByLoginName(playerName).ifPresent(player -> activePlayers.add(player));
        });
        return activePlayers;
    }

    /**
     * Add points
     */
    public int addPoints(Player player, int points) {
        if (!raidPlayers.containsKey(player.getLoginNameLower())) return 0;
        int currentPoints = raidPlayers.getOrDefault(player.getLoginNameLower(), 0);
        raidPlayers.put(player.getLoginNameLower(), currentPoints + points);
        groupPoints = raidPlayers.entrySet().stream().mapToInt(val -> val.getValue()).sum();
        return currentPoints + points;
    }

    public int currentHeight;
    /**
     * The current path
     */
    private int path;
    /**
     * The current way
     */
    private int way;
    /**
     * Current room
     */
    public int currentRoom;
    private boolean chestRoomDoorOpen = true;
    private final int chestToOpenTheDoor = 5 + Misc.random(20);
    private final HashSet<Integer> chestRoomChestsSearched = new HashSet<>();
//	/**
//	 * Instance;
//	 */
    //private InstancedArea instance = null;
//
    /**
     * Monster spawns (No Double Spawning)
     */
    public boolean lizards;
    public boolean vasa;
    public boolean vanguard;
    public boolean ice;
    public boolean chest;
    public boolean mystic;
    public boolean tekton;
    public boolean mutta;
    public boolean archers;
    public boolean olm;
    public boolean olmDead;
    public boolean rightHand;
    public boolean leftHand;
    /**
     * The door location of the current paths
     */
    private final ArrayList<Location> roomPaths = new ArrayList<Location>();
    /**
     * The names of the current rooms in path
     */
    private final ArrayList<String> roomNames = new ArrayList<String>();
    /**
     * Current monsters needed to kill
     */
    private int mobAmount;

    /**
     * Gets the start location for the path
     * @return path
     */
    public Location getStartLocation() {
        switch (path) {
        case 0: 
            return RaidRooms.STARTING_ROOM_2.doorLocation;
        }
        return RaidRooms.STARTING_ROOM.doorLocation;
    }

    public Location getOlmWaitLocation() {
        switch (path) {
        case 0: 
            return RaidRooms.ENERGY_ROOM.doorLocation;
        }
        return RaidRooms.ENERGY_ROOM_2.doorLocation;
    }


    /**
     * Handles raid rooms
     * @author Goon
     */
    public enum RaidRooms {
        STARTING_ROOM("start_room", 1, 0, new Location(3299, 5189)), LIZARDMEN_SHAMANS("lizardmen", 1, 0, new Location(3308, 5208)), SKELETAL_MYSTIC("skeletal", 1, 0, new Location(3312, 5217, 1)), VASA_NISTIRIO("vasa", 1, 0, new Location(3312, 5279)), VANGUARDS("vanguard", 1, 0, new Location(3312, 5311)), ICE_DEMON("ice", 1, 0, new Location(3313, 5346)), CHEST_ROOM("chest", 1, 0, new Location(3311, 5374)), 
        //SCAVENGER_ROOM_2("scavenger",1,new Location(3343,5217,1)),
        //ARCHERS_AND_MAGERS("archer",1,0,new Location(3309,5340,1)),
        MUTTADILE("muttadile", 1, 0, new Location(3311, 5309, 1)), TEKTON("tekton", 1, 0, new Location(3310, 5277, 1)), ENERGY_ROOM("energy", 1, 0, new Location(3275, 5159)), OLM_ROOM_WAIT("olm_wait", 1, 0, new Location(3232, 5721)), OLM_ROOM("olm", 1, 0, new Location(3232, 5730)), STARTING_ROOM_2("start_room", 1, 1, new Location(3299, 5189)), MUTTADILE_2("muttadile", 1, 1, new Location(3311, 5309, 1)), VASA_NISTIRIO_2("vasa", 1, 1, new Location(3312, 5279)), VANGUARDS_2("vanguard", 1, 1, new Location(3312, 5311)), ICE_DEMON_2("ice", 1, 1, new Location(3313, 5346)), 
        //ARCHERS_AND_MAGERS_2("archer",1,1,new Location(3309,5340,1)),
        CHEST_ROOM_2("chest", 1, 1, new Location(3311, 5374)), 
        //SCAVENGER_ROOM_2("scavenger",1,new Location(3343,5217,1)),
        SKELETAL_MYSTIC_2("skeletal", 1, 1, new Location(3312, 5217, 1)), TEKTON_2("tekton", 1, 1, new Location(3310, 5277, 1)), LIZARDMEN_SHAMANS_2("lizardmen", 1, 1, new Location(3308, 5208)), ENERGY_ROOM_2("energy", 1, 1, new Location(3275, 5159)), OLM_ROOM_WAIT_2("olm_wait", 1, 1, new Location(3232, 5721)), OLM_ROOM_2("olm", 1, 1, new Location(3232, 5730));
        private final Location doorLocation;
        private final int path;
        private final int way;
        private final String roomName;

        RaidRooms(String name, int path1, int way1, Location door) {
            doorLocation = door;
            roomName = name;
            path = path1;
            way = way1;
        }

        public Location getDoor() {
            return doorLocation;
        }

        public int getPath() {
            return path;
        }

        public int getWay() {
            return way;
        }

        public String getRoomName() {
            return roomName;
        }
    }

    /**
     * Starts the raid.
     */
    public void startRaid(List<Player> players, boolean party) {
        //Initializes the raid
        currentHeight = RaidConstants.currentRaidHeight;
        RaidConstants.currentRaidHeight += 4;
        path = 1;
        way = Misc.random(1);
        for (RaidRooms room : RaidRooms.values()) {
            if (room.getWay() == way) {
                roomNames.add(room.getRoomName());
                roomPaths.add(room.getDoor());
            }
        }
        for (Player lobbyPlayer : players) {
            if (!party) {
                //gets all players in lobby
                if (lobbyPlayer == null) continue;
                if (!lobbyPlayer.getPosition().inRaidLobby()) {
                    lobbyPlayer.sendMessage("You were not in the lobby you have been removed from the raid queue.");
                    continue;
                }
            }
            lobbyPlayer.getPA().closeAllWindows();
            raidPlayers.put(lobbyPlayer.getLoginNameLower(), 0);
            activeRoom.put(lobbyPlayer.getLoginNameLower(), 0);
            lobbyPlayer.setRaidsInstance(this);
            //lobbyPlayer.setInstance(instance);
            lobbyPlayer.getPA().movePlayer(getStartLocation().getX(), getStartLocation().getY(), currentHeight);
            lobbyPlayer.sendMessage("@red@The raid has now started! Good Luck! type ::leaveraid to leave!");
            lobbyPlayer.sendMessage("[TEMP] @blu@If you get stuck in a wall, type ::stuckraids to be sent back to room 1!");
        }
        RaidConstants.raidGames.add(this);
    }

    public boolean hadPlayer(Player player) {
        long leftAt = playerLeftAt.getOrDefault(player.getLoginNameLower(), (long) -1);
        return leftAt > 0;
    }

    public boolean login(Player player) {
        long leftAt = playerLeftAt.getOrDefault(player.getLoginNameLower(), (long) -1);
        if (leftAt > 0) {
            playerLeftAt.remove(player.getLoginNameLower());
            if (System.currentTimeMillis() - leftAt <= 60000) {
                raidPlayers.put(player.getLoginNameLower(), 0);
                player.setRaidsInstance(this);
                player.sendMessage("@red@You rejoin the raid!");
                lastActivity = -1;
                return true;
            }
        }
        return false;
    }

    public void logout(Player player) {
        player.setRaidsInstance(null);
        removePlayer(player);
        playerLeftAt.put(player.getLoginNameLower(), System.currentTimeMillis());
    }

    public void resetOlmRoom(Player player) {
        this.activeRoom.put(player.getLoginNameLower(), 9);
    }

    public void resetRoom(Player player) {
        this.activeRoom.put(player.getLoginNameLower(), 0);
    }

    /**
     * Kill all spawns for the raid leader if left
     */
    public void killAllSpawns() {
        NPCHandler.kill(currentHeight, currentHeight + 3, 394, 3341, 7563, 7566, 7585, 7560, 7544, 7573, 7604, 7606, 7605, 7559, 7527, 7528, 7529, 7553, 7554, 7555);
    }

    /**
     * Leaves the raid.
     * @param player
     */
    public void leaveGame(Player player) {
        if (System.currentTimeMillis() - player.infernoLeaveTimer < 15000) {
            player.sendMessage("You cannot leave yet, wait a couple of seconds and try again.");
            return;
        }
        player.sendMessage("@red@You have left the Chambers of Xeric.");
        player.getPA().movePlayer(3034, 6067, 0);
        player.setRaidsInstance(null);
        //player.setInstance(null);
        removePlayer(player);
        player.specRestore = 120;
        player.specAmount = 10.0;
        player.setRunEnergy(100, true);
        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
        player.getPA().refreshSkill(Player.playerPrayer);
        player.getHealth().removeAllStatuses();
        player.getHealth().reset();
        player.getPA().refreshSkill(5);
    }

    public static List<RaidsRank> buildRankList(List<RaidsRank> ranks) {
        ranks.sort(Comparator.comparingInt(it -> it.damage));
        ranks = Lists.reverse(ranks);
        for (int index = 0; index < ranks.size(); index++) {
            ranks.get(index).rank = index + 1;
        }

        return ranks;
    }

    /**
     * Handles giving the raid reward
     */
    public void giveReward(Player player, Boolean kronosReward) {
        if (ranks == null) {
           ranks = buildRankList(getPlayers().stream().map(it -> new RaidsRank(it, getDamage(it))).collect(Collectors.toList()));
           logger.debug("Ranks {}", ranks);
        }

        if (getDamage(player) < RAIDS_DAMAGE_FOR_REWARD) {
            player.sendMessage("@red@You didn't do enough damage to earn a reward, you must do at least " + RAIDS_DAMAGE_FOR_REWARD + " damage!");
            return;
        }

        int myRank;
        Optional<RaidsRank> rank = ranks.stream().filter(it -> it.player.equals(player)).findFirst();
        if (rank.isEmpty()) {
            myRank = 100;
            logger.error("No rank for player {}", player);
        } else {
            myRank = rank.get().rank;
        }

        logger.debug("Rank for {} is {}", player.getDisplayName(), myRank);
        player.sendMessage("[@pur@RAIDS@bla@] Your place was @pur@#" + myRank + " @bla@with @red@" + getDamage(player) + "@bla@ damage done.");
        if (myRank > 5) {
            myRank = myRank + 4;
        }
        if (myRank > 24) {
            myRank = 24;
        }

        int chance = Misc.random(1000);
        int rareChance = 975 + myRank;
        if (player.getItems().playerHasItem(21046)) {
            rareChance = 971 + myRank;
            player.getItems().deleteItem(21046, 1);
            player.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate.");
            player.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        if (chance >= 0 && chance < rareChance) {
            player.getItems().addItemUnderAnyCircumstance(COMMON_KEY, 1);
            player.getEventCalendar().progress(EventChallenge.COMPLETE_X_RAIDS);
            LeaderboardUtils.addCount(LeaderboardType.COX, player, 1);
            Achievements.increase(player, AchievementType.COX, 1);
            player.sendMessage("@red@You have just received a @bla@Common Key.");
        } else if (chance >= rareChance) {
            player.getItems().addItemUnderAnyCircumstance(RARE_KEY, 1);
            player.getEventCalendar().progress(EventChallenge.COMPLETE_X_RAIDS);
            LeaderboardUtils.addCount(LeaderboardType.COX, player, 1);
            Achievements.increase(player, AchievementType.COX, 1);
            player.sendMessage("@red@You have just received a @pur@Rare Key.");
            PlayerHandler.executeGlobalMessage("@bla@[@blu@RAIDS@bla@] " + player.getDisplayName() + "@pur@ has just received a @bla@Rare Raids Key!");
        }
        if (!kronosReward) {
            if (player.raidCount == 25) {
                player.getItems().addItemUnderAnyCircumstance(22388, 1);
                PlayerHandler.executeGlobalMessage("@blu@[@pur@" + player.getDisplayName() + "@blu@] has completed 25 Raids and obtained the Xeric\'s Guard Cape!");
            }
            if (player.raidCount == 50) {
                player.getItems().addItemUnderAnyCircumstance(22390, 1);
                PlayerHandler.executeGlobalMessage("@blu@[@pur@" + player.getDisplayName() + "@blu@] has completed 50 Raids and obtained the Xeric\'s Warrior Cape!");
            }
            if (player.raidCount == 100) {
                player.getItems().addItemUnderAnyCircumstance(22392, 1);
                PlayerHandler.executeGlobalMessage("@blu@[@pur@" + player.getDisplayName() + "@blu@] has completed 100 Raids and obtained the Xeric\'s Sentinel Cape!");
            }
            if (player.raidCount == 250) {
                player.getItems().addItemUnderAnyCircumstance(22394, 1);
                PlayerHandler.executeGlobalMessage("@blu@[@pur@" + player.getDisplayName() + "@blu@] has completed 250 Raids and obtained the Xeric\'s General Cape!");
            }
            if (player.raidCount == 500) {
                player.getItems().addItemUnderAnyCircumstance(22396, 1);
                PlayerHandler.executeGlobalMessage("@blu@[@pur@" + player.getDisplayName() + "@blu@] has completed 500 Raids and obtained the Xeric\'s Champions Cape!");
            }
        }
    }

    final int OLM = 7554;
    final int OLM_RIGHT_HAND = 7553;
    final int OLM_LEFT_HAND = 7555;

    public void handleMobDeath(Player killer, int npcType) {
        mobAmount -= 1;
        switch (npcType) {
        case OLM: 
            /*
			 * Crystal & Olm removal after olm's death
			 */
            olmDead = true;
            //idk
//			Server.getGlobalObjects().add(new GlobalObject(-1, 3233, 5751, currentHeight, 3, 10).setInstance(instance));
//			Server.getGlobalObjects().add(new GlobalObject(-1, 3232, 5749, currentHeight, 3, 10).setInstance(instance));
//			Server.getGlobalObjects().add(new GlobalObject(-1, 3232, 5750, currentHeight, 3, 10).setInstance(instance));
//			Server.getGlobalObjects().add(new GlobalObject(-1, 3233, 5749, currentHeight, 3, 10).setInstance(instance));
//			Server.getGlobalObjects().add(new GlobalObject(-1, 3233, 5750, currentHeight, 3, 10).setInstance(instance));
//			Server.getGlobalObjects().add(new GlobalObject(-1, 3233, 5750, currentHeight, 3, 10).setInstance(instance));
//			Server.getGlobalObjects().remove(new GlobalObject(29881, 3220, 5738, currentHeight, 3, 10).setInstance(instance));

			getPlayers().stream().forEach(player -> {
				player.getPA().sendPlayerObjectAnimation(3220, 5738, 7348, 10, 3);
				player.sendMessage("@red@Congratulations you have defeated The Great Olm and completed the raid!");
				player.sendMessage("@red@Please go up the stairs beyond the Crystals to get your reward " );
			});
			return;

		case OLM_RIGHT_HAND:
			rightHand = true;
			if(leftHand == true) {
				getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable."));
				Server.getGlobalObjects().add(new GlobalObject(29888, 3220, 5733, currentHeight, 3, 10));
			}else {
				getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!"));
			}
			//Server.getGlobalObjects().remove(new GlobalObject(29887, 3220, 5733, currentHeight, 3, 10).setInstance(instance));

			//Server.getGlobalObjects().add(new GlobalObject(29888, 3220, 5733, currentHeight, 3, 10).setInstance(instance));
			getPlayers().stream()
			.forEach(otherPlr -> {
				otherPlr.getPA().sendPlayerObjectAnimation(3220, 5733, 7352, 10, 3);
				if(leftHand) {
					otherPlr.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable.");
				} else {
					otherPlr.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!");
				}
			});
		
			return;
		case OLM_LEFT_HAND:
			leftHand = true;
			Server.getGlobalObjects().remove(new GlobalObject(29884, 3220, 5743, currentHeight, 3, 10));
			Server.getGlobalObjects().add(new GlobalObject(29885, 3220, 5743, currentHeight, 3, 10));
			getPlayers().stream()
			.forEach(otherPlr -> {
				otherPlr.getPA().sendPlayerObjectAnimation(3220, 5743, 7360, 10, 3);
				if(rightHand) {
					otherPlr.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable.");
				} else {
					otherPlr.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!");
				}
			
			});
			if(rightHand == true) {
				Server.getGlobalObjects().remove(new GlobalObject(29884, 3220, 5743, currentHeight, 3, 10));
				Server.getGlobalObjects().add(new GlobalObject(29885, 3220, 5743, currentHeight, 3, 10));
				getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable."));
			}else {
				getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!"));
			}
			return;
		}
		if(killer != null) {
			int randomPoints = Misc.random(500);
			int newPoints = addPoints(killer, randomPoints);
		
			killer.sendMessage("@red@You receive "+ randomPoints +" points from killing this monster.");
			killer.sendMessage("@red@You now have "+ newPoints +" points.");
		}
		if(mobAmount <= 0) {
			getPlayers().stream().forEach(player ->	player.sendMessage("@red@The room has been cleared and you are free to pass."));
			roomSpawned = false;
		}else {
			getPlayers().stream().forEach(player ->	player.sendMessage("@red@There are "+ mobAmount+" enemies remaining."));
		}
	}

	public void spawnRaidsNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence, boolean attackPlayer) {
		int[] stats = getScaledStats(HP, attack, defence, getPlayers().size());
		attack = stats[0];
        defence = stats[1];
        HP = stats[2];
		NPC npc = NPCSpawning.spawn(npcType, x, y, heightLevel, WalkingType, maxHit, attackPlayer,
				NpcStats.builder().setAttackLevel(attack).setHitpoints(HP).setDefenceLevel(defence).createNpcStats());
		npc.setRaidsInstance(this);
		npc.getBehaviour().setRespawn(false);
		//System.out.println(modifier + " | "  + lowModifier);
	}

	public static int[] getScaledStats(int HP, int attack, int defence, int groupsize) {
        int modifier = 0;
        int attackScale =1;
        int defenceScale =1;
        int baseMod = 100;
        int baseLowMod = 35;
        if (groupsize > 1) {
            if (HP < 200) {
                baseMod = 10;
            }
            modifier = (baseMod + (groupsize * (int) (HP * 0.15))); // groupsize:modifier | 1:1 | 2:1.8 | 3:2.2
            attackScale = (baseLowMod + (groupsize * 10)); // groupsize:modifier | 1:1 | 2:1.2 | 3:1.3
            defenceScale = (baseLowMod + (groupsize * 10)); // groupsize:modifier | 1:1 | 2:1.2 | 3:1.3
        }
        defence = (defence + defenceScale);
        HP = (HP + modifier);
        attack = (attack + attackScale);

        return new int[] { attack, defence, HP };
    }

    public static final int LIZARDMAN_HP = 150;
    public static final int LIZARDMAN_ATTACK = 200;
    public static final int LIZARDMAN_DEFENCE = 100;

    public static final int VASA_HP = 400;
    public static final int VASA_ATTACK = 250;
    public static final int VASA_DEFENCE = 130;

    public static final int OLM_HP = 500;
    public static final int OLM_ATTACK = 272;
    public static final int OLM_DEFENCE = 350;

    /**
	 * Spawns npc for the current room
	 * @param currentRoom The room
	 */
	public void spawnNpcs(int currentRoom) {

		int height = currentHeight;
		switch(roomNames.get(currentRoom)) {
		case "lizardmen":
			if(lizards) {
				return;
			}
			if(path == 0) {

				spawnRaidsNpc(7573, 3274, 5262, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
				spawnRaidsNpc(7573, 3282, 5266, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
				spawnRaidsNpc(7573, 3275, 5269, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
			}else {
				spawnRaidsNpc(7573, 3307,5265, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
				spawnRaidsNpc(7573, 3314,5265, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
				spawnRaidsNpc(7573, 3314,5261, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
			}
			lizards = true;
			mobAmount+=3;
			break;
		case "vasa":
			if(vasa) {
				return;
			}
			
			if(path == 0) {
				spawnRaidsNpc(7566, 3280,5295, height, -1, VASA_HP, 25, VASA_ATTACK, VASA_DEFENCE,true);
			}else {
				spawnRaidsNpc(7566, 3311,5295, height, -1, VASA_HP, 25, VASA_ATTACK, VASA_DEFENCE,true);
			}
			vasa = true;
			mobAmount+=1;
			break;
		case "vanguard":
			if(vanguard) {
				return;
			}
			if(path == 0) {
				spawnRaidsNpc(7527, 3277,5326, height, -1, 170, 25, 140, 120,true);// melee vanguard
				spawnRaidsNpc(7528, 3277,5332, height, -1, 170, 25, 140, 120,true); // range vanguard
				spawnRaidsNpc(7529, 3285,5329, height, -1, 170, 25, 140, 120,true); // magic vanguard
			}else {
				spawnRaidsNpc(7527, 3310,5324, height, -1, 170, 25, 140, 120,true); // melee vanguard
				spawnRaidsNpc(7528, 3310,5331, height, -1, 170, 25, 140, 120,true); // range vanguard
				spawnRaidsNpc(7529, 3316,5331, height, -1, 170, 25, 140, 120,true);// magic vanguard
			}
			vanguard = true;
			mobAmount+=3;
			break;
		case "ice":
			if(ice) {
				return;
			}
			if(path == 0) {
				spawnRaidsNpc(7585, 3273,5365, height, -1, 500, 45, 350, 160,true);
			}else {
				spawnRaidsNpc(7585, 3310,5367, height, -1, 500, 45, 350, 160,true);
			}
			ice = true;
			mobAmount+=1;
			break;
		case "skeletal":
			if(mystic) {
				return;
			}
			if(path == 0) {
				spawnRaidsNpc(7604, 3279,5271, height+1, -1, 150, 25, 400, 150,true);
				spawnRaidsNpc(7605, 3290,5268, height+1, -1, 150, 25, 500, 150,true);
				spawnRaidsNpc(7606, 3279,5264, height+1, -1, 150, 25, 400, 150,true);
			}else {
				spawnRaidsNpc(7604, 3318,5262,height+1, -1, 180, 25, 400, 150,true);
				spawnRaidsNpc(7605, 3307,5258, height+1, -1, 180, 25, 500, 150,true);
				spawnRaidsNpc(7606, 3301,5262, height+1, -1, 180, 25, 400, 150,true);
			}
			mobAmount+=3;
			mystic = true;
			break;
		case "tekton":
			if(tekton) {
				return;
			}
			if(path == 0) {
				spawnRaidsNpc(7544, 3280,5295, height+1, -1, 550, 45, 450, 230,true);
			}else {
				spawnRaidsNpc(7544, 3310, 5293, height+1, -1, 550, 45, 450, 230,true);
			}
			mobAmount+=1;
			tekton = true;
			break;
		case "muttadile":
			if(mutta) {
				return;
			}
			if(path == 0) {
				spawnRaidsNpc(7563, 3276,5331, height + 1, 1, 300, 25, 400, 220,true);
			}else {
				spawnRaidsNpc(7563, 3308,5331, height + 1, 1, 300, 25, 400, 220,true);
			}
			mobAmount+=1;
			mutta = true;
			break;
		case "archer":
			if(archers) {
				return;
			}
			if(path == 0) {
				spawnRaidsNpc(7559, 3287,5364, height + 1, -1, 150, 25, 100, 100,true); // deathly ranger
				spawnRaidsNpc(7559, 3287,5363, height + 1, -1, 150, 25, 100, 100,true); // deathly ranger
				spawnRaidsNpc(7559, 3285,5363, height + 1, -1, 150, 30, 100, 100,true); // deathly ranger
				spawnRaidsNpc(7559, 3285,5364, height + 1, -1, 150, 30, 100, 100,true); // deathly ranger

				spawnRaidsNpc(7560, 3286,5369, height + 1, -1, 150, 25, 100, 100,true); // deathly mager
				spawnRaidsNpc(7560, 3284,5369, height + 1, -1, 150, 25, 100, 100,true); // deathly mager
				spawnRaidsNpc(7560, 3286,5370, height + 1, -1, 150, 30, 100, 100,true); // deathly mager
				spawnRaidsNpc(7560, 3284,5370, height + 1, -1, 150, 30, 100, 100,true); // deathly mager
			}else {
				spawnRaidsNpc(7559, 3319,5363, height + 1, -1, 150, 25, 100, 100,true); // deathly ranger
				spawnRaidsNpc(7559, 3317,5363, height + 1, -1, 150, 25, 100, 100,true); // deathly ranger
				spawnRaidsNpc(7559, 3317,5364, height + 1, -1, 150, 30, 100, 100,true); // deathly ranger
				spawnRaidsNpc(7559, 3319,5364, height + 1, -1, 150, 30, 100, 100,true); // deathly ranger

				spawnRaidsNpc(7560, 3318,5370, height + 1, -1, 150, 25, 100, 100,true); // deathly mager
				spawnRaidsNpc(7560, 3318,5369, height + 1, -1, 150, 25, 100, 100,true); // deathly mager
				spawnRaidsNpc(7560, 3316,5369, height + 1, -1, 150, 30, 100, 100,true); // deathly mager
				spawnRaidsNpc(7560, 3316,5370, height + 1, -1, 150, 30, 100, 100,true); // deathly mager
			}
			archers = true;
			mobAmount+=8;
			break;
		case "olm":
			if(olm) {
				return;
			}

			// TODO custom region object clipping for instances like this
			Server.getGlobalObjects().add(new GlobalObject(29884, 3220, 5743, currentHeight, 3, 10));
			Server.getGlobalObjects().add(new GlobalObject(29887, 3220, 5733, currentHeight, 3, 10));
			Server.getGlobalObjects().add(new GlobalObject(29881, 3220, 5738, currentHeight, 3, 10));
			getPlayers().stream()
			.forEach(otherPlr -> {
				otherPlr.getPA().sendPlayerObjectAnimation(3220, 5733, 7350, 10, 3);
				otherPlr.getPA().sendPlayerObjectAnimation(3220, 5743, 7354, 10, 3);
				otherPlr.getPA().sendPlayerObjectAnimation(3220, 5738, 7335, 10, 3);
			});
			spawnRaidsNpc(7553, 3223, 5733, height, -1, 200, 33, 272, 200,false); // left claw
			spawnRaidsNpc(7554, 3223, 5738, height, -1, OLM_HP, 33, OLM_ATTACK, OLM_DEFENCE,true); // olm head
			spawnRaidsNpc(7555, 3223, 5742, height, -1, 200, 33, 272, 200 ,false); // right claw

			olm = true;
			mobAmount+=3;
			break;
			default:
				roomSpawned = false;
				
				break;
		}
		
	}
	/**
	 * Handles object clicking for raid objects
	 * @param player The player
	 * @param objectId The object id
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean handleObjectClick(Player player, int objectId, int x, int y) {
		player.objectDistance = 3;
		switch(objectId) {
			// Searching chest to
			case 29742:
				if (chestRoomDoorOpen) {
					player.sendMessage("The room is already opened, no need for more searching.");
				} else {
					if (chestRoomChestsSearched.contains(Objects.hash(x, y))) {
						player.sendMessage("This chest has already been searched.");
					} else {
						player.startAnimation(6387);
						chestRoomChestsSearched.add(Objects.hash(x, y));
						player.sendMessage("You search the chest..");

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								if (chestRoomChestsSearched.size() == chestToOpenTheDoor) {
									player.sendMessage("You find a lever to open the door..");
									player.forcedChat("I found the lever!");
									getPlayers().forEach(plr -> plr.sendMessage("@red@The door has been opened."));
									chestRoomDoorOpen = true;
								} else {
									player.sendMessage("You find nothing.");
									player.startAnimation(65535);
								}

								container.stop();
							}
						}, 2);
					}
				}
				return true;
		case 29789://First entrance
			player.objectDistance = 3;
		case 29734:
			player.objectDistance = 3;
		case 29879:
			if (roomNames.get(getRoomForPlayer(player)).equalsIgnoreCase("chest") && !chestRoomDoorOpen) {
				player.sendMessage("This passage way is blocked, you must search the boxes to find the lever to open it.");
			} else {
				player.objectDistance = 3;
				nextRoom(player);
			}
			return true;
		case 30066:
			player.objectDistance = 3;
			return true;
		case 29777:
			player.objectDistance = 3;
		case 29778:
			player.objectDistance = 3;
			if(!olmDead) {
				if(player.objectX == 3298 && player.objectY == 5185) {
					player.getDH().sendDialogues(10000, -1);
					return true;
				}
				player.sendMessage("You need to complete the raid!");
				return true;
			}
			if (System.currentTimeMillis() - player.lastMysteryBox < 150 * 4) {
				return true;
			}
			player.objectDistance = 3;
			player.lastMysteryBox = System.currentTimeMillis();
			player.raidCount+=1;
			if (Boundary.isIn(player, Boundary.FULL_RAIDS)) {
				giveReward(player, false);
				if (Hespori.activeKronosSeed == true) {
                    giveReward(player, true);
                    player.sendMessage("@red@The @gre@Kronos seed@red@ doubles your chances!" );
                }
			}
            resetDamage(player);
			player.healEverything();
			player.sendMessage("@red@You receive your reward." );
			player.sendMessage("@red@You have completed "+player.raidCount+" raids." );
			leaveGame(player);
			break;

		case 30028:
			player.objectDistance = 3;
			player.getPA().showInterface(57000);

			return true;
		}
		return false;
	}
	
	private boolean roomSpawned;

	private int getRoomForPlayer(Player player) {
		return activeRoom.getOrDefault(player.getLoginNameLower(), 0);
	}

	/**
	 * Goes to the next room, Handles spawning etc.
	 */
	public void nextRoom(Player player) {
		player.objectDistance = 3;
		if(activeRoom.getOrDefault(player.getLoginNameLower(), 0) == currentRoom && mobAmount > 0) {
			player.objectDistance = 3;
			player.sendMessage("You need to defeat the current room before moving on!");
			return;
		}
		if(!roomSpawned) {
			player.objectDistance = 3;
			currentRoom+=1;
			roomSpawned = true;
			spawnNpcs(currentRoom);
		}

		int playerRoom = activeRoom.getOrDefault(player.getLoginNameLower(), 0) + 1;
		if (playerRoom >= roomPaths.size()) {
			player.sendMessage("You can't go this way.");
			return;
		}
		player.getPA().movePlayer(roomPaths.get(playerRoom).getX(),
				roomPaths.get(playerRoom).getY(),
				roomPaths.get(playerRoom).getZ() == 1 ? currentHeight + 1 :currentHeight);
		activeRoom.put(player.getLoginNameLower(), playerRoom);

	}

	public static void damage(Player player, int damage) {
        int current = getDamage(player);
        player.getAttributes().setInt(RAIDS_DAMAGE_ATTRIBUTE_KEY, current + damage);
    }

    public static int getDamage(Player player) {
	    return player.getAttributes().getInt(RAIDS_DAMAGE_ATTRIBUTE_KEY, 0);
    }

    private static void resetDamage(Player player) {
	    player.getAttributes().removeInt(RAIDS_DAMAGE_ATTRIBUTE_KEY);
    }

    public static class RaidsRank {
	    private final Player player;
	    private int rank;
	    private final int damage;

	    RaidsRank(Player player, int damage) {
	        this.player = player;
            this.damage = damage;
        }

        @Override
        public String toString() {
            return "RaidsRank{" +
                    "player=" + (player == null ? null : player.getDisplayName()) +
                    ", rank=" + rank +
                    ", damage=" + damage +
                    '}';
        }

        public Player getPlayer() {
            return player;
        }

        public int getRank() {
            return rank;
        }

        public int getDamage() {
            return damage;
        }
    }

}
