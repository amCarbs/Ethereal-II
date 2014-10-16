package com.rs.game.player.actions;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Misc;

public class Agility {

	// gnome course

	public static void walkGnomeLog(final Player player) {
		if (player.getX() != 2474 || player.getY() != 3436)
			return;
		final boolean running = player.isRunning();
		player.setRunHidden(false);
		player.addStopDelay(8);
		player.addWalkSteps(2474, 3429, -1, false);

		player.getPackets().sendGameMessage(
				"You walk carefully across the slippery log...", true);
		
		WorldTasksManager.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearence().setRenderEmote(155);
				} else {
					player.getAppearence().setRenderEmote(-1);
					player.setRunHidden(running);
					setGnomeStage(player, 0);
					player.getSkills().addXp(Skills.AGILITY, 50);
					player.getInventory().addItem(995, 5000);
					player.getPackets().sendGameMessage(
							"... and make it safely to the other side.", true);
					stop();
				}
			}
		}, 0, 6);
	}

	public static void climbGnomeObstacleNet(final Player player) {
		if (player.getY() != 3426)
			return;
		player.getPackets().sendGameMessage("You climb the netting.", true);
		player.useStairs(828, new WorldTile(player.getX(), 3423, 1), 1, 2);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 0)
					setGnomeStage(player, 1);
				player.getSkills().addXp(Skills.AGILITY, 45);
				player.getInventory().addItem(995, 2500);
			}
		}, 1);
	}

	public static void climbUpGnomeTreeBranch(final Player player) {
		player.getPackets().sendGameMessage("You climb the tree...", true);
		player.useStairs(828, new WorldTile(2473, 3420, 2), 1, 2,
				"... to the plantaform above.");
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 1)
					setGnomeStage(player, 2);
				player.getSkills().addXp(Skills.AGILITY, 25);
				player.getInventory().addItem(995, 2500);
			}
		}, 1);
	}
	public static void walk2ndTreeBranch(Player player) {
		player.getPackets().sendGameMessage("You climb the tree...", true);
		player.useStairs(828, new WorldTile(2472, 3420, 3), 1, 2,
				"... to the plantaform above.");
		
				player.getSkills().addXp(Skills.AGILITY, 25);
				player.getInventory().addItem(995, 2500);
}
	
	public static void RunGnomeBoard(Player player) {
		
		if (player.getX() != 2477 || player.getY() != 3418
				|| player.getPlane() != 3)
			return;
		player.addStopDelay(4);
		player.setNextAnimation(new Animation(2922));
		player.addWalkSteps(2484, 3418, -1, false);
	//	final WorldTile toTile = new WorldTile(2484, 3418, object.getPlane());
	//	player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3, ForceMovement.EAST));
		
		player.getSkills().addXp(Skills.AGILITY, 22);
		player.getPackets().sendGameMessage("You skilfully run across the Board", true);
	}

	public static void walkBackGnomeRope(final Player player) {
		if (player.getX() != 2483 || player.getY() != 3420
				|| player.getPlane() != 2)
			return;
		final boolean running = player.isRunning();
		player.setRunHidden(false);
		player.addStopDelay(7);
		player.addWalkSteps(2477, 3420, -1, false);
		player.getInventory().addItem(995, 5000);
		WorldTasksManager.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearence().setRenderEmote(155);
				} else {
					player.getAppearence().setRenderEmote(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Skills.AGILITY, 25);
					player.getInventory().addItem(995, 5000);
					player.getPackets().sendGameMessage(
							"You passed the obstacle succesfully.", true);
					stop();
				}
			}
		}, 0, 5);
	}

	public static void walkGnomeRope(final Player player) {
		if (player.getX() != 2477 || player.getY() != 3420
				|| player.getPlane() != 2)
			return;
		final boolean running = player.isRunning();
		player.setRunHidden(false);
		player.addStopDelay(7);
		player.addWalkSteps(2483, 3420, -1, false);
		player.getInventory().addItem(995, 5000);
		WorldTasksManager.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearence().setRenderEmote(155);
				} else {
					player.getAppearence().setRenderEmote(-1);
					player.setRunHidden(running);
					if (getGnomeStage(player) == 2)
						setGnomeStage(player, 3);
					player.getSkills().addXp(Skills.AGILITY, 55);
					player.getInventory().addItem(995, 5000);
					player.getPackets().sendGameMessage(
							"You passed the obstacle succesfully.", true);
					stop();
				}
			}
		}, 0, 5);
	}

	public static void climbDownGnomeTreeBranch(final Player player) {
		player.useStairs(828, new WorldTile(2487, 3421, 0), 1, 2,
				"You climbed the tree branch succesfully.");
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 3)
					setGnomeStage(player, 4);
				player.getSkills().addXp(Skills.AGILITY, 55);
				player.getInventory().addItem(995, 5000);
			}
		}, 1);
	}

	public static void climbGnomeObstacleNet2(final Player player) {
		if (player.getY() != 3425)
			return;
		player.getPackets().sendGameMessage("You climb the netting.", true);
		player.useStairs(828, new WorldTile(player.getX(),
				player.getY() == 3425 ? 3428 : 3425, 0), 1, 2);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 4)
					setGnomeStage(player, 5);
				player.getSkills().addXp(Skills.AGILITY, 34);
				player.getInventory().addItem(995, 5000);
			}
		}, 1);
	}

	public static void enterGnomePipe(final Player player, int objectX,
			int objectY) {
		final boolean running = player.isRunning();
		player.setRunHidden(false);
		player.addStopDelay(8);
		player.addWalkSteps(objectX, objectY == 3431 ? 3437 : 3430, -1, false);
		player.getPackets().sendGameMessage(
				"You pulled yourself through the pipes.", true);
		
		WorldTasksManager.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearence().setRenderEmote(295);
				} else {
					player.getAppearence().setRenderEmote(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Skills.AGILITY, 45);
					
					if (getGnomeStage(player) == 5) {
						removeGnomeStage(player);
						player.getSkills().addXp(Skills.AGILITY, 600);
						player.AGILITY_LAPS_DONE++;
						player.getInventory().addItem(2996, (Misc.random(6)));
						
						
						if (player.AGILITY_LAPS_DONE == 250) {
							for (Player players : World.getPlayers()) {
								players.getPackets().sendGameMessage(
										"<img=8> <col=996600>"
										              + player.getDisplayName() + " has just completed 250 agility laps!");
						}
						}
						player.getInventory().addItem(995, 100000);

					}
					stop();
				}
			}
		}, 0, 6);
	}

	public static void removeGnomeStage(Player player) {
		player.getTemporaryAttributtes().remove("GnomeCourse");
	}

	public static void setGnomeStage(Player player, int stage) {
		player.getTemporaryAttributtes().put("GnomeCourse", stage);
	}

	public static int getGnomeStage(Player player) {
		Integer stage = (Integer) player.getTemporaryAttributtes().get(
				"GnomeCourse");
		if (stage == null)
			return -1;
		return stage;
	}

	
	

}
