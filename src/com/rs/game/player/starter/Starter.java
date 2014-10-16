package com.rs.game.player.starter;

import com.rs.game.player.Player;

public class Starter {

	public static final int MAX_STARTER_COUNT = 2;

	public static void appendStarter(Player player) {
		String ip = player.getSession().getIP();
		int count = StarterMap.getSingleton().getCount(ip);
		player.starter = 1;
		if (count >= MAX_STARTER_COUNT) {
			return;
		}

		player.getInventory().addItem(995, 25000000); // 2.5M


		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getMusicsManager().reset();
		player.getCombatDefinitions().setAutoRelatie(false);
		player.getCombatDefinitions().refreshAutoRelatie();
		StarterMap.getSingleton().addIP(ip);
	}
}
