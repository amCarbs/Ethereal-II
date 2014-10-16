package com.rs.game.player.content;

import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;
/**
 * @author JazzyYaYaYa | Fuzen Seth | Nexon
 */
public class DisplayNameAction {
	

	
	public static void RemoveDisplay (Player player) {
		player.setDisplayName(Utils.formatPlayerNameForDisplay(player.getUsername()));
		player.getInterfaceManager().closeChatBoxInterface();
		SerializableFilesManager.savePlayer(player);
		player.getAppearence().generateAppearenceData();
		player.sm("Display name removed, if you don't see your original name please relog.");
	}


	
}