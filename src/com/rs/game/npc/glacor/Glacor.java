package com.rs.game.npc.glacor;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.glacor.attacks.MageAttack;
import com.rs.game.npc.glacor.attacks.MeleeAttack;
import com.rs.game.npc.glacor.attacks.RangeAttack;
import com.rs.game.npc.glacor.attacks.SpecialRangeAttack;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * 
 * @author Tyler Telis <tyler@xlitersps.com>
 * @version 1.0
 */
public class Glacor extends NPC {

	/**
	 * Generated SerialUID
	 */
	private static final long serialVersionUID = 764009624457994527L;
	/**
	 * Checks if the minions are spawned.
	 */
	private boolean minionsSummoned;
	/**
	 * The minions array.
	 */
	private NPC[] minions;

	/**
	 * GlacorAttacks array.
	 */
	public GlacorAttacks[] attacks = { new MageAttack(), new RangeAttack() };

	/**
	 * Glacors current inherited effect.
	 */
	private GlacorEffect effect;
	private final int EXPLODE_GFX = 956;
	private Effect effectHandler;
	/**
	 * Current GlacorAttack
	 */
	private GlacorAttacks attack, lastAttack;
	/**
	 * Enduring Glacyte.
	 */
	private EnduringGlacyte enduring;
	/**
	 * Sapping Glacyte.
	 */
	private SappingGlacyte sapping;
	/**
	 * Unstable Glacyte.
	 */
	private UnstableGlacyte unstable;
	/**
	 * Glacors attack delay.
	 */
	private int attackDelay;
	/**
	 * Attack Switch Delay.
	 */
	private int switchAttackDelay;
	/**
	 * Glacors current target.
	 */
	private Entity target;
	/**
	 * Holds the Glacytes.
	 */
	private ArrayList<NPC> glacytes = new ArrayList<NPC>();

	private boolean isHealing;
	private boolean exploding;

	/**
	 * Contructs a the 'Glacor' Class.
	 * 
	 * @param id
	 * @param tile
	 * @param mapAreaNameHash
	 * @param canBeAttackFromOutOfArea
	 */
	public Glacor(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.setAtMultiArea(true);
		this.setCantFollowUnderCombat(true);
		this.minionsSummoned = false;
		this.attackDelay = 6;
		this.attack = attacks[Utils.random(attacks.length)];
		this.lastAttack = attack;

	}

	/**
	 * Checks if the glacor is attackable
	 * 
	 * @return
	 */
	public boolean attackable(Player player) {
		return this.target.getIndex() == player.getIndex()
				|| player.getAttackedByDelay() > Utils.currentTimeMillis();
	}

	private void explode() {
		applyHit(new Hit(null, (int) (this.getHitpoints() * 0.90),
				HitLook.REGULAR_DAMAGE));
		setNextGraphics(new Graphics(EXPLODE_GFX));
		if (target == null)
			return;
		for (Entity entity : getPossibleTargets()) {
			if (entity == null || this.isDead())
				continue;
			if (entity.withinDistance(this, 5)) {
				entity.applyHit(new Hit(null,
						(int) (target.getHitpoints() * 0.33),
						HitLook.REGULAR_DAMAGE));
			}
		}
	}

	public void followTarget(Player player) {
		if (getLastFaceEntity() != player.getClientIndex()) {
			setNextFaceEntity(player);
		}
		if (getFreezeDelay() >= Utils.currentTimeMillis()) {
			return;
		}
		int size = getSize();
		int distanceX = player.getX() - getX();
		int distanceY = player.getY() - getY();
		if (distanceX < size && distanceX > -1 && distanceY < size
				&& distanceY > -1 && !player.hasWalkSteps() && !hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(player.getX() + 1, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(player.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), player.getY() + 1)) {
						resetWalkSteps();
						addWalkSteps(getX(), player.getY() - size);
					}
				}
			}
			return;
		}
		if ((!clipedProjectile(player, true)) || distanceX > size
				|| distanceX < -1 || distanceY > size || distanceY < -1) {
			resetWalkSteps();
			addWalkStepsInteract(player.getX(), player.getY(),
					getRun() ? 2 : 1, size, true);
			return;
		}
		resetWalkSteps();
	}

	/**
	 * @return the attackDelay
	 */
	public int getAttackDelay() {
		return attackDelay;
	}

	/**
	 * @return the effect
	 */
	public GlacorEffect getEffect() {
		return effect;
	}

	public Effect getEffectHandler() {
		return effectHandler;
	}

	/**
	 * @return the glacytes
	 */
	public ArrayList<NPC> getGlacytes() {
		return glacytes;
	}

	/**
	 * Gets the glacors minions
	 * 
	 * @return
	 */
	public NPC[] getMinions() {
		return minions;
	}

	/**
	 * @return the switchAttackDelay
	 */
	public int getSwitchAttackDelay() {
		return switchAttackDelay;
	}

	/**
	 * Get the current target.
	 * 
	 * @return
	 */
	public Entity getTarget() {
		return target;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		Entity target = hit.getSource();
		if (target instanceof Player) {
			if (getGlacytes().size() != 0 && target != null) {
				hit.setDamage(0);
			}
		}
		if (target instanceof Familiar) {
			if (getGlacytes().size() != 0 && target != null) {
				Familiar npc = (Familiar) target;
				npc.resetCombat();
				npc.call();
				hit.setDamage(0);
			}
		}
		if (effect == GlacorEffect.ENDURING)
			hit.setDamage((int) Utils.getPercent(60, hit.getDamage()));
		super.handleIngoingHit(hit);
	}

	/**
	 * @return the isHealing
	 */
	public boolean isHealing() {
		return isHealing;
	}

	/**
	 * @return the minionsSummoned
	 */
	public boolean isMinionsSummoned() {
		return minionsSummoned;
	}

	// 4188 5720 - coords
	/**
	 * Process the spawning of the minions.
	 */
	private void processMinionSpawns() {
		enduring = new EnduringGlacyte(this, new WorldTile(this.getX() + 3,
				this.getY() + 1, this.getPlane()), target);
		sapping = new SappingGlacyte(this, new WorldTile(this.getX() + 2,
				this.getY() + 1, this.getPlane()), target);
		unstable = new UnstableGlacyte(this, new WorldTile(this.getX() + 1,
				this.getY() + 1, this.getPlane()), target);
		this.getGlacytes().add(enduring);
		this.getGlacytes().add(sapping);
		this.getGlacytes().add(unstable);
	}

	@Override
	public void processNPC() {
		target = this.getCombat().getTarget();
		if (target != null) {
			if (target instanceof Familiar) {
				Familiar npc = (Familiar) target;
				target = npc.getOwner();
				npc.resetCombat();
			}
			if (this.isDead()) {
				for (NPC npc : getGlacytes()) {
					if (npc == null)
						continue;
					this.getGlacytes().remove(npc);
					npc.sendDeath(this);
				}
				this.getCombat().reset();
				this.setMinionsSummoned(false);
			}
			if (target instanceof Player) {
				Player player = (Player) target;
				if (this.withinDistance(player, 1)) {
					followTarget(player);
				}
				if (target.isDead() || player == null || player.hasFinished()
						|| !player.isRunning()
						|| !player.withinDistance(this, 100)) {
					getGlacytes().clear();
					this.getCombat().reset();
					this.setTarget(null);
					this.getCombat().setTarget(null);
					this.setMinionsSummoned(false);
					this.heal(30000);
					if (sapping != null)
						sapping.sendDeath(null);
					if (enduring != null)
						enduring.sendDeath(null);
					if (unstable != null)
						unstable.sendDeath(null);
					this.setAttackDelay(effect == GlacorEffect.ENDURING ? 5 : 6);
				}
			}

		}
		if (getCombat().underCombat()) {
			this.attackDelay--;
			this.switchAttackDelay--;
		}
		if (target != null && !target.withinDistance(this, 100)
				&& this.getCombat().underCombat()) {
			this.getCombat().removeTarget();
			this.getCombat().reset();
			this.setMinionsSummoned(false);
			this.setHitpoints(this.getMaxHitpoints());
			this.setAttackDelay(effect == GlacorEffect.ENDURING ? 5 : 6);
			this.getGlacytes().clear();
		}
		if (this.getAttackDelay() == 0 && getCombat().underCombat()) {
			if (target != null && !target.isDead()) {
				if (lastAttack instanceof SpecialRangeAttack) {
					if (clipedProjectile(target, true))
						attack = new RangeAttack();
				} else if (target.withinDistance(this, 2)) {
					if (clipedProjectile(target, true))
						attack = new MeleeAttack();
				} else {
					if (Utils.random(40) == 0) {
						attack = new SpecialRangeAttack();
					} else {
						attack = attacks[Utils.random(attacks.length)];
					}
				}
				if (clipedProjectile(target, true)) {
					attack.attack(this, target);
				}
				lastAttack = attack;

			}
			this.setAttackDelay(effect == GlacorEffect.ENDURING ? 5 : 6);
			lastAttack = attack;
		}
		if (effect == GlacorEffect.UNSTABLE) {
			if (!exploding) {
				exploding = true;
				setSecondBar(new SecondBar(750));
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						explode();
						stop();
					}
				}, 25, 0);
			}
		}
		if (this.getHitpoints() <= this.getMaxHitpoints() / 2 && target != null
				&& !minionsSummoned && this.getCombat().underCombat()) {
			processMinionSpawns();
			setMinionsSummoned(true);
		}
	}

	@Override
	public void sendDeath(Entity target) {
		super.sendDeath(target);
		this.setNextAnimation(new Animation(9961));
		setMinionsSummoned(false);
		this.effect = null;
	}

	/**
	 * @param attackDelay
	 *            the attackDelay to set
	 */
	public void setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
	}

	/**
	 * @param effect
	 *            the effect to set
	 */
	public void setEffect(GlacorEffect effect) {
		this.effect = effect;
	}

	public void setEffectHandler(Effect effectHandler) {
		this.effectHandler = effectHandler;
	}

	/**
	 * @param glacytes
	 *            the glacytes to set
	 */
	public void setGlacytes(ArrayList<NPC> glacytes) {
		this.glacytes = glacytes;
	}

	/**
	 * @param isHealing
	 *            the isHealing to set
	 */
	public void setHealing(boolean isHealing) {
		this.isHealing = isHealing;
	}

	/**
	 * Sets the minions.
	 * 
	 * @param minions
	 */
	public void setMinions(NPC[] minions) {
		this.minions = minions;
	}

	/**
	 * @param minionsSummoned
	 *            the minionsSummoned to set
	 */
	public void setMinionsSummoned(boolean minionsSummoned) {
		this.minionsSummoned = minionsSummoned;
	}

	/**
	 * @param switchAttackDelay
	 *            the switchAttackDelay to set
	 */
	public void setSwitchAttackDelay(int switchAttackDelay) {
		this.switchAttackDelay = switchAttackDelay;
	}

	public void setTarget(Player target) {
		this.target = target;
	}

}