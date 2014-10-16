package com.rs.game.player.content.dungeons;

import com.rs.game.WorldObject;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;

public class ResourceDungeons {
	
	/**
	* @author HerBrightSkies @ rune-server.org
	*/
	
	private enum Dungeons {
		
		DUNGEON(52859, 85, 15000, 1297, 4510, 0, 0),
		DUNGEON_1(52875, 1, 1500, 3033, 9599, 0, 0),
		DUNGEON_2(52855, 15, 2500, 1041, 4575, 0, 1),
		DUNGEON_3(52864, 1, 1500, 3034, 9772, 0, 1),
		DUNGEON_4(52860, 75, 12500, 1182, 4515, 0, 2),
		DUNGEON_5(52872, 1, 1500, 3298, 3307, 0, 2),
		DUNGEON_6(52849, 10, 2000, 991, 4585, 0, 3),
		DUNGEON_7(52867, 1, 1500, 3132, 9933, 0, 3),
		DUNGEON_8(52853, 20, 3500, 1134, 4589, 0, 4),
		DUNGEON_9(52868, 1, 1500, 3104, 9826, 0, 4),
		DUNGEON_10(52850, 25, 4000, 1186, 4598, 0, 5),
		DUNGEON_11(52869, 1, 1500, 2845, 9557, 0, 5),
		DUNGEON_12(52861, 30, 5000, 3498, 3633, 0, 6),
		DUNGEON_13(52862, 1, 1500, 3513, 3666, 0, 6),
		DUNGEON_14(52857, 35, 6500, 1256, 4592, 0, 7),
		DUNGEON_16(52873, 1, 1500, 2578, 9898, 0, 7),
		DUNGEON_17(52856, 45, 7500, 1052, 4521, 0, 8),
		DUNGEON_18(52866, 1, 1500, 3022, 9741, 0, 8),
		DUNGEON_19(52851, 55, 9000, 1394, 4588, 0, 9),
		DUNGEON_20(52870, 1, 1500, 2854, 9841, 0, 9),
		DUNGEON_21(52852, 60, 9500, 1000, 4522, 0, 10),
		DUNGEON_22(52865, 1, 1500, 2912, 9810, 0, 10),
		DUNGEON_23(52863, 65, 10000, 1312, 4590, 0, 11),
		DUNGEON_24(52876, 1, 1500, 3164, 9878, 0, 11),
		DUNGEON_25(52858, 70, 11000, 1238, 4524, 0, 12),
		DUNGEON_26(52874, 1, 1500, 3160, 5521, 0, 12),
		DUNGEON_27(77579, 80, 13500, 1140, 4499, 0, 13),
		DUNGEON_28(77580, 1, 1500, 2697, 9442, 0, 13);
		
		private int objectId;
		private int lvlRequired;
		private int xp;
		private int x;
		private int y;
		private int plane;
		private int number;
		
		Dungeons(int objectId, int lvlRequired, int xp, int x, int y, int plane, int number) {
			this.objectId = objectId;
			this.lvlRequired = lvlRequired;
			this.xp = xp;
			this.x = x;
			this.y = y;
			this.plane = plane;
			this.number = number;
		}
		
		private int getObjectId() {
			return objectId;
		}
		
		private int getLvlReq() {
			return lvlRequired;
		}
		
		private int getXp() {
			return xp;
		}
		
		private int getX() {
			return x;
		}
		
		private int getY() {
			return y;
		}
		
		private int getPlane() {
			return plane;
		}
		
		private int getNumber() {
			return number;
		}
	}

	public static boolean handleDungeons(Player player, WorldObject o) {
		for (Dungeons d : Dungeons.values()) {
			if (o.getId() == d.getObjectId()) {
				if (player.getSkills().getLevel(Skills.DUNGEONEERING) >= d.getLvlReq()) {
					Magic.resourcesTeleport(player, d.getX(), d.getY(), d.getPlane());
					if (!player.getSeenDungeon()[d.getNumber()]) {
						player.getSkills().addXp(Skills.DUNGEONEERING, d.getXp());
						player.getSeenDungeon()[d.getNumber()] = true;
						player.sm("You've gained "+d.getXp()+" xp in Dungeoneering for uncovering the secrets of "
						+ "a new dungeon!");
						return true;
					}
					return true;
				} else {
					player.sm("You need a Dungeoneering level of "+d.getLvlReq()+" to uncover the secrets of this "
					+ "dungeon.");
					return true;
				}
			}
		}
		return false;
	}

}