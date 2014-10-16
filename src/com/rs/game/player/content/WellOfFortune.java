package com.rs.game.player.content;


import com.rs.game.player.Player;
import com.rs.utils.Utils;


public class WellOfFortune {


	public static void handleWell(Player player) {
		
		if (player.getBoostTime() > Utils.currentTimeMillis()) {
			player.sendMessage("<col=FF0000>You already have an active boost of "+Utils.getPercent(player.getDropBoost())+"% that expires in "+Utils.getTimeLeft(player.getBoostTime())+".");
			return;
		}
		
		player.getTemporaryAttributtes().put("well_donate", Boolean.TRUE);
		player.getPackets().sendRunScript(108, "Enter the amount you wish to drop in the well:");
	}
	
	public static int minAmount = 1000000;
	
	public static void handleBoost(Player player, int amount) {
		if (amount < 0 || amount > Integer.MAX_VALUE)
			return;
		
		if (!player.getInventory().containsItem(995, amount)) {
			player.sendMessage("You do not have enough coin to drop in the well.");
			return;
		}
		
		double boost = 1.00;
		long time = 0;
		
		if (amount < minAmount) {
			player.sendMessage("The Well of Good Fortune refuses your donation.");
			return;
		}
		
		if (amount < 100000000) {
			boost = 1.05;
			time = Utils.currentTimeMillis() + (1000* 60 * 5);
		} else if (amount < 250000000) {
			boost = 1.10;
			time = Utils.currentTimeMillis() + (1000* 60 * 10);
		} else if (amount < 500000000) {
			boost = 1.15;
			time = Utils.currentTimeMillis() + (1000* 60 * 15);
		} else if (amount < 750000000) {
			boost = 1.20;
			time = Utils.currentTimeMillis() + (1000* 60 * 20);
		} else if (amount < 100000000) {
			boost = 1.25;
			time = Utils.currentTimeMillis() + (1000* 60 * 25);
		} else if (amount < Integer.MAX_VALUE) {
			boost = 1.30;
			time = Utils.currentTimeMillis() + (1000* 60 * 30);
		}
		
		player.setDropBoost(boost);
		player.setBoostTime(time);
		player.setTotalDonatedToWell(player.getTotalDonatedToWell() + amount);
		player.getInventory().deleteItem(995, amount); 
		
		player.sendMessage("<col=0000FF>You've received a "+Utils.getPercent(boost)+"% drop boost for "+Utils.getTimeLeft(player.getBoostTime())+"!");
		player.sendMessage("<col=0000FF>You've donated a total of "+Utils.formatNumber(player.getTotalDonatedToWell())+" gold to the Well of Good Fortune!");
	}
}