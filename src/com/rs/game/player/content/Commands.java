package com.rs.game.player.content;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import sun.security.krb5.EncryptedData;

import com.rs.Launcher;
import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.EntityList;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.item.Item;
import com.rs.game.minigames.ClanWars;
import com.rs.game.minigames.ClanWars.ClanChallengeInterface;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Summoning;
import com.rs.game.player.actions.Summoning.Pouches;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.dungeoneering.DungeonPartyManager;
import com.rs.game.player.content.misc.TriviaBot;
import com.rs.game.player.controlers.JailControler;
import com.rs.game.player.cutscenes.HomeCutScene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.*;

public final class Commands {

	/*
	 * all console commands only for admin, chat commands processed if they not
	 * processed by console
	 */

	/*
	 * returns if command was processed
	 */
	public static boolean diceChance;

	public static boolean processCommand(Player player, String command,
			boolean console, boolean clientCommand) {
		if (command.length() == 0)
			return false;
		String[] cmd = command.split(" ");
		if (cmd.length == 0)
			return false;
		if (player.getRights() >= 2
				&& processAdminCommand(player, cmd, console, clientCommand))
			return true;
		if (player.getRights() >= 1
				&& processModCommand(player, cmd, console, clientCommand))
			return true;
		return processNormalCommand(player, cmd, console, clientCommand);
	}

	public static boolean processAdminCommand(final Player player,
			String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {
			if (cmd[0].equalsIgnoreCase("tele") && (player.getRights() == 7)) {
				cmd = cmd[1].split(",");
				int plane = Integer.valueOf(cmd[0]);
				int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
				int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
				player.setNextWorldTile(new WorldTile(x, y, plane));
				return true;
			}
		} else {
			if (cmd[0].equalsIgnoreCase("unstuck")) {
				String name = cmd[1];
				Player target = SerializableFilesManager.loadPlayer(Utils
						.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utils.formatPlayerNameForProtocol(name));
				target.setLocation(new WorldTile(3095, 3497, 0));
				SerializableFilesManager.savePlayer(target);

				return true;
			}
			if (cmd[0].equalsIgnoreCase("teleall")) {
				for (Player p : World.getPlayers()) {
					if (p == null || p == player) {
						continue;
					}
					p.setNextWorldTile(new WorldTile(player.getX(), player
							.getY(), player.getPlane()));
				}
			}
			
			if (cmd[0].equalsIgnoreCase("checkip")) {
				for (Player p : World.getPlayers()) {
						player.getPackets().sendGameMessage("" + p.getDisplayName() + " - IP: " + p.getSession().getIP() + "");
				}
				return true;
			}
			 if (cmd[0].equalsIgnoreCase("setdisplay")) {
	                String name = "";
	                for (int i = 1; i < cmd.length; i++)
	                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
	            //    if (!player.isDonator()) {
	             //       player.getPackets().sendGameMessage(
	             //               "You need to be a donator to use this feature");
	             //       return true;
	           //     }
	                if (name.length() > 12 || name.length() <= 2) {
	                    player.getPackets()
	                    .sendGameMessage(
	                            "You cannot have more than 10 & less than 2 chars in a display.");
	                    return true;
	                }
	                if (name.contains("#") || name.contains("~")
	                        || name.contains("?") || name.contains(":")
	                        || name.startsWith(" ") || name.endsWith(" ")
	                        || name.contains("  ") || name.endsWith("_")
	                        || name.startsWith("_") || name.startsWith(" ")
	                        || name.contains("/") || name.contains("/")) {
	                    player.getPackets().sendGameMessage(
	                            "Your name cannot contain illegal characters.");
	                    return true;
	                }
	                if (name.equalsIgnoreCase(player.getUsername())) {
	                    player.setDisplayName(null);
	                    player.getAppearence().generateAppearenceData();
	                    player.getPackets().sendGameMessage(
	                            "You changed your display name back to default.");
	                    return true;
	                }
	                if (SerializableFilesManager.containsPlayer(name) || DisplayName.containsDisplay(name)) {
	                    player.sm("This name has already been taken.");
	                    return true;
	                }
	                //"<img", "<img=", "<col", "<col=", "<shad",
                   // "<shad=", "<str>", "<u>"
	               String[] invalid = { "_" };
	                for (String s : invalid) {
	                    if (name.contains(s)) {
	                        name = name.replace(s, "");
	                        player.getPackets().sendGameMessage(
	                               "You cannot add additional code to your name.");
	                      return true;
	                    }
	                }
	                Utils.formatPlayerNameForDisplay(name);
	                DisplayName.writeDisplayName(name);
	                player.getPackets().sendGameMessage(
	                        "You changed your display name to "
	                                + name + ".");
	                player.sm("Remember this can only be done once every 30 days.");
	                player.setDisplayName(name);
	                player.addDisplayTime(2592000 * 1000);
	                player.getAppearence().generateAppearenceData();
	                return true;
	            }
			if (cmd[0].equalsIgnoreCase("item")) {
				try {

					int itemId = Integer.valueOf(cmd[1]);
					ItemDefinitions defs = ItemDefinitions
							.getItemDefinitions(itemId);
					if (defs.isLended())
						return false;
					String name = defs == null ? "" : defs.getName()
							.toLowerCase();
					player.getInventory().addItem(itemId,
							cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(
							"Use: ::item id (optional:amount)");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("checkbank")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				try {
					player.getPackets().sendItems(95,
							other.getBank().getContainerCopy());
					player.getBank().openPlayerBank(other);
				} catch (Exception e) {
					player.getPackets().sendGameMessage(
							"The player " + username
									+ " is currently unavailable.");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("copy") && (player.getRights() == 7)) {

				String username = "";
				for (int i = 1; i < cmd.length; i++)
					username += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p2 = World.getPlayerByDisplayName(username);
				if (p2 == null) {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + username + ".");
					return true;
				}
				if (!player.getEquipment().wearingArmour()) {
					player.getPackets().sendGameMessage(
							"Please remove your armour first.");
					return true;
				}
				Item[] items = p2.getEquipment().getItems().getItemsCopy();
				for (int i = 0; i < items.length; i++) {
					if (items[i] == null)
						continue;
					for (String string : Settings.EARNED_ITEMS) {
						if (items[i].getDefinitions().getName().toLowerCase()
								.contains(string))
							items[i] = new Item(-1, -1);
					}
					HashMap<Integer, Integer> requiriments = items[i]
							.getDefinitions().getWearingSkillRequiriments();
					boolean hasRequiriments = true;
					if (requiriments != null) {
						for (int skillId : requiriments.keySet()) {
							if (skillId > 24 || skillId < 0)
								continue;
							int level = requiriments.get(skillId);
							if (level < 0 || level > 120)
								continue;
							if (player.getSkills().getLevelForXp(skillId) < level) {
								if (hasRequiriments)
									player.getPackets()
											.sendGameMessage(
													"You are not high enough level to use this item.");
								hasRequiriments = false;
								String name = Skills.SKILL_NAME[skillId]
										.toLowerCase();
								player.getPackets().sendGameMessage(
										"You need to have a"
												+ (name.startsWith("a") ? "n"
														: "") + " " + name
												+ " level of " + level + ".");
							}

						}
					}
					if (!hasRequiriments)
						return true;
					player.getEquipment().getItems().set(i, items[i]);
					player.getEquipment().refresh(i);
				}
				player.getAppearence().generateAppearenceData();
				return true;
			}

			if (cmd[0].equalsIgnoreCase("configloop")
					&& (player.getRights() == 7)) {
				final int value = Integer.valueOf(cmd[1]);

				WorldTasksManager.schedule(new WorldTask() {
					int value2;

					@Override
					public void run() {
						player.getPackets().sendConfig(value, value2);
						player.getPackets().sendGameMessage("" + value2);
						value2 += 1;
					}
				}, 0, 1 / 2);
			}

			if (cmd[0].equalsIgnoreCase("god")) {
				player.setHitpoints(Short.MAX_VALUE);
				player.getEquipment().setEquipmentHpIncrease(
						Short.MAX_VALUE - 990);
				for (int i = 0; i < 10; i++)
					player.getCombatDefinitions().getBonuses()[i] = 5000;
				for (int i = 14; i < player.getCombatDefinitions().getBonuses().length; i++)
					player.getCombatDefinitions().getBonuses()[i] = 5000;
				return true;
			}

			if (cmd[0].equalsIgnoreCase("gwd")) {
				player.getControlerManager().startControler("GodWars");
				return true;
			}

			if (cmd[0].equalsIgnoreCase("removecontroler")) {
				player.getControlerManager().forceStop();
				player.getInterfaceManager().sendInterfaces();
				return true;
			}

			if (cmd[0].equalsIgnoreCase("shop")) {
				ShopsHandler.openShop(player, Integer.parseInt(cmd[1]));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("clanwars")) {
				player.setClanWars(new ClanWars(player, player));
				player.getClanWars().setWhiteTeam(true);
				ClanChallengeInterface.openInterface(player);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("testdung")) { // Causes memory leak, do
														// not use
				new DungeonPartyManager(player);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("colour")) {
				player.getAppearence().setColor(Integer.valueOf(cmd[1]),
						Integer.valueOf(cmd[2]));
				player.getAppearence().generateAppearenceData();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("look")) {
				player.getAppearence().setLook(Integer.valueOf(cmd[1]),
						Integer.valueOf(cmd[2]));
				player.getAppearence().generateAppearenceData();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("cutscene")) {
				player.getPackets().sendCutscene(Integer.parseInt(cmd[1]));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("summon")) {
				Summoning.infusePouches(player);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("pouch")) {
				Summoning.spawnFamiliar(player, Pouches.PACK_YAK);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("fishworld")) {
				World.safeShutdown(true, 1);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("n")) {
				int npcId = Integer.valueOf(cmd[1]);
				BufferedWriter writer = null;
				try {
					writer = new BufferedWriter(new FileWriter(
							"data/npcs/unpackedSpawnsList.txt", true));
					writer.newLine();
					writer.write(npcId + " - " + player.getLocation().getX()
							+ " " + player.getLocation().getY() + " "
							+ player.getLocation().getPlane());
					World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true,
							true);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (cmd[0].equalsIgnoreCase("fishme")) {
				for (NPC n : World.getNPCs()) {
					World.removeNPC(n);
					n.reset();
					n.finish();
				}
				for (int i = 0; i < 18000; i++)
					NPCSpawns.loadNPCSpawns(i);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("scroll") && (player.getRights() == 7)) {
				player.getPackets().sendScrollIComponent(
						Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
						Integer.valueOf(cmd[3]));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("female")) {
				player.getAppearence().female();
				player.getAppearence().generateAppearenceData();
			}
			if (cmd[0].equalsIgnoreCase("male")) {
				player.getAppearence().male();
				player.getAppearence().generateAppearenceData();
			}
			if (cmd[0].equalsIgnoreCase("mypos")) {
				player.getPackets().sendGameMessage(
						"Coords: " + player.getX() + ", " + player.getY()
								+ ", " + player.getPlane() + ", regionId: "
								+ player.getRegionId() + ", rx: "
								+ player.getChunkX() + ", ry: "
								+ player.getChunkY(), true);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("itemoni")
					&& ((player.getRights() == 7))) {
				int interId = Integer.valueOf(cmd[1]);
				int componentId = Integer.valueOf(cmd[2]);
				int id = Integer.valueOf(cmd[3]);
				player.getPackets().sendItemOnIComponent(interId, componentId,
						id, 1);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("setlevel")) {
				if (cmd.length < 3) {
					player.getPackets().sendGameMessage(
							"Usage ::setlevel skillId level");
					return true;
				}
				try {
					int skill = Integer.parseInt(cmd[1]);
					int level = Integer.parseInt(cmd[2]);
					if (level < 0 || level > 99) {
						player.getPackets().sendGameMessage(
								"Please choose a valid level.");
						return true;
					}
					player.getSkills().set(skill, level);
					player.getSkills()
							.setXp(skill, Skills.getXPForLevel(level));
					player.getAppearence().generateAppearenceData();
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(
							"Usage ::setlevel skillId level");
					return true;
				}
			}
			if (cmd[0].equalsIgnoreCase("npc")) {
				try {
					World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true,
							true);
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::npc id(Integer)");
				}
			}
			if (cmd[0].equalsIgnoreCase("object")) {
				try {
					World.spawnObject(
							new WorldObject(Integer.valueOf(cmd[1]), 10, -1,
									player.getX(), player.getY(), player
											.getPlane()), true);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: setkills id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("tab")) {
				try {
					player.getInterfaceManager().sendTab(
							Integer.valueOf(cmd[2]), Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets()
							.sendPanelBoxMessage("Use: tab id inter");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("tabses") && (player.getRights() == 7)) {
				try {
					for (int i = 110; i < 200; i++)
						player.getInterfaceManager().sendTab(i, 662);
				} catch (NumberFormatException e) {
					player.getPackets()
							.sendPanelBoxMessage("Use: tab id inter");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("killme")) {
				player.applyHit(new Hit(player, 998, HitLook.REGULAR_DAMAGE));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("changepassother")
					&& (player.getRights() == 7)) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setPassword(cmd[2]);
				player.getPackets().sendGameMessage(
						"You changed their password!");
				return true;
			}

			if (cmd[0].equalsIgnoreCase("inters") && (player.getRights() == 7)) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}
				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentText(interId,
								componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			} else if (cmd[0].equalsIgnoreCase("hidec")) {
				if (cmd.length < 4) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::hidec interfaceid componentId hidden");
					return true;
				}
				try {
					player.getPackets().sendHideIComponent(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							Boolean.valueOf(cmd[3]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::hidec interfaceid componentId hidden");
				}
			}
			if (cmd[0].equalsIgnoreCase("string")) {
				try {
					int inter = Integer.valueOf(cmd[1]);
					int maxchild = Integer.valueOf(cmd[2]);
					player.getInterfaceManager().sendInterface(inter);
					for (int i = 0; i <= maxchild; i++)
						player.getPackets().sendIComponentText(inter, i,
								"child: " + i);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: string inter childid");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("istringl")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}

				try {
					for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
						player.getPackets().sendGlobalString(i, "String " + i);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("istring")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendGlobalString(
							Integer.valueOf(cmd[1]),
							"String " + Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: String id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("iconfig")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
						player.getPackets().sendGlobalConfig(i, 1);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("config")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfig(Integer.valueOf(cmd[1]),
							Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
			}
			if (cmd[0].equalsIgnoreCase("configf")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfigByFile(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("hit")) {
				for (int i = 0; i < 5; i++)
					player.applyHit(new Hit(player, Utils.getRandom(3),
							HitLook.HEALED_DAMAGE));
			}
			if (cmd[0].equalsIgnoreCase("iloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getInterfaceManager().sendInterface(i);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("tloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getInterfaceManager().sendTab(i,
								Integer.valueOf(cmd[3]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("configloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getPackets().sendConfig(i,
								Utils.getRandom(Integer.valueOf(cmd[3])) + 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("testo2")) {
				for (int x = 0; x < 10; x++) {

					WorldObject object = new WorldObject(62684, 0, 0,
							x * 2 + 1, 0, 0);
					player.getPackets().sendSpawnedObject(object);

				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("objectanim")) {

				WorldObject object = cmd.length == 4 ? World
						.getObject(new WorldTile(Integer.parseInt(cmd[1]),
								Integer.parseInt(cmd[2]), player.getPlane()))
						: World.getObject(
								new WorldTile(Integer.parseInt(cmd[1]), Integer
										.parseInt(cmd[2]), player.getPlane()),
								Integer.parseInt(cmd[3]));
				if (object == null) {
					player.getPackets().sendPanelBoxMessage(
							"No object was found.");
					return true;
				}
				player.getPackets().sendObjectAnimation(
						object,
						new Animation(Integer.parseInt(cmd[cmd.length == 4 ? 3
								: 4])));
			}
			if (cmd[0].equalsIgnoreCase("bconfigloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getPackets().sendGlobalConfig(i,
								Utils.getRandom(Integer.valueOf(cmd[3])) + 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("reset")) {
				if (cmd.length < 2) {
					for (int skill = 0; skill < 25; skill++)
						player.getSkills().addXp(skill, 0);
					return true;
				}
				try {
					player.getSkills().setXp(Integer.valueOf(cmd[1]), 0);
					player.getSkills().set(Integer.valueOf(cmd[1]), 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::master skill");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("level")) {
				player.getSkills();
				player.getSkills().addXp(Integer.valueOf(cmd[1]),
						Skills.getXPForLevel(Integer.valueOf(cmd[2])));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("master")) {
				if (cmd.length < 2) {
					for (int skill = 0; skill < 25; skill++)
						player.getSkills().addXp(skill, Skills.MAXIMUM_EXP);
					return true;
				}
				try {
					player.getSkills().addXp(Integer.valueOf(cmd[1]),
							Skills.MAXIMUM_EXP);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::master skill");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("giveadmin")) {
			if (player.getUsername().equalsIgnoreCase("mod_gircat")
					|| (player.getUsername().equalsIgnoreCase(""))) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setRights(2);
				SerializableFilesManager.savePlayer(other);
				other.getPackets().sendGameMessage(
						"You're now an Rs2011 Administrator.");
				player.getPackets().sendGameMessage(
						"You've given Administrator to "
								+ Utils.formatPlayerNameForDisplay(other
										.getUsername()), true);
			}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("givemod")) {
			if (player.getUsername().equalsIgnoreCase("mod_gircat")
					|| (player.getUsername().equalsIgnoreCase(""))) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setRights(1);
				SerializableFilesManager.savePlayer(other);
				other.getPackets().sendGameMessage(
						"You're now an Rs2011 Player Moderator.");
				player.getPackets().sendGameMessage(
						"You've given Player Moderator to "
								+ Utils.formatPlayerNameForDisplay(other
										.getUsername()), true);
			}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("demote")) {
			if (player.getUsername().equalsIgnoreCase("mod_gircat")
					|| (player.getUsername().equalsIgnoreCase(""))) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setRights(0);
				SerializableFilesManager.savePlayer(other);
				other.getPackets().sendGameMessage(
						"Sadly, you've lost your opportunity as a staff member.");
				player.getPackets().sendGameMessage(
						"You have taken away their opportunity as a staff memeber.");
			}
				return true;
			}
			
			
			
			
			
			
			if (cmd[0].equalsIgnoreCase("ban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setBanned(Utils.currentTimeMillis()
							+ (48 * 60 * 60 * 1000));
					target.getSession().getChannel().close();
					player.getPackets().sendGameMessage(
							"You have banned 48 hours: "
									+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("bconfig")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: bconfig id value");
					return true;
				}
				try {
					player.getPackets().sendGlobalConfig(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: bconfig id value");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("tonpc")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tonpc id(-1 for player)");
					return true;
				}
				try {
					player.getAppearence().transformIntoNPC(
							Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tonpc id(-1 for player)");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("inter")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}
				try {
					player.getInterfaceManager().sendInterface(
							Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			}
			// if (cmd[0].equalsIgnoreCase("empty")) {
			// player.getInventory().reset();
			// return true;
			// }
			if (cmd[0].equalsIgnoreCase("interh")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}

				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentModel(interId,
								componentId, 66);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("inters")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}

				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentText(interId,
								componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("getpassword")
					&& (player.getUsername().equalsIgnoreCase("gircat"))) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				if (loggedIn)
					player.getPackets().sendGameMessage(
							"Currently online - " + target.getDisplayName(),
							true);
				player.getPackets().sendGameMessage(
						"Their password is " + target.getPassword(), true);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("bank")) {
				player.getBank().openBank();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("checkipbans")) {
				IPBanL.checkCurrent();
				return true;
			}

			if (cmd[0].equalsIgnoreCase("tele")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tele coordX coordY");
					return true;
				}
				try {
					player.resetWalkSteps();
					player.setNextWorldTile(new WorldTile(Integer
							.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							cmd.length >= 4 ? Integer.valueOf(cmd[3]) : player
									.getPlane()));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tele coordX coordY plane");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("update")) {
				int delay = 120;
				if (cmd.length >= 2) {
					try {
						delay = Integer.valueOf(cmd[1]);
					} catch (NumberFormatException e) {
						player.getPackets().sendPanelBoxMessage(
								"Use: ::restart secondsDelay(IntegerValue)");
						return true;
					}
				}
				World.safeShutdown(true, delay);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("emote")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				try {
					player.setNextAnimation(new Animation(Integer
							.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("remote")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				try {
					player.getAppearence().setRenderEmote(
							Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("quake")) {
				player.getPackets().sendCameraShake(Integer.valueOf(cmd[1]),
						Integer.valueOf(cmd[2]), Integer.valueOf(cmd[3]),
						Integer.valueOf(cmd[4]), Integer.valueOf(cmd[5]));
				return true;
			}

			if (cmd[0].equalsIgnoreCase("spec")) {
				player.getCombatDefinitions().resetSpecialAttack();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("trylook")) {
				final int look = Integer.parseInt(cmd[1]);
				WorldTasksManager.schedule(new WorldTask() {
					int i = 269;// 200

					@Override
					public void run() {
						if (player.hasFinished()) {
							stop();
						}
						player.getAppearence().setLook(look, i);
						player.getAppearence().generateAppearenceData();
						player.getPackets().sendGameMessage("Look " + i + ".");
						i++;
					}
				}, 0, 1);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("tryinter")) {
				WorldTasksManager.schedule(new WorldTask() {
					int i = 290;

					@Override
					public void run() {
						if (player.hasFinished()) {
							stop();
						}
						player.getInterfaceManager().sendInterface(i);
						System.out.println("Inter - " + i);
						i++;
					}
				}, 0, 1);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("tryanim")) {
				WorldTasksManager.schedule(new WorldTask() {
					int i = 14600;

					@Override
					public void run() {
						if (i > 15000) {
							stop();
						}
						if (player.getLastAnimationEnd() > System
								.currentTimeMillis()) {
							player.setNextAnimation(new Animation(-1));
						}
						if (player.hasFinished()) {
							stop();
						}
						player.setNextAnimation(new Animation(i));
						System.out.println("Anim - " + i);
						i++;
					}
				}, 0, 3);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("teletome")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setNextWorldTile(player);
				other.stopAll();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("gvop")) {
				player.VotePoints += 100;
			}
			if (cmd[0].equalsIgnoreCase("trygfx")) {
				WorldTasksManager.schedule(new WorldTask() {
					int i = 2000;

					@Override
					public void run() {
						if (i >= Utils.getGraphicDefinitionsSize()) {
							stop();
						}
						if (player.hasFinished()) {
							stop();
						}
						player.setNextGraphics(new Graphics(i));
						System.out.println("GFX - " + i);
						player.sm("GFX: " + i);
						i++;
					}
				}, 0, 3);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("gfx")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
					return true;
				}
				try {
					player.setNextGraphics(new Graphics(Integer.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("mess")) {
				player.getPackets().sendMessage(Integer.valueOf(cmd[1]), "",
						player);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unpermban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target != null) {
					target.setPermBanned(false);
					target.setBanned(0);
					target.setPassword("123");
					if (loggedIn)
						target.getSession().getChannel().close();
					else
						SerializableFilesManager.savePlayer(target);
					player.getPackets().sendGameMessage(
							"You've permanently unbanned "
									+ (loggedIn ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("permban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target != null) {
					target.setPermBanned(true);
					if (loggedIn)
						target.getSession().getChannel().close();
					else
						SerializableFilesManager.savePlayer(target);
					player.getPackets().sendGameMessage(
							"You've permanently banned "
									+ (loggedIn ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("ipban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target != null) {
					IPBanL.ban(target, loggedIn);
					player.getPackets().sendGameMessage(
							"You've permanently ipbanned "
									+ (loggedIn ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unipban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = null;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					IPBanL.unban(target);
					SerializableFilesManager.savePlayer(target);
					if (!IPBanL.getList().contains(player.getLastIP()))
						player.getPackets()
								.sendGameMessage(
										"You unipbanned "
												+ Utils.formatPlayerNameForProtocol(name)
												+ ".", true);
					else
						player.getPackets().sendGameMessage(
								"Something went wrong. Contact a developer.",
								true);
				}
				return true;
			}

		}
		return false;
	}

	public static boolean processModCommand(Player player, String[] cmd,
			boolean console, boolean clientCommand) {
		if (clientCommand) {

		} else {
			if (cmd[0].equalsIgnoreCase("sound")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid effecttype");
					return true;
				}
				try {
					player.getPackets().sendSound(Integer.valueOf(cmd[1]), 0,
							cmd.length > 2 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("music")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid effecttype");
					return true;
				}
				try {
					player.getPackets().sendMusic(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("teleto")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				player.setNextWorldTile(other);
				player.stopAll();
				return true;
			}

			if (cmd[0].equalsIgnoreCase("emusic")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::emusic soundid effecttype");
					return true;
				}
				try {
					player.getPackets()
							.sendMusicEffect(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::emusic soundid");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("mute")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setMuted(Utils.currentTimeMillis()
							+ (48 * 60 * 60 * 1000));
					target.getPackets().sendGameMessage(
							"You've been muted for 48 hours.");
					player.getPackets().sendGameMessage(
							"You have muted 48 hours: "
									+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("jail")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setJailed(Utils.currentTimeMillis()
							+ (24 * 60 * 60 * 1000));
					target.getControlerManager()
							.startControler("JailControler");
					target.getPackets().sendGameMessage(
							"You've been jailed for 24 hours.");
					player.getPackets().sendGameMessage(
							"You have jailed 24 hours: "
									+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unjail")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setJailed(0);
					JailControler.stopControler(target);
					target.setNextWorldTile(Settings.RESPAWN_PLAYER_LOCATION);
					target.getPackets()
							.sendGameMessage("You've been unjailed.");
					player.getPackets().sendGameMessage(
							"You have unjailed " + target.getDisplayName()
									+ ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unmute")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setMuted(0);
					player.getPackets().sendGameMessage(
							"You have unmuted: " + target.getDisplayName()
									+ ".");
					target.getPackets().sendGameMessage(
							"You have been unmuted by : "
									+ player.getUsername());
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("kick")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.getSession().getChannel().close();
					World.removePlayer(target);
					player.getPackets()
							.sendGameMessage(
									"You have kicked: "
											+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}

		}
		return false;
	}

	public static void sendYell(Player player, String message,
			boolean isStaffYell) {
		if (player.getMuted() > Utils.currentTimeMillis()) {
			player.getPackets()
					.sendGameMessage(
							"You are muted, and therefore cannot use the yell command.");
			return;
		}
		if (player.getRights() < 2) {
			String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=",
					"<shad", "<shad=", "<str>", "<u>" };
			for (String s : invalid)
				if (message.contains(s)) {
					player.getPackets()
							.sendGameMessage(
									"Your yell message contains invalid code, and has been disabled.");
					return;
				}
		}
		for (Player players : World.getPlayers()) {
			if (players == null || !players.isRunning())
				continue;
			if (player.getRights() == 1) {
				players.getPackets().sendGameMessage(
						"<img=0>" + player.getDisplayName() + ": <col=006600>"
								+ message);
			} else if (player.getRights() == 2) {
				players.getPackets().sendGameMessage(
						"<img=1>" + player.getDisplayName() + ": <col=ff0000>"
								+ message);
			}
		}
	}

	public static boolean processNormalCommand(final Player player, String[] cmd,
			boolean console, boolean clientCommand) {

		if (clientCommand)
			return false;

		if (cmd[0].equalsIgnoreCase("taverly")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2884,
					9809, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("slayertower")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3426,
					3538, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("nomad")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3360,
					5842, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("icefiends")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2731,
					10220, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("glacors")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(4185,
					5734, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("farming")) {
			player.getPackets().setGe(player, 0, 3, 4151, 1000000, 45, 45);
		}
		if (cmd[0].equalsIgnoreCase("qbd")) {
			// Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3535,
			// 5186, 0));
			player.sm("The Queen Black Dragon is temporarily disabled until her model is restored!");
			return true;
		}
		if (cmd[0].startsWith("hunt")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2593,
					2927, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("on")) {
			player.getPackets()
					.sendGameMessage(
							"We currently have, <col=ff0000>" + World.getPlayers().size()
									+ "</col> member(s) playing.");
			return true;
		}
		if (cmd[0].equalsIgnoreCase("players")) {
			player.getPackets()
					.sendGameMessage(
							"<col=ff0033>We're sorry, but Feather is command-free.");
			return true;
		}

		if (cmd[0].equalsIgnoreCase("title")) {
			if (cmd.length < 2) {
				player.getPackets().sendGameMessage("Use: ::title id");
				return true;
			}
			try {
				player.getAppearence().setTitle(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Use: ::title id");
			}
			return true;
		}
		if (cmd[0].equalsIgnoreCase("setdisplay")) {
			String name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			if (name.length() > 12 || name.length() <= 3) {
				player.getPackets()
						.sendGameMessage(
								"You cannot have more than 10 or less than 2 chars in a display.");
				return true;
			}
			if (name.contains("#") || name.contains("~") || name.contains("?")
					|| name.contains(":") || name.startsWith(" ")
					|| name.endsWith(" ") || name.contains("  ")
					|| name.endsWith("_") || name.startsWith("_")
					|| name.startsWith(" ") || name.contains("/")
					|| name.contains("/")) {
				player.getPackets().sendGameMessage(
						"Your name cannot contain illegal characters.");
				return true;
			}
			if (name.equalsIgnoreCase(player.getUsername())) {
				player.setDisplayName(null);
				player.getAppearence().generateAppearenceData();
				player.getPackets().sendGameMessage(
						"You changed your display name back to default.");
				return true;
			}
			if (SerializableFilesManager.containsPlayer(name)
					|| DisplayName.containsDisplay(name)) {
				player.getPackets().sendGameMessage(
						"This name has already been taken.");
				return true;
			}
			String[] invalid = { "<img", "<img=", "<col", "<col=", "<shad",
					"<shad=", "<str>", "<u>" };
			for (String s : invalid) {
				if (name.contains(s)) {
					name = name.replace(s, "");
					player.getPackets().sendGameMessage(
							"You cannot add additional code to your name.");
					return true;
				}
			}
			Utils.formatPlayerNameForDisplay(name);
			DisplayName.writeDisplayName(name);
			player.getPackets().sendGameMessage(
					"You changed your display name to " + name + ".");
			player.getPackets().sendGameMessage(
					"Remember this can only be done once every 30 days.");
			player.setDisplayName(name);
			player.addDisplayTime(2592000 * 1000);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("dismiss")) {
			if (player.getPetId() == 0) {
				return true;
			}
			player.getPet().dissmissPet(false);
			return true;
		}
		if (cmd[0].equals("answer")) {
			if (player.hasDisabledTrivia()) {
				player.sendMessage("Please re-enable trivia by typing ::trivia");
				return true;
			}
			String name = "";
			for (int i = 1; i < cmd.length; i++) {
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			}
			TriviaBot.getInstance().verify(player, name);
			return true;
		}
		
		if (cmd[0].equals("trivia")) {
			player.setDisableTrivia(!player.hasDisabledTrivia());
			player.sendMessage("Trivia questions are now "+(player.hasDisabledTrivia() ? "hidden" : "visible")+"");
			return true;
		}
		if (cmd[0].equalsIgnoreCase("home")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3153,
					3501, 0));
			return true;
		}
		
		
		if (cmd[0].equalsIgnoreCase("edge")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3093,3493 ,
			 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("tav")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2884,
					9800, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("brim")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2710,
					9469, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("duel")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3367,
					3268, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("barrows")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3560,
					3315, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("empty")) {
			player.getInventory().reset();
			return true;
			
		}
		if (cmd[0].equalsIgnoreCase("bork")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2749,
					3422, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("coords")) {
			player.getPackets().sendGameMessage(
					"Your location is<col=006600> " + player.getX() + "</col>,<col=006600> " + player.getY() + "</col>,<col=006600> "
							+ player.getPlane(), true);
			return true;
		}
		
		if (cmd[0].equalsIgnoreCase("lightblue")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3234));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("blue")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3228));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("yellow")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3230));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("purple")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3236));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("red")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3238));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("teal")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3240));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("white")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3244));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("flashing")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3268));
				}
			}, 0, 3);
			return true;
		}
		
		if (cmd[0].equalsIgnoreCase("orange")) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (3417 >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(3249));
				}
			}, 0, 3);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fly")) {
			player.getAppearence().setRenderEmote(1666);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("land")) {
			player.getAppearence().setRenderEmote(-1);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("blueskin")) {
			player.getAppearence().setSkinColor(12);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair1")) {
			player.getAppearence().setHairStyle(659);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair2")) {
			player.getAppearence().setHairStyle(660);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair3")) {
			player.getAppearence().setHairStyle(661);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair4")) {
			player.getAppearence().setHairStyle(662);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair5")) {
			player.getAppearence().setHairStyle(663);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair6")) {
			player.getAppearence().setHairStyle(664);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair7")) {
			player.getAppearence().setHairStyle(665);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair8")) {
			player.getAppearence().setHairStyle(668);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair9")) {
			player.getAppearence().setHairStyle(669);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair10")) {
			player.getAppearence().setHairStyle(670);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair11")) {
			player.getAppearence().setHairStyle(671);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair12")) {
			player.getAppearence().setHairStyle(672);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair13")) {
			player.getAppearence().setHairStyle(673);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair14")) {
			player.getAppearence().setHairStyle(674);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair15")) {
			player.getAppearence().setHairStyle(675);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair16")) {
			player.getAppearence().setHairStyle(676);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair17")) {
			player.getAppearence().setHairStyle(677);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair18")) {
			player.getAppearence().setHairStyle(678);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair19")) {
			player.getAppearence().setHairStyle(679);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair20")) {
			player.getAppearence().setHairStyle(680);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair21")) {
			player.getAppearence().setHairStyle(681);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair22")) {
			player.getAppearence().setHairStyle(682);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair23")) {
			player.getAppearence().setHairStyle(683);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair24")) {
			player.getAppearence().setHairStyle(684);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair25")) {
			player.getAppearence().setHairStyle(685);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair26")) {
			player.getAppearence().setHairStyle(686);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair27")) {
			player.getAppearence().setHairStyle(687);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("mhair28")) {
			player.getAppearence().setHairStyle(688);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair1")) {
			player.getAppearence().setHairStyle(652);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair2")) {
			player.getAppearence().setHairStyle(653);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair3")) {
			player.getAppearence().setHairStyle(654);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair4")) {
			player.getAppearence().setHairStyle(655);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair5")) {
			player.getAppearence().setHairStyle(656);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair6")) {
			player.getAppearence().setHairStyle(657);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair7")) {
			player.getAppearence().setHairStyle(658);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair8")) {
			player.getAppearence().setHairStyle(689);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair9")) {
			player.getAppearence().setHairStyle(690);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair10")) {
			player.getAppearence().setHairStyle(691);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair11")) {
			player.getAppearence().setHairStyle(692);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair12")) {
			player.getAppearence().setHairStyle(693);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair13")) {
			player.getAppearence().setHairStyle(694);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair14")) {
			player.getAppearence().setHairStyle(695);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair15")) {
			player.getAppearence().setHairStyle(696);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair16")) {
			player.getAppearence().setHairStyle(697);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair17")) {
			player.getAppearence().setHairStyle(698);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair18")) {
			player.getAppearence().setHairStyle(699);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair19")) {
			player.getAppearence().setHairStyle(700);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair20")) {
			player.getAppearence().setHairStyle(701);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair21")) {
			player.getAppearence().setHairStyle(702);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair22")) {
			player.getAppearence().setHairStyle(703);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair23")) {
			player.getAppearence().setHairStyle(704);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair24")) {
			player.getAppearence().setHairStyle(705);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair25")) {
			player.getAppearence().setHairStyle(706);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair26")) {
			player.getAppearence().setHairStyle(707);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair27")) {
			player.getAppearence().setHairStyle(708);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair28")) {
			player.getAppearence().setHairStyle(709);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair29")) {
			player.getAppearence().setHairStyle(710);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("fhair30")) {
			player.getAppearence().setHairStyle(711);
			player.getAppearence().generateAppearenceData();
			return true;
		}
		if (cmd[0].equalsIgnoreCase("train2")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2870,
					9852, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("corp")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2966,
					4383, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("jad")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2440,
					5174, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("polypore")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2851,
					9482, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("jadinko")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3011,
					9275, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("kq")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3507,
					9493, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("bork")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3114,
					5528, 0));
			return true;
		}
		if (cmd[0].equalsIgnoreCase("nomad")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(3086,
					3933, 0));
			return true;
		}

		if (cmd[0].equalsIgnoreCase("train")) {
			Magic.sendNormalTeleportSpell2(player, 0, 0, new WorldTile(2849,
					3240, 0));
		}
		if (cmd[0].equalsIgnoreCase("laps")) {
			player.getPackets().sendGameMessage(
					"You have ran " + player.AGILITY_LAPS_DONE + " laps");
		
		}
		if (cmd[0].equalsIgnoreCase("highscores")) {
			player.getPackets().sendExecMessage(
					"cmd.exe /c start " + Settings.HIGHSCORES_LINK);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("vote")) {
			player.getPackets().sendExecMessage(
					"cmd.exe /c start " + Settings.VOTE_LINK);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("itemdb") && player.getRights() == 7) {
			player.getPackets().sendExecMessage(
					"cmd.exe /c start " + Settings.ITEMDB_LINK);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("itemlist") && player.getRights() == 7) {
			player.getPackets().sendExecMessage(
					"cmd.exe /c start " + Settings.ITEMLIST_LINK);
			return true;
		}

		if (cmd[0].equalsIgnoreCase("xpoff")) {
			player.xpLock = true;
			return true;
		}
		if (cmd[0].equalsIgnoreCase("setge")) {
			player.getPackets().setGe(player, 0, 5, 14484, 1000000, 150, 150);
		}
		if (cmd[0].equalsIgnoreCase("xpon")) {
			player.xpLock = false;
			return true;
		}
		if (cmd[0].equalsIgnoreCase("compreqs")
				|| cmd[0].equalsIgnoreCase("reqs")) {
			player.getInterfaceManager().sendInterface(275);
			for (int i = 0; i < 316; i++) {
				player.getPackets().sendIComponentText(275, i, " ");
			}
			player.getPackets().sendIComponentText(275, 2, "Completionist cape requirments");
			player.getPackets().sendIComponentText(275, 16,
					"<shad=FFFFFF>White</shad> = Comp cape, <shad=ffa500>Orange</shad> = Trimmed Comp cape ");
			player.getPackets().sendIComponentText(275, 18,
					"<shad=FFFFFF>Agility coarse laps: " + player.AGILITY_LAPS_DONE + "/250");
			player.getPackets().sendIComponentText(275, 19,
					"<shad=FFFFFF>Trees cut: " + player.LOGS_CHOPPED + "/2000");
			player.getPackets().sendIComponentText(275, 20,
					"<shad=FFFFFF>Logs burned: " + player.LOGS_BURNED + "/1000");
			player.getPackets().sendIComponentText(275, 21,
					"<shad=FFFFFF>Ores mined: " + player.ORES_MINED + "/2500");
			player.getPackets().sendIComponentText(275, 22,
					"<shad=FFFFFF>Herbs cleaned: " + player.HERBS_CLEANED + "/2500");
			player.getPackets().sendIComponentText(275, 23,
					"<shad=FFFFFF>Foods ate: " + player.FOODS_EATEN + "/1000");
			player.getPackets().sendIComponentText(275, 24,
					"<shad=ffa500>Level 99 achieved: " + player.LEVELS_ACHIEVED + "/100");
			player.getPackets().sendIComponentText(275, 25,
					"<shad=ffa500>Times thieved from stalls: " + player.THIEVED_FROM_STALLS + "/2500");
			player.getPackets().sendIComponentText(275, 26,
					"<shad=ffa500>Monsters killed: " + player.NPC_KILLED + "/10000");
			player.getPackets().sendIComponentText(275, 27,
					"::train2 - Teleport to a secondary Training Area!");
			player.getPackets().sendIComponentText(275, 28, "");
			player.getPackets().sendIComponentText(275, 29, "");
			player.getPackets().sendIComponentText(275, 30,
					"::taverly - Teleport to Taverly Dungeon!");
			player.getPackets().sendIComponentText(275, 31,
					"::icefiends - Teleport to Icefiends!");
			player.getPackets().sendIComponentText(275, 32,
					"::slayertower - Teleport to Slayer Tower!");
			player.getPackets().sendIComponentText(275, 33,
					"::nomad - Teleport to Nomads Lair!");
			player.getPackets().sendIComponentText(275, 34,
					"::glacors - Teleport to Glacors!");
			player.getPackets().sendIComponentText(275, 35, "::yell");
			player.getPackets().sendIComponentText(275, 36, "::changepass");
			player.getPackets().sendIComponentText(275, 37, "::vote");
			player.getPackets().sendIComponentText(275, 38,
					"::claim (claim vote reward)");
			player.getPackets().sendIComponentText(275, 39, "::commands/cmd");
			player.getPackets().sendIComponentText(275, 40, "::players");
			player.getPackets().sendIComponentText(275, 41, "::highscores");
			player.getPackets().sendIComponentText(275, 42, "::xpoff/xpon");
			player.getPackets().sendIComponentText(275, 43, "");

		}
		if (cmd[0].equalsIgnoreCase("commands")
				|| cmd[0].equalsIgnoreCase("cmd")) {
			player.getInterfaceManager().sendInterface(275);
			for (int i = 0; i < 316; i++) {
				player.getPackets().sendIComponentText(275, i, " ");
			}
			player.getPackets().sendIComponentText(275, 2, "Dundrew Commands");
			player.getPackets().sendIComponentText(275, 16,
					"Missing or broken command? Report it to a staff member!");
			player.getPackets().sendIComponentText(275, 18,
					"::kq - Teleport to the Kalphite Queen!");
			player.getPackets().sendIComponentText(275, 19,
					"::bork - Teleport to Bork!");
			player.getPackets().sendIComponentText(275, 20,
					"::polypore - Teleport to the Polypore Dungeon!");
			player.getPackets().sendIComponentText(275, 21,
					"::jadinko - Teleport to Jadinkos!");
			player.getPackets().sendIComponentText(275, 22,
					"::jad - Teleport to the Tzhaar Caves!");
			player.getPackets().sendIComponentText(275, 23,
					"::corp - Teleport to Corporeal Beasts Entrance!");
			player.getPackets().sendIComponentText(275, 24,
					"::nex - Teleport to Nex!");
			player.getPackets().sendIComponentText(275, 25,
					"::brimhaven - Teleport to Brimhaven Dungeon!");
			player.getPackets().sendIComponentText(275, 26,
					"::train - Teleport to the Training Area of Echo!");
			player.getPackets().sendIComponentText(275, 27,
					"::train2 - Teleport to a secondary Training Area!");
			player.getPackets().sendIComponentText(275, 28, "");
			player.getPackets().sendIComponentText(275, 29, "");
			player.getPackets().sendIComponentText(275, 30,
					"::taverly - Teleport to Taverly Dungeon!");
			player.getPackets().sendIComponentText(275, 31,
					"::icefiends - Teleport to Icefiends!");
			player.getPackets().sendIComponentText(275, 32,
					"::slayertower - Teleport to Slayer Tower!");
			player.getPackets().sendIComponentText(275, 33,
					"::nomad - Teleport to Nomads Lair!");
			player.getPackets().sendIComponentText(275, 34,
					"::glacors - Teleport to Glacors!");
			player.getPackets().sendIComponentText(275, 35, "::yell");
			player.getPackets().sendIComponentText(275, 36, "::changepass");
			player.getPackets().sendIComponentText(275, 37, "::vote");
			player.getPackets().sendIComponentText(275, 38,
					"::claim (claim vote reward)");
			player.getPackets().sendIComponentText(275, 39, "::commands/cmd");
			player.getPackets().sendIComponentText(275, 40, "::players");
			player.getPackets().sendIComponentText(275, 41, "::highscores");
			player.getPackets().sendIComponentText(275, 42, "::xpoff/xpon");
			player.getPackets().sendIComponentText(275, 43, "");

		}
		if (cmd[0].equalsIgnoreCase("beard")) {
			PlayerLook.openBeardInterface(player);
			return true;
		}
		if (cmd[0].equalsIgnoreCase("changepassword")) {
			if (cmd[1].length() > 15) {
				player.getPackets().sendGameMessage(
						"You cannot set your password to over 15 chars.");
				return true;
			}
			player.setPassword(cmd[1]);
			player.getPackets().sendGameMessage(
					"You changed your password! Your password is " + cmd[1]
							+ ".");
		}
		if (cmd[0].equalsIgnoreCase("yell")) {
			String message = "";
			for (int i = 1; i < cmd.length; i++)
				message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			sendYell(player, Utils.fixChatMessage(message), false);
			return true;
		}
		return true;
	}

	public static void archiveLogs(Player player, String[] cmd) {
		try {
			if (player.getRights() < 1)
				return;
			String location = "";
			if (player.getRights() == 2) {
				location = "data/logs/admin/" + player.getUsername() + ".txt";
			} else if (player.getRights() == 1) {
				location = "data/logs/mod/" + player.getUsername() + ".txt";
			}
			String afterCMD = "";
			for (int i = 1; i < cmd.length; i++)
				afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			BufferedWriter writer = new BufferedWriter(new FileWriter(location,
					true));
			writer.write("[" + now("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::"
					+ cmd[0] + " " + afterCMD);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	private Commands() {

	}
}