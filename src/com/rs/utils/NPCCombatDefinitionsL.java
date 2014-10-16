package com.rs.utils;
import com.rs.cache.loaders.NPCDefinitions;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.io.FileNotFoundException;

import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.io.NpcXMLHandler;

public final class NPCCombatDefinitionsL {

	private static HashMap<Integer, NPCCombatDefinitions> npcCombatDefinitions = new HashMap<Integer, NPCCombatDefinitions>();
	private final static NPCCombatDefinitions DEFAULT_DEFINITION = new NPCCombatDefinitions(
			1, -1, -1, -1, 5, 1, 1, 0, NPCCombatDefinitions.MELEE, -1, -1,
			NPCCombatDefinitions.PASSIVE);
	private static final String PACKED_PATH = "data/npcs/packedCombatDefinitions.ncd";
	private static final String XML_PATH = "data/xml/npcCombatDefinitions.xml";

	public static void init() {
		loadXMLNPCCombatDefinitions();
		/*
		 * if (new File(PACKED_PATH).exists()) loadPackedNPCCombatDefinitions();
		 * else loadUnpackedNPCCombatDefinitions();
		 */
	}

	public static NPCCombatDefinitions getNPCCombatDefinitions(int npcId) {
		NPCCombatDefinitions def = npcCombatDefinitions.get(npcId);
		if (def == null) {
		int render = NPCDefinitions.getNPCDefinitions(npcId).renderEmote;
		int c = NPCDefinitions.getNPCDefinitions(npcId).combatLevel;
		return new NPCCombatDefinitions(getFakeHp(c), getAttackEmote(render), getDefenceEmote(render), 
		getDeathEmote(render), 4, getDeathEmote(render) == -1 ? 1 : 3, 30,
		getFakeMaxHit(c), NPCCombatDefinitions.MELEE, -1, -1, 
		NPCCombatDefinitions.PASSIVE);
		}
		if (def.getDefenceEmote() == 65535) {
		int render = NPCDefinitions.getNPCDefinitions(npcId).renderEmote;
		def.setAttackVariables(getAttackEmote(render), getDefenceEmote(render), getDeathEmote(render));
		}
		return def;
		}

	public static int getFakeHp(int combat) {
		if (combat <= 0)
		return 100;
		return combat * 10;
		}

		public static int getFakeMaxHit(int combat) {
		if (combat <= 0)
		return 5;
		return combat * 2;
		}
		public static int getAttackEmote(int render) {
			switch(render) {
			case 4:
			return 422;
			case 75:
			return 64;
			case 97:
			return 6579;
			case 56:
			case 61:
			case 55:
			return 4652;
			case 2100:
			case 118:
			case 711:
			return 13049;
			case 29:
			case 1714:
			case 1573:
			case 1272:
			return 13047;
			case 70:
			case 71:
			case 72:
			return 5485;
			case 95:
			return 6489;
			case 107:
			return 7218;
			case 67:
			return 5568;
			case 38:
			case 69:
			return 5571;
			case 68:
			return 5578;
			}
			return -1;
			}

			public static int getDefenceEmote(int render) {
			switch(render) {
			case 4:
			case 29:
			case 1714:
			case 1573:
			case 1272:
			return 424;
			case 118:
			case 2100:
			return 13038;
			case 711:
			return 13042;
			case 75:
			return 65;
			case 97:
			return 6578;
			case 56:
			case 61:
			case 55:
			return 4651;
			case 70:
			case 71:
			case 72:
			return 5489;
			case 95:
			return 6488;
			case 107:
			return 7214;
			case 67:
			return 5567;
			case 38:
			case 69:
			return 5574;
			case 68:
			return 5579;
			}
			return -1;
			}

			public static int getDeathEmote(int render) {
			switch(render) {
			case 4:
			case 2100:
			case 711:
			case 118:
			case 29:
			case 1714:
			case 1573:
			case 1272:
			return 836;
			case 75:
			return 67;
			case 97:
			return 6576;
			case 56:
			case 61:
			case 55:
			return 4653;
			case 70:
			case 71:
			case 72:
			return 5491;
			case 95:
			return 6490;
			case 107:
			return 7213;
			case 67:
			return 5569;
			case 38:
			case 69:
			return 5575;
			case 68:
			return 5580;
			}
			return -1;
			}

		public static final void loadXMLNPCCombatDefinitions() {
		try {
			npcCombatDefinitions = NpcXMLHandler.fromXML(XML_PATH);
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				try {
					new File(XML_PATH);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		}
	}

	@SuppressWarnings("unused")
	private static void loadUnpackedNPCCombatDefinitions() {
		int count = 0;
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			BufferedReader in = new BufferedReader(new FileReader(
					"data/npcs/unpackedCombatDefinitionsList.txt"));
			while (true) {
				String line = in.readLine();
				count++;
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2)
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + count
									+ ", " + line);
				int npcId = Integer.parseInt(splitedLine[0]);
				String[] splitedLine2 = splitedLine[1].split(" ", 12);
				if (splitedLine2.length != 12)
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + count
									+ ", " + line);
				int hitpoints = Integer.parseInt(splitedLine2[0]);
				int attackAnim = Integer.parseInt(splitedLine2[1]);
				int defenceAnim = Integer.parseInt(splitedLine2[2]);
				int deathAnim = Integer.parseInt(splitedLine2[3]);
				int attackDelay = Integer.parseInt(splitedLine2[4]);
				int deathDelay = Integer.parseInt(splitedLine2[5]);
				int respawnDelay = Integer.parseInt(splitedLine2[6]);
				int maxHit = Integer.parseInt(splitedLine2[7]);
				int attackStyle;
				if (splitedLine2[8].equalsIgnoreCase("MELEE"))
					attackStyle = NPCCombatDefinitions.MELEE;
				else if (splitedLine2[8].equalsIgnoreCase("RANGE"))
					attackStyle = NPCCombatDefinitions.RANGE;
				else if (splitedLine2[8].equalsIgnoreCase("MAGE"))
					attackStyle = NPCCombatDefinitions.MAGE;
				else if (splitedLine2[8].equalsIgnoreCase("SPECIAL"))
					attackStyle = NPCCombatDefinitions.SPECIAL;
				else if (splitedLine2[8].equalsIgnoreCase("SPECIAL2"))
					attackStyle = NPCCombatDefinitions.SPECIAL2;
				else
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + line);
				int attackGfx = Integer.parseInt(splitedLine2[9]);
				int attackProjectile = Integer.parseInt(splitedLine2[10]);
				int agressivenessType;
				if (splitedLine2[11].equalsIgnoreCase("PASSIVE"))
					agressivenessType = NPCCombatDefinitions.PASSIVE;
				else if (splitedLine2[11].equalsIgnoreCase("AGRESSIVE"))
					agressivenessType = NPCCombatDefinitions.AGRESSIVE;
				else
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + line);
				out.writeShort(npcId);
				out.writeShort(hitpoints);
				out.writeShort(attackAnim);
				out.writeShort(defenceAnim);
				out.writeShort(deathAnim);
				out.writeByte(attackDelay);
				out.writeByte(deathDelay);
				out.writeInt(respawnDelay);
				out.writeShort(maxHit);
				out.writeByte(attackStyle);
				out.writeShort(attackGfx);
				out.writeShort(attackProjectile);
				out.writeByte(agressivenessType);
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						attackDelay, deathDelay, respawnDelay, maxHit,
						attackStyle, attackGfx, attackProjectile,
						agressivenessType));
			}
			in.close();
			out.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	@SuppressWarnings("unused")
	private static void loadPackedNPCCombatDefinitions() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int hitpoints = buffer.getShort() & 0xffff;
				int attackAnim = buffer.getShort() & 0xffff;
				int defenceAnim = buffer.getShort() & 0xffff;
				int deathAnim = buffer.getShort() & 0xffff;
				int attackDelay = buffer.get() & 0xff;
				int deathDelay = buffer.get() & 0xff;
				int respawnDelay = buffer.getInt();
				int maxHit = buffer.getShort() & 0xffff;
				int attackStyle = buffer.get() & 0xff;
				int attackGfx = buffer.getShort() & 0xffff;
				int attackProjectile = buffer.getShort() & 0xffff;
				int agressivenessType = buffer.get() & 0xff;
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						attackDelay, deathDelay, respawnDelay, maxHit,
						attackStyle, attackGfx, attackProjectile,
						agressivenessType));
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private NPCCombatDefinitionsL() {

	}

}
