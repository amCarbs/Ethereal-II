package com.rs.game.npc.fightcave;

import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.utils.Utils;

/**
 * 
 * @author Flaborgasted
 * 
 */
@SuppressWarnings("serial")
public class Zombiecave extends NPC {

	public Zombiecave(int id, WorldTile tile) {
		super(id, tile, Utils.getNameHash("Zombies"), true, true);
	}

}