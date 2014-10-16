package com.rs.game.player.dialogues;

public class ZarosAltar extends Dialogue {

	@Override
	public void start() {
		if (!player.getPrayer().isAncientCurses()) {
			player.getPrayer().setPrayerBook(true);
			player.getPackets().sendGameMessage("You're now on the curses prayer book.");
		} else {
			player.getPrayer().setPrayerBook(false);
			player.getPackets().sendGameMessage("You're now on the normal prayer book.");
			}
		}

	public void run(int interfaceId, int componentId) {
		if (interfaceId == SEND_2_OPTIONS && componentId == 1) {
			if (!player.getPrayer().isAncientCurses()) {
				sendDialogue(
						SEND_3_TEXT_CHAT,
						"",
						"The altar fills your head with dark thoughts, purging the",
						"prayers from your memory and leaving only curses in",
						" their place.");
				player.getPrayer().setPrayerBook(true);
			} else {
				sendDialogue(
						SEND_2_TEXT_CHAT,
						"",
						"The altar eases its grip on your mid. The curses slip from",
						"your memory and you recall the prayers you used to know.");
				player.getPrayer().setPrayerBook(false);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
