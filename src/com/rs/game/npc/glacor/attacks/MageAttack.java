package com.rs.game.npc.glacor.attacks;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.glacor.Glacor;
import com.rs.game.npc.glacor.GlacorAttacks;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * 
 * @author Tyler <email>tyler@xlitersps.com</email> Represents the Glacors Magic
 *         Attack.
 */
public class MageAttack implements GlacorAttacks {
	// GFX: 739 962 739 963 902 905 899 634
	// Animations: 9968 9967
	ArrayList<WorldTile> tile = new ArrayList<WorldTile>();

	@Override
	public int attack(final Glacor glacor, final Entity target) {
		target.setAttackedBy(glacor);
		glacor.setNextAnimation(new Animation(9967));
		int SPEED = 25;
		glacor.setNextGraphics(new Graphics(905));
		World.sendProjectile(glacor, target, 634, 60, 40, SPEED, 30, 12, 0);
		int time = Utils.getDistance(glacor, target) / SPEED;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (target instanceof Player) {
					final Player player = (Player) target;
					if (!player.getPrayer().usingPrayer(
							player.getPrayer().isAncientCurses() ? 1 : 0,
							player.getPrayer().isAncientCurses() ? 7 : 17)
							&& Utils.random(15) == 0) {
						player.sendMessage("<col=01A9DB>The glacor fires a freezing attack!</col>");
						if (!tile.contains(new WorldTile(target)))
							tile.add(new WorldTile(target));
						World.sendGraphics(target, new Graphics(369),
								tile.get(0));
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								if (target.matches(tile.get(0))
										&& !tile.isEmpty()) {
									target.applyHit(new Hit(glacor, Utils
											.random(200, 400),
											HitLook.MAGIC_DAMAGE));
									target.addFreezeDelay(1000, false);
									player.sendMessage("You've been hit by the glacors freezing attack, it fires a punishing attack!");

								} else {
									player.sendMessage("You break free from the ice!");
								}
								this.stop();
								tile.clear();
							}

						}, 3);
					}
				}

				glacor.getCombat()
						.delayHit(
								glacor,
								1,
								target,
								new Hit(glacor, Utils.random(312),
										HitLook.MAGIC_DAMAGE));
				this.stop();

			}
		}, time);
		return 3;
	}

}