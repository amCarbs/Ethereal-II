package com.rs.game.player.dialogues;

public class AncientAltar extends Dialogue {

	@Override
	public void start() {
		if (player.getCombatDefinitions().getSpellBook() != 193) {
			player.getCombatDefinitions().setSpellBook(1);
			player.getPackets().sendGameMessage("You're now on the ancient spell book.");
		} else {
			player.getCombatDefinitions().setSpellBook(0);
			player.getPackets().sendGameMessage("You're now on the normal spell book.");
		}
	}

	public void run(int interfaceId, int componentId) {
		if (interfaceId == SEND_2_OPTIONS && componentId == 1) {
			if (player.getCombatDefinitions().getSpellBook() != 193) {
				sendDialogue(SEND_2_TEXT_CHAT, "",
						"Your mind clears and you switch",
						"back to the ancient spellbook.");
				player.getCombatDefinitions().setSpellBook(1);
			} else {
				sendDialogue(SEND_2_TEXT_CHAT, "",
						"Your mind clears and you switch",
						"back to the normal spellbook.");
				player.getCombatDefinitions().setSpellBook(0);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
