package com.rs.game.player.content;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Utils;
/**
 * @author Fuzen Seth
 * @information Represents the caskets.
 */
public class Caskets {
	
	/**
	 * Casket rewards.
	 */
	private static final int[] ITEM_REWARDS = {1712, 1215, 4587, 1305, 1079, 1127, };
	
	/**
	 * Open a casket.
	 */
	public static final boolean lootCasket(Player player, Item item) {
		if (player.isDead())
			return false;
		
		
		
		
		
		if (!player.getInventory().containsItem(405,1)) {
			player.getInventory().deleteItem(405, 1);
			System.out.println("got here!");
			
			player.addStopDelay(1);
			player.sendMessage("You loot the casket!");
			switch (Utils.getRandom(1)) {
			case 0: // The gold coin reward.
				if (!player.isDonator())
				player.getInventory().addItem(995, Utils.random(50000, 150000));
				else
					player.getInventory().addItem(995, Utils.random(75000, 190000));
				return true;
			case 1: //The item reward.
				player.getInventory().addItem(ITEM_REWARDS[Utils.getRandom(ITEM_REWARDS.length - 1)], 1);
				return true;
			}
		}
		return false;
	}
	
}