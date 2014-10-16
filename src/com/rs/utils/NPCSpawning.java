package com.rs.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

public class NPCSpawning {

	/**
	 * Contains the custom npc spawning
	 */

	public static void spawnNPCS() {
		
		World.spawnNPC(7938, new WorldTile(3860, 4944, 0), -1, true, true);
		   World.spawnObject(new WorldObject(411, 10, 1, 3146, 3494, 0), true);
		   World.spawnObject(new WorldObject(13193, 10, 2, 3159, 3508, 0), true);
		   World.spawnObject(new WorldObject(6552, 10, 2, 3155, 3508, 0), true);
		   World.spawnObject(new WorldObject(34383, 10, 3, 3158, 3501, 0), true);
		   World.spawnObject(new WorldObject(34387, 10, 3, 3156, 3498, 0), true);
		   World.spawnObject(new WorldObject(34386, 10, 3, 3154, 3495, 0), true);
		   World.spawnObject(new WorldObject(34385, 10, 3, 3152, 3492, 0), true);
		   World.spawnObject(new WorldObject(2783, 10, 3, 2341, 3809, 0), true);
		   World.spawnObject(new WorldObject(1309, 10, 3, 2330, 3805, 0), true);
		   World.spawnObject(new WorldObject(1306, 10, 3, 2331, 3800, 0), true);
		   World.spawnObject(new WorldObject(1307, 10, 3, 2350, 3803, 0), true);
		   World.spawnObject(new WorldObject(19864, 10, 3, 2323, 3808, 0), true);
		   World.spawnObject(new WorldObject(4130, 10, 2, 3153, 3507, 0), true);
						
		   World.spawnObject(new WorldObject(142, 10, 3, 2358, 3803, 0), true);
			
		   World.spawnObject(new WorldObject(1281, 10, 3, 2359, 3796, 0), true);
			
		   
		   	   World.spawnObject(new WorldObject(11938, 10, 3, 2328, 3811, 0), true);
		   		   World.spawnObject(new WorldObject(11933, 10, 3, 2328, 3810, 0), true);
		   World.spawnObject(new WorldObject(37307, 10, 3, 2328, 3809, 0), true);
		   World.spawnObject(new WorldObject(11930, 10, 3, 2328, 3808, 0), true);
		 
		   
		   World.spawnObject(new WorldObject(11942, 10, 3, 2325, 3811, 0), true);
		   World.spawnObject(new WorldObject(11941, 10, 3, 2324, 3811, 0), true);
		   World.spawnObject(new WorldObject(14860, 10, 3, 2323, 3811, 0), true);
		 	
			
		
		   
		   
		   
		World.spawnObject(new WorldObject(36972, 10, 1, 3146, 3497, 0), true);
	}
	 
	 
	/**
	 * The NPC classes.
	 */
	private static final Map<Integer, Class<?>> CUSTOM_NPCS = new HashMap<Integer, Class<?>>();

	public static void npcSpawn() {
		int size = 0;
		boolean ignore = false;
		try {
			for (String string : FileUtilities
					.readFile("data/npcs/npcspawns.txt")) {
				if (string.startsWith("//") || string.equals("")) {
					continue;
				}
				if (string.contains("/*")) {
					ignore = true;
					continue;
				}
				if (ignore) {
					if (string.contains("*/")) {
						ignore = false;
					}
					continue;
				}
				String[] spawn = string.split(" ");
				@SuppressWarnings("unused")
				int id = Integer.parseInt(spawn[0]), x = Integer
						.parseInt(spawn[1]), y = Integer.parseInt(spawn[2]), z = Integer
						.parseInt(spawn[3]), faceDir = Integer
						.parseInt(spawn[4]);
				NPC npc = null;
				Class<?> npcHandler = CUSTOM_NPCS.get(id);
				if (npcHandler == null) {
					npc = new NPC(id, new WorldTile(x, y, z), -1, true, false);
				} else {
					npc = (NPC) npcHandler.getConstructor(int.class)
							.newInstance(id);
				}
				if (npc != null) {
					WorldTile spawnLoc = new WorldTile(x, y, z);
					npc.setLocation(spawnLoc);
					World.spawnNPC(npc.getId(), spawnLoc, -1, true, false);
					size++;
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		
		   World.spawnObject(new WorldObject(11938, 10, 3, 2328, 3811, 0), true);
		   World.spawnObject(new WorldObject(11933, 10, 3, 2328, 3810, 0), true);
		   World.spawnObject(new WorldObject(37307, 10, 3, 2328, 3809, 0), true);
		   World.spawnObject(new WorldObject(11930, 10, 3, 2328, 3808, 0), true);
		 
		
		
		  World.spawnObject(new WorldObject(34383, 10, 3, 3158, 3501, 0), true);
		  World.spawnObject(new WorldObject(34386, 10, 3, 3154, 3495, 0), true);

		  World.spawnObject(new WorldObject(34387, 10, 3, 3156, 3498, 0), true);
		   World.spawnObject(new WorldObject(34385, 10, 3, 3152, 3492, 0), true);
		   World.spawnObject(new WorldObject(2783, 10, 3, 2341, 3809, 0), true);
			
		   World.spawnObject(new WorldObject(36972, 10, 1, 3146, 3497, 0), true);
		   World.spawnObject(new WorldObject(411, 10, 1, 3146, 3494, 0), true);
		   World.spawnObject(new WorldObject(6552, 10, 2, 3155, 3508, 0), true);
		   World.spawnObject(new WorldObject(13193, 10, 2, 3159, 3508, 0), true);
			 
		System.err.println("Loaded " + size + " custom npc spawns!");
	}

}