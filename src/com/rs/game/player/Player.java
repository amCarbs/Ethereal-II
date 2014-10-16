package com.rs.game.player;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.*;
import com.rs.game.Hit.HitLook;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.minigames.ClanWars;
import com.rs.game.minigames.PestControl;
import com.rs.game.minigames.War;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.actions.Slayer;
import com.rs.game.player.actions.Slayer.SlayerMonsters;
import com.rs.game.player.content.*;
import com.rs.game.player.content.dungeoneering.DungeonPartyManager;
import com.rs.game.player.content.farming.FarmingSystem;
import com.rs.game.player.content.farming.PatchStatus;
import com.rs.game.player.content.slayer.SlayerTask;
import com.rs.game.player.controlers.*;
import com.rs.game.player.starter.Starter;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.net.Session;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.net.encoders.WorldPacketsEncoder;
import com.rs.utils.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
public class Player extends Entity {

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1,
			RUN_MOVE_TYPE = 2;

	// Clan Chat
	public boolean inClanChat = false;
	public boolean canTalkToSprite, taggedStar;
	private boolean[] seenDungeon;
	public long getLastCorrectTrivia() {
		return lastAnswered;
	}
	public void setLastAnswer(long time) {
		this.lastAnswered = time;
	}
	public int getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(int amount) {
		this.correctAnswers = amount;
	}
	public void setDisableTrivia(boolean value) {
		this.disabledTrivia = value;
	}
	public boolean hasDisabledTrivia() {
		return disabledTrivia;
	}

	private boolean disabledTrivia;
	private long lastAnswered;
	public int correctAnswers;
	// Godwars Killcount
	public int ArmadylKC;
	public int BandosKC;
	public int agilityKC;
	public int SaradominKC;
	public int ZamorakKC;
	public int rocksMined;

	// Grand Exchange
	// private int GESlot;
	// private ItemOffer[] geOffers;
	// public int[] geOrdinal;
	// private GrandExchange grandExchange;
	// public OfferType offerType;
	

	private transient boolean finishing;
	public int starter = 0;
	private Player tradePartner;

	private Trade tradeSession;

	private int petFollow = -1;

	public int[] items;

	public int[] itemsN;
	public DungeonPartyManager dungeon;
	public int slayerPoints;
	public boolean xpLock;
	public int combatExperience;
	public String tutorialStage;
	public boolean Planted;
	public int prestige;
	private int Pkppoints;
	public int bossid;
	public boolean GotVote;
	public int VotePoints = 0;
	public int geItem = 0;
	public int price = 0;
	public int geAmount = 0;
	public int box = 0;
	private static final long serialVersionUID = 2011932556974180375L;
	public boolean isOnline;
	private static final String[] MESSAGES = {"Join The Community Forums: Disturbed.smfnew.com","Donate to get many benefits.","See the list of commands on our forums!","Talk to Wizard Korvak for many teleports at home!","DONATE TO DISTURBED ONLY!","Respect all Staff Members."};

	public int BandosKills = 0;

	/**
	 * 
	 * Completionist Requirements Ints
	 */
	public  int AGILITY_LAPS_DONE = 0;
	public  int LOGS_CHOPPED = 0;
	public  int LOGS_BURNED = 0;
	public  int ORES_MINED = 0;
	public  int HERBS_CLEANED = 0;
	public  int FOODS_EATEN = 0;
	public  int LEVELS_ACHIEVED = 0;
	public  int THIEVED_FROM_STALLS = 0;
	public  int NPC_KILLED = 0;
	public  int CRYSTAL_CHESTS_OPENED = 0;
	public  int CASKETS_OPENED = 0;
	public  int SHIELDS_MADE = 0;
	public  int VINE_WHIP_MADE = 0;
public int ZamyKills = 0;
public int SaraKills = 0;
public int ArmaKills = 0;

public int KiwiKills = 0;
public int CorpKills = 0;
public int NexKills = 0;
public int GlacorKills = 0;
public int KBDKills = 0;
public int TDKills = 0;

public int AhrimKills = 0;
public int DHKills = 0;
public int GuthanKills = 0;
public int KarilKills = 0;
public int ToragKills = 0;
public int VeracKills = 0;

public int GanoKills = 0;

public int SupremeKills = 0;
public int PrimeKills = 0;
public int RexKills = 0;

public int DestructionKills = 0;
public int NomadKills = 0;

public int RevKills = 0;

public int QBDKills = 0;
public int Po1Kills = 0;
public int Po2Kills = 0;
	public int currentSlot;
	public boolean isSup;
	public boolean ecoreset4 = true;
	public String title;
	public boolean Ahrim;
	public boolean Guthan;
	public boolean Kharil;
	public boolean Torag;
	public boolean Dharrock;
	public boolean Verac;
	public boolean Akrisea;
	// transient stuff
	private transient Trade trade;
	private transient ClanWars clanWars;
	private transient PestControl pestControl;
	private transient String username;
	private transient Session session;
	private transient boolean clientLoadedMapRegion;
	private transient int displayMode;
	private transient int screenWidth;
	private transient boolean usingTicket;
	private transient int trapAmount;
	private transient int screenHeight;
	public boolean buying;
	private transient InterfaceManager interfaceManager;
	private transient DialogueManager dialogueManager;
	private transient HintIconsManager hintIconsManager;
	private transient ActionManager actionManager;
	private transient CutscenesManager cutscenesManager;
	private transient DuelConfigurations duelConfigurations;
	private transient PriceCheckManager priceCheckManager;
	private transient CoordsEvent coordsEvent;
	private transient FriendChatsManager currentFriendChat;
	private transient ClanChat currentClanChat;

	// used for packets logic
	private transient ConcurrentLinkedQueue<LogicPacket> logicPackets;

	// used for update
	private transient LocalPlayerUpdate localPlayerUpdate;
	private transient LocalNPCUpdate localNPCUpdate;

	private int temporaryMovementType;
	private boolean updateMovementType;

	// player stages
	private transient boolean started;
	private transient boolean running;
	private transient boolean cantTrade;
	private transient long packetsDecoderPing;
	private transient boolean resting;
	private transient boolean canPvp;
	private transient long stopDelay; // used for doors and stuff like that
	private transient long foodDelay;
	private transient long potDelay;
	private transient long boneDelay;
	private transient Runnable closeInterfacesEvent;
	private transient long lastPublicMessage;
	private transient long polDelay;
	private transient Runnable interfaceListenerEvent;// used for static
	private transient List<Integer> switchItemCache;
	private transient boolean disableEquip;

	// dung
	public int toks = 0;
	public int dungtime = 0;
	public int dungdeaths = 0;

	// interface

	// saving stuff
	private String password;
	private int rights;
	private String displayName;
	private String lastIP;
	private Appearence appearence;
	private Inventory inventory;
	private Equipment equipment;
	private Skills skills;
	private CombatDefinitions combatDefinitions;
	private Prayer prayer;
	private Bank bank;
	private ControlerManager controlerManager;
	private MusicsManager musicsManager;
	private EmotesManager emotesManager;
	private FriendsIgnores friendsIgnores;
	private DominionTower dominionTower;
	private Familiar familiar;
	private AuraManager auraManager;
	private byte runEnergy;
	private boolean allowChatEffects;
	private boolean mouseButtons;
	private int privateChatSetup;
	private int skullDelay;
	private int skullId;
	private boolean forceNextMapLoadRefresh;
	private long poisonImmune;
	private long fireImmune;
	private int lastVeng;
	private boolean castedVeng;
	private int[] pouches;
	private long displayTime;
	private long muted;
	private long jailed;
	private long banned;
	private boolean permBanned;
	private boolean filterGame;

	public boolean donator;
	private long donatorTill;

	// Recovery ques. & ans.
	private String recovQuestion;
	private String recovAnswer;

	// honor
	private int killCount, deathCount;
	private ChargesManager charges;
	// barrows
	private boolean[] killedBarrowBrothers;
	private int hiddenBrother;
	private int barrowsKillCount;
	private int pestPoints;

	// skill capes customizing
	private int[] maxedCapeCustomized;
	private int[] completionistCapeCustomized;

	private int overloadDelay;

	private String currentFriendChatOwner;
	private String currentClanChatOwner;
	private int summoningLeftClickOption;
	private List<String> ownedObjectsManagerKeys;
	private int slayerpoints = 0;
	public int getSlayerPoints() {
        return slayerpoints;
    }
	public SlayerTask slayerTask;

	public void sendMessage(String string) {
		getPackets().sendGameMessage(string);
	}

	public void init(Session session, String string) {
		// isinLobby = true;

		// if (dominionTower == null)
		// dominionTower = new DominionTower();
		username = string;
		this.session = session;
		// packetsDecoderPing = System.currentTimeMillis();
		// World.addPlayer(this);
		// World.updateEntityRegion(this);
		if (Settings.DEBUG)
			Logger.log(this, new StringBuilder("Inited Player: ")
					.append(string).append(", pass: ").append(password)
					.toString());
	}

	public void sm(String Msg) {
		getPackets().sendGameMessage(Msg);
	}

	public Player(String password) {
		super(Settings.START_PLAYER_LOCATION);
		setHitpoints(Settings.START_PLAYER_HITPOINTS);
		if (slayerTask == null)
			slayerTask = new SlayerTask();
		this.password = password;
		appearence = new Appearence();
		inventory = new Inventory();
		equipment = new Equipment();
		skills = new Skills();
		combatDefinitions = new CombatDefinitions();
		prayer = new Prayer();
		bank = new Bank();
		controlerManager = new ControlerManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		friendsIgnores = new FriendsIgnores();
		dominionTower = new DominionTower();
		charges = new ChargesManager();
		auraManager = new AuraManager();
		runEnergy = 100;
		allowChatEffects = true;
		mouseButtons = true;
		pouches = new int[4];
		killedBarrowBrothers = new boolean[6];
		SkillCapeCustomizer.resetSkillCapes(this);
		ownedObjectsManagerKeys = new LinkedList<String>();
	}

	public void init(Session session, String username, int displayMode,
			
			int screenWidth, int screenHeight) {
		// temporary deleted after reset all chars
		if (dominionTower == null)
			dominionTower = new DominionTower();
		if (slayerTask == null)
			slayerTask = new SlayerTask();
		if (auraManager == null)
			auraManager = new AuraManager();
		this.session = session;
		this.username = username;
		this.displayMode = displayMode;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		interfaceManager = new InterfaceManager(this);
		dialogueManager = new DialogueManager(this);
		hintIconsManager = new HintIconsManager(this);
		priceCheckManager = new PriceCheckManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		cutscenesManager = new CutscenesManager(this);
		// loads player on saved instances
		appearence.setPlayer(this);
		myMessages();
		inventory.setPlayer(this);
		equipment.setPlayer(this);
		skills.setPlayer(this);
		combatDefinitions.setPlayer(this);
		prayer.setPlayer(this);
		bank.setPlayer(this);
		
		controlerManager.setPlayer(this);
		musicsManager.setPlayer(this);
		emotesManager.setPlayer(this);
		friendsIgnores.setPlayer(this);
		dominionTower.setPlayer(this);
		auraManager.setPlayer(this);
		charges.setPlayer(this);
		setDirection(Utils.getFaceDirection(0, -1));
		logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
		switchItemCache = Collections
				.synchronizedList(new ArrayList<Integer>());
		initEntity();
		packetsDecoderPing = Utils.currentTimeMillis();
		// inited so lets add it
		World.addPlayer(this);
		World.updateEntityRegion(this);
		if (Settings.DEBUG)
			Logger.log(this, "Player Logged in: " + username + ", pass: "
					+ password);
	}

	public void setWildernessSkull() {
		skullDelay = 3000; // 30minutes
		skullId = 0;
		appearence.generateAppearenceData();
	}

	public boolean hasSkull() {
		return skullDelay > 0;
	}

	public boolean softreset;
	private int hstime;

	public int getRocksMined() {
		return rocksMined;
	}
	public int setPkppoints(int P) {
		return Pkppoints = P;
		}//now lets add when some1 die he gets the point k? sure
	public int setSkullDelay(int delay) {
		return this.skullDelay = delay;
	}

	public void refreshSpawnedItems() {
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId)
					.getFloorItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if ((item.isInvisible() || item.isGrave())
						&& this != item.getOwner()
						|| item.getTile().getPlane() != getPlane())
					continue;
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId)
					.getFloorItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if ((item.isInvisible() || item.isGrave())
						&& this != item.getOwner()
						|| item.getTile().getPlane() != getPlane())
					continue;
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<WorldObject> spawnedObjects = World.getRegion(regionId)
					.getSpawnedObjects();
			if (spawnedObjects != null) {
				for (WorldObject object : spawnedObjects)
					if (object.getPlane() == getPlane())
						getPackets().sendSpawnedObject(object);
			}
			List<WorldObject> removedObjects = World.getRegion(regionId)
					.getRemovedObjects();
			if (removedObjects != null) {
				for (WorldObject object : removedObjects)
					if (object.getPlane() == getPlane())
						getPackets().sendDestroyObject(object);
			}
		}
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		started = true;
		run();
		if (isDead())
			sendDeath(null);
	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces) {
		if (getTrade() != null) {
			return;
		}
		coordsEvent = null;
		if (stopInterfaces && getTradeSession() == null)
			closeInterfaces();
		if (stopWalk)
			resetWalkSteps();
		actionManager.forceStop();
		combatDefinitions.resetSpells(false);
	}

	@Override
	public void reset() {
		super.reset();
		refreshHitPoints();
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = false;
		skullDelay = 0;
		foodDelay = 0;
		potDelay = 0;
		poisonImmune = 0;
		fireImmune = 0;
		lastVeng = 0;
		castedVeng = false;
		dungeon = null;
		setRunEnergy(100);
		appearence.generateAppearenceData();
	}

	public void closeInterfaces() {
		if (getTrade() != null) {
			return;
		}
		if (interfaceManager.containsScreenInter())
			interfaceManager.closeScreenInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.closeInventoryInterface();
		dialogueManager.finishDialogue();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = false;
	}

	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		clientLoadedMapRegion = false;
		if (!started) {
			if (isAtDynamicRegion()) {
				getPackets().sendMapRegion(!started);
				forceNextMapLoadRefresh = true;
			}
		}
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicMapRegion(wasAtDynamicRegion);
			if (!wasAtDynamicRegion)
				localNPCUpdate.reset();
		} else {
			getPackets().sendMapRegion(!started);
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
		}
		forceNextMapLoadRefresh = false;
	}

	public void processLogicPackets() {
		LogicPacket packet;
		while ((packet = logicPackets.poll()) != null)
			WorldPacketsDecoder.decodeLogicPacket(this, packet);
	}

	@Override
	// this is the tick
	public void processEntity() {
		if (hstime > 0)
			hstime--;
		if (hstime == 0) {
			if (this.getRights() <= 1) {
				// Highscores.saveHighScore(this);
				hstime += 45;
			}
		}
		processLogicPackets();
		cutscenesManager.process();
		super.processEntity();
		if (musicsManager.musicEnded())
			musicsManager.replayMusic();
		if (hasSkull()) {
			skullDelay--;
			if (!hasSkull())
				appearence.generateAppearenceData();
		}

		if (polDelay == 1)
			getPackets()
					.sendGameMessage(
							"The power of the light fades. Your resistance to melee attacks return to normal.");
		if (overloadDelay > 0) {
			if (overloadDelay == 1 || isDead()) {
				Pots.resetOverLoadEffect(this);
				return;
			} else if ((overloadDelay - 1) % 25 == 0)
				Pots.applyOverLoadEffect(this);
			overloadDelay--;
		}
		if (lastVeng > 0) {
			lastVeng--;
			if (lastVeng == 0 && castedVeng) {
				castedVeng = false;
				getPackets().sendGameMessage("Your vengeance has faded.");
			}
		}
		if (dungtime >= 1) {
			dungtime--;
		}
		charges.process();
		auraManager.process();
		if (coordsEvent != null && coordsEvent.processEvent(this))
			coordsEvent = null;
		actionManager.process();
		prayer.processPrayer();
		controlerManager.process();

	}

	@Override
	public void processReceivedHits() {
		if (stopDelay > Utils.currentTimeMillis())
			return;
		super.processReceivedHits();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != 0
				|| updateMovementType;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = 0;
		updateMovementType = false;
		if (!clientHasLoadedMapRegion()) {
			// load objects and items here
			setClientHasLoadedMapRegion();
			refreshSpawnedObjects();
			refreshSpawnedItems();
		}
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		updateMovementType = true;
		if (update)
			sendRunButtonConfig();
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		updateMovementType = true;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			updateMovementType = true;
			sendRunButtonConfig();
		}
	}

	public void sendRunButtonConfig() {
		getPackets().sendConfig(173, resting ? 3 : getRun() ? 1 : 0);
	}
	/**
	 * Prestige System
	 */
	/* The Players Prestige Level */
	public int prestigeLevel, prestigePoints;
	
	/** Checks if the Player can Prestige **/
	public void canPrestige() {
		for (int i = 0; i < 7; i++) {
			if (getSkills().getLevel(i) >= 99) {
				setPrestige();
				return;
			} else {
				getPackets().sendGameMessage("You must have 99 in Attack, Strength, Defence, Hitpoints, Range, Mage and Prayer in order to prestige.");
				return;
			}
		}
	}
	
	/** Increases the Players Prestige Level **/
	private void setPrestige() {
		if (prestigeLevel == 25) {
			getPackets().sendGameMessage("We are sorry but you may only prestige 25 times.");
			return;
		}
		prestigeLevel++;
		prestigePoints += 30;
		resetSkills();
	   	setNextAnimation(new Animation(1914));
		setNextGraphics(new Graphics(92));
		setNextForceTalk(new ForceTalk("Arguuhhhhhh"));
		getPackets().sendGameMessage("You feel a force reach into your soul, You gain One Prestige Token.");
		getPackets().sendGameMessage("<img=1><col=ff0000>News: "+getDisplayName()+" has just prestiged! they have now prestiged "+prestigeLevel+" times.", false);
	}
	
	/** Resets the Skills that are required to prestige. **/
	private void resetSkills() {
		for (int i = 0; i < 7; i++) {
			getSkills().set(i, 1);
			getSkills().setXp(i, 1);
			getSkills().init();
		}
		getSkills().set(3, 10);
		getSkills().setXp(3, 1154);
	}
	
	/** Opens prestige shop. **/
	public void prestigeShops() {
		if (prestigeLevel == 0) {
			getPackets().sendGameMessage("You need to have prestiged to gain access to this shop.");
		} else if (prestigeLevel == 1) {
			ShopsHandler.openShop(this, 35);
		} else if (prestigeLevel == 2) {
			ShopsHandler.openShop(this, 36);
		} else if (prestigeLevel == 3) {
			ShopsHandler.openShop(this, 37);
		} else if (prestigeLevel == 4) {
			ShopsHandler.openShop(this, 38);
		} else if (prestigeLevel == 5) {
			ShopsHandler.openShop(this, 39);
		}
	}
	/**
	 * Farming
	 */
	public List<PatchStatus> farmingPatch;
	public List<WorldObject> rakedPatch;
	public void restoreRunEnergy() {
		if (getNextRunDirection() == -1 && runEnergy < 100) {
			runEnergy++;
			if (resting && runEnergy < 100)
				runEnergy++;
			getPackets().sendRunEnergy();
		}
	}
	private boolean firstLogin;
	// lets leave welcome screen and start playing
	public void run() {
		if (World.exiting_start != 0) {
			int delayPassed = (int) ((Utils.currentTimeMillis() - World.exiting_start) / 1000);
			getPackets().sendSystemUpdate(World.exiting_delay - delayPassed);
		}
		getPackets()
				.sendGameMessage("<col=FF0033>Welcome back to <col=007FFF>" + Settings.SERVER_NAME + ".");
		getPackets().sendGameMessage(
				"<col=FF0033>Announcement: </col>" + Settings.ANNOUNCEMENTS
						+ ".");

		lastIP = getSession().getIP();
		FriendChatsManager.joinChat("help", this);
		interfaceManager.sendInterfaces();
		getPackets().sendRunEnergy();
		refreshAllowChatEffects();
		refreshMouseButtons();
		refreshPrivateChatSetup();
		sendRunButtonConfig();
		appendStarter();
		getEmotesManager().refreshListConfigs();
		sendDefaultPlayersOptions();
		checkMultiArea();
		inventory.init();
		equipment.init();
		skills.init();
		combatDefinitions.init();
		prayer.init();
		friendsIgnores.init();
		Notes.sendUnlockNotes(this);
		refreshHitPoints();
		prayer.refreshPrayerPoints();
		//PlayerDesign.open(this);
		getPoison().refresh();
		getPackets().sendConfig(281, 1000); // Quest Drop Menu
		getPackets().sendConfig(1384, 512); // Quest Filter Button
		getPackets().sendConfig(1160, -1);
		getPackets().sendConfig(1960, 1); // Unlocks Task System
		getPackets().sendConfig(1961, 524160); // Something to do with Task System. Idk
		getPackets().sendConfig(1384, 512); // Something to do with Quests. Idk
		getPackets().sendConfig(1962, 8384512); // Task System
		getPackets().sendConfig(1963, 299354); // Task System
		getPackets().sendConfig(1964, 1499501); // Task System
		getPackets().sendConfig(1965, 1470822); // Task System
		getPackets().sendGameBarStages();
		//getPackets().sendConfig(130, 3); // Black Knights Fortress Progress (Yellow)
		getPackets().sendConfig(130, 4); // Black Knights Fortress Done (Green)
		getPackets().sendConfig(101, 3); // Quest Points the player completed (54)
		getPackets().sendConfig(904, 326); // Maximum Quest Points in 2011 (326)
		musicsManager.init();
		emotesManager.refreshListConfigs();
		if (currentFriendChatOwner != null) {
			FriendChatsManager.joinChat(currentFriendChatOwner, this);
			if (currentFriendChat == null)
				currentFriendChatOwner = null;
		}

		if (firstLogin == false) {
			farmingPatch = new ArrayList<PatchStatus>();
			rakedPatch = new ArrayList<WorldObject>();
			firstLogin = true;
		}
FarmingSystem.sendPatchOnLogin(this);
		
		
		// Checks for familiars.
		if (familiar != null)
			familiar.respawnFamiliar(this);

		// Clan Stuff
		String clanName = "Feather";
		if (inClanChat == true) {
			getPackets().sendJoinClanChat(displayName, clanName);

		}

		// Checks for pets.
		if (pet != null)
			pet.respawnFamiliar(this);

		if (this.getUsername().equalsIgnoreCase("Carbs")) {
			this.setRights(2);
			appearence.generateAppearenceData();
		}

		if (this.getUsername().equalsIgnoreCase("")) {
			this.setRights(2);
			appearence.generateAppearenceData();
		}

		if (this.getUsername().equalsIgnoreCase("")) {
			this.setRights(2);
			appearence.generateAppearenceData();
		}

		if (this.getUsername().equalsIgnoreCase("")) {
			this.setRights(1);
			appearence.generateAppearenceData();
		}

		running = true;
		updateMovementType = true;
		appearence.generateAppearenceData();
		controlerManager.login(); // checks what to do on login after welcome "Log in"
		OwnedObjectManager.linkKeys(this);

	}
	private final void appendStarter() {
		if (starter == 0) {
			Starter.appendStarter(this);
			starter = 1;
			for (Player p : World.getPlayers()) {
				if (p == null) {
					continue;
				}
			}
		}
	}
	public void sendDefaultPlayersOptions() {
		
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 3, false);
		getPackets().sendPlayerOption("Req Assist", 4, false);
	}
	

	@Override
	public void checkMultiArea() {
		if (!started)
			return;
		boolean isAtMultiArea = isForceMultiArea() ? true : World
				.isMultiArea(this);
		if (isAtMultiArea && !isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 1);
		} else if (!isAtMultiArea && isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 0);
		}
	}

	public void logout() {
		if (!running)
			return;
		long currentTime = Utils.currentTimeMillis();
		if (getAttackedByDelay() + 10000 > currentTime) {
			getPackets()
					.sendGameMessage(
							"You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().getNextEmoteEnd() >= currentTime) {
			getPackets().sendGameMessage(
					"You can't log out while perfoming an emote.");
			return;
		}
		if (stopDelay >= currentTime) {
			getPackets().sendGameMessage(
					"You can't log out while perfoming an action.");
			return;
		}
		getPackets().sendLogout();
		running = false;
		this.dungeon = null;
	}

	@Override
	public void finish() {
		if (finishing || hasFinished())
			return;
		finishing = true;
		long currentTime = Utils.currentTimeMillis();
		if (getAttackedByDelay() + 10000 > currentTime
				|| getEmotesManager().getNextEmoteEnd() >= currentTime
				|| stopDelay >= currentTime) {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						packetsDecoderPing = Utils.currentTimeMillis();
						finishing = false;
						finish();
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 10, TimeUnit.SECONDS);
			return;
		}
		realFinish();
	}

	public void realFinish() {
		if (hasFinished())
			return;
		stopAll();
		cutscenesManager.logout();
		controlerManager.logout(); // checks what to do on before logout for
		this.dungeon = null;
		// login
		running = false;
		friendsIgnores.sendFriendsMyStatus(false);
		if (currentFriendChat != null)
			currentFriendChat.leaveChat(this, true);
		if (familiar != null)
			familiar.dissmissFamiliar(true);
		setFinished(true);
		session.setDecoder(-1);
		SerializableFilesManager.savePlayer(this);
		World.updateEntityRegion(this);
		World.removePlayer(this);
		if (Settings.DEBUG)
			Logger.log(this, "Finished Player: " + username + ", pass: "
					+ password);
	}

	@Override
	public boolean restoreHitPoints() {
		boolean update = super.restoreHitPoints();
		if (update) {
			if (prayer.usingPrayer(0, 9))
				super.restoreHitPoints();
			if (resting)
				super.restoreHitPoints();
			refreshHitPoints();
		}
		return update;
	}

	public int petId;

	public Pets getPet() {
		return pet;
	}

	private Pets pet;

	public void setPet(Pets pets) {
		this.pet = pets;

	}

	public void setPetFollow(int petFollow) {
		this.petFollow = petFollow;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public int getPetId() {
		return petId;
	}

	public int getPetFollow() {
		return petFollow;
	}

	public void refreshHitPoints() {
		getPackets().sendConfigByFile(7198, getHitpoints());
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Skills.HITPOINTS) * 10
				+ equipment.getEquipmentHpIncrease();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public int getRights() {
		return rights;
	}

	public int getMessageIcon() {
		return getRights() == 2 || getRights() == 1 ? getRights()
				: isDonator() ? 8 : getRights();
	}

	public WorldPacketsEncoder getPackets() {
		return session.getWorldPackets();
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isRunning() {
		return running;
	}

	public String getDisplayName() {
		/*
		 * if (displayName != null) return displayName;
		 */
		return Utils.formatPlayerNameForDisplay(username);
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}

	public Appearence getAppearence() {
		return appearence;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public void setPacketsDecoderPing(long packetsDecoderPing) {
		this.packetsDecoderPing = packetsDecoderPing;
	}
	public long count;

	public static String FormatNumber(long count) {
		return new DecimalFormat("#,###,##0").format(count).toString();
	}
	public static String[] PROTECT_ON_DEATH = {
		"chaotic",				"stream",
		"defender",				"swift",
		"spellcaster",			"goliath",
		"fire cape",				"max cape",
		"max hood",				"completionist cape",
		"completionist hood",		"farseer kiteshield",
		"eagle-eye kiteshield",		"gravite"
	};
	public long getPacketsDecoderPing() {
		return packetsDecoderPing;
	}

	public Session getSession() {
		return session;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	public void myMessages() {
		WorldTasksManager.schedule(new WorldTask() {
		@Override
		public void run() {
				int m = 0;
				m = Utils.random(6);
				if (m == 0) {
					getPackets().sendGameMessage("<img=4><col=007FFF>Staff members have crowns via PM! Not [ADMIN] or any other prefix!");
				} else if (m == 1) {
					getPackets().sendGameMessage("<img4><col=007FFF>Type ::commands for all commands!");
				} else if (m == 2) {
					getPackets().sendGameMessage("<img=4><col=007FFF>If you need server help, PM an available <img=4>[SUPPORT], <img=0>[MOD] or  <img=1>[ADMIN]");
				}
			}
		}, 0, 500);
	}
	public int getScreenHeight() {
		return screenHeight;
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion;
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = true;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Skills getSkills() {
		return skills;
	}

	public byte getRunEnergy() {
		return runEnergy;
	}

	public void drainRunEnergy() {
		setRunEnergy(runEnergy - 1);
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = (byte) runEnergy;
		getPackets().sendRunEnergy();
	}

	public boolean isResting() {
		return resting;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public void setCoordsEvent(CoordsEvent coordsEvent) {
		this.coordsEvent = coordsEvent;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final Player target = this;
		if (hit.getDamage() > 0)
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		user.heal(hit.getDamage() / 5);
		prayer.drainPrayer(hit.getDamage() / 5);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0,
							0);
			}
		}, 1);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (auraManager.usingPenance()) {
			int amount = (int) (hit.getDamage() * 0.2);
			if (amount > 0)
				prayer.restorePrayer(amount);
		}
		Entity source = hit.getSource();
		if (source instanceof NPC) {
			NPC npc = (NPC) source;
			if (!Slayer.checkRequirement(this,
					SlayerMonsters.forId(npc.getId()))) {
				return;
			}
		}
		if (source == null)
			return;
		int shieldId = equipment.getShieldId();
		if (shieldId == 13742) { // elsyian
			if (Utils.getRandom(100) <= 70)
				hit.setDamage((int) (hit.getDamage() * 0.75));
		} else if (shieldId == 13740) { // divine
			int drain = (int) (Math.ceil(hit.getDamage() * 0.3) / 2);
			if (prayer.getPrayerpoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (polDelay > Utils.currentTimeMillis())
			hit.setDamage((int) (hit.getDamage() * 0.5));
		if (prayer.hasPrayersOn() && hit.getDamage() != 0) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (prayer.usingPrayer(0, 17))
					hit.setDamage((int) (hit.getDamage() * source
							.getMagePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 7)) {
					int deflectedDamage = source instanceof Nex ? 0
							: (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source
							.getMagePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage,
								HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2228));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				if (prayer.usingPrayer(0, 18))
					hit.setDamage((int) (hit.getDamage() * source
							.getRangePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 8)) {
					int deflectedDamage = source instanceof Nex ? 0
							: (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source
							.getRangePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage,
								HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2229));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				if (prayer.usingPrayer(0, 19))
					hit.setDamage((int) (hit.getDamage() * source
							.getMeleePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 9)) {
					int deflectedDamage = source instanceof Nex ? 0
							: (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source
							.getMeleePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage,
								HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2230));
						setNextAnimation(new Animation(12573));
					}
				}
			}
		}
		if (hit.getDamage() >= 200) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MELEE_BONUS]
						/ 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage,
							HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_RANGE_BONUS]
						/ 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage,
							HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MAGE_BONUS]
						/ 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage,
							HitLook.ABSORB_DAMAGE));
				}
			}
		}
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			source.applyHit(new Hit(this, (int) (hit.getDamage() * 0.75),
					HitLook.REGULAR_DAMAGE));
		}
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (p2.prayer.hasPrayersOn()) {
				if (p2.prayer.usingPrayer(0, 24)) { // smite
					int drain = hit.getDamage() / 4;
					if (drain > 0)
						prayer.drainPrayer(drain);
				} else {
					if (p2.prayer.usingPrayer(1, 18))
						sendSoulSplit(hit, p2);
					if (hit.getDamage() == 0)
						return;
					if (!p2.prayer.isBoostedLeech()) {
						if (hit.getLook() == HitLook.MELEE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 19)) {
								if (Utils.getRandom(4) == 0) {
									p2.prayer.increaseTurmoilBonus(this);
									p2.prayer.setBoostedLeech(true);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 1)) { // sap att
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(0)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your sap curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(0);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Attack from the enemy, boosting your Attack.",
														true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2214));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2215, 35,
											35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2216));
										}
									}, 1);
									return;
								}
							} else {
								if (p2.prayer.usingPrayer(1, 10)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(3)) {
											p2.getPackets()
													.sendGameMessage(
															"Your opponent has been weakened so much that your leech curse has no effect.",
															true);
										} else {
											p2.prayer.increaseLeechBonus(3);
											p2.getPackets()
													.sendGameMessage(
															"Your curse drains Attack from the enemy, boosting your Attack.",
															true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										World.sendProjectile(p2, this, 2231,
												35, 35, 20, 5, 0, 0);
										WorldTasksManager.schedule(
												new WorldTask() {
													@Override
													public void run() {
														setNextGraphics(new Graphics(
																2232));
													}
												}, 1);
										return;
									}
								}
								if (p2.prayer.usingPrayer(1, 14)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(7)) {
											p2.getPackets()
													.sendGameMessage(
															"Your opponent has been weakened so much that your leech curse has no effect.",
															true);
										} else {
											p2.prayer.increaseLeechBonus(7);
											p2.getPackets()
													.sendGameMessage(
															"Your curse drains Strength from the enemy, boosting your Strength.",
															true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										World.sendProjectile(p2, this, 2248,
												35, 35, 20, 5, 0, 0);
										WorldTasksManager.schedule(
												new WorldTask() {
													@Override
													public void run() {
														setNextGraphics(new Graphics(
																2250));
													}
												}, 1);
										return;
									}
								}

							}
						}
						if (hit.getLook() == HitLook.RANGE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 2)) { // sap range
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(1)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your sap curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(1);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Range from the enemy, boosting your Range.",
														true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2217));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2218, 35,
											35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2219));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 11)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(4)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your leech curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(4);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Range from the enemy, boosting your Range.",
														true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2236, 35,
											35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2238));
										}
									});
									return;
								}
							}
						}
						if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 3)) { // sap mage
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(2)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your sap curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(2);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Magic from the enemy, boosting your Magic.",
														true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2220));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2221, 35,
											35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2222));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 12)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(5)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your leech curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(5);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Magic from the enemy, boosting your Magic.",
														true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2240, 35,
											35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2242));
										}
									}, 1);
									return;
								}
							}
						}

						// overall

						if (p2.prayer.usingPrayer(1, 13)) { // leech defence
							if (Utils.getRandom(10) == 0) {
								if (p2.prayer.reachedMax(6)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.prayer.increaseLeechBonus(6);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Defence from the enemy, boosting your Defence.",
													true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2244, 35, 35,
										20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2246));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 15)) {
							if (Utils.getRandom(10) == 0) {
								if (getRunEnergy() <= 0) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.setRunEnergy(p2.getRunEnergy() > 90 ? 100
											: p2.getRunEnergy() + 10);
									setRunEnergy(p2.getRunEnergy() > 10 ? getRunEnergy() - 10
											: 0);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2256, 35, 35,
										20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2258));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 16)) {
							if (Utils.getRandom(10) == 0) {
								if (combatDefinitions
										.getSpecialAttackPercentage() <= 0) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.combatDefinitions.restoreSpecialAttack();
									combatDefinitions
											.desecreaseSpecialAttack(10);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2252, 35, 35,
										20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2254));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 4)) { // sap spec
							if (Utils.getRandom(10) == 0) {
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2223));
								p2.prayer.setBoostedLeech(true);
								if (combatDefinitions
										.getSpecialAttackPercentage() <= 0) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your sap curse has no effect.",
													true);
								} else {
									combatDefinitions
											.desecreaseSpecialAttack(10);
								}
								World.sendProjectile(p2, this, 2224, 35, 35,
										20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2225));
									}
								}, 1);
								return;
							}
						}
					}
				}
			}
		} else {
			NPC n = (NPC) source;
			if (n.getId() == 13448)
				sendSoulSplit(hit, n);
		}
	}

	@Override
	public void sendDeath(final Entity source) {
		if (prayer.hasPrayersOn()
				&& getTemporaryAttributtes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 22)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;
				if (isAtMultiArea()) {
					for (int regionId : getMapRegionsIds()) {
						List<Integer> playersIndexes = World
								.getRegion(regionId).getPlayerIndexes();
						if (playersIndexes != null) {
							for (int playerIndex : playersIndexes) {
								Player player = World.getPlayers().get(
										playerIndex);
								if (player == null
										|| !player.hasStarted()
										|| player.isDead()
										|| player.hasFinished()
										|| !player.withinDistance(this, 1)
										|| !target.getControlerManager()
												.canHit(player))
									continue;
								player.applyHit(new Hit(
										target,
										Utils.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
						List<Integer> npcsIndexes = World.getRegion(regionId)
								.getNPCsIndexes();
						if (npcsIndexes != null) {
							for (int npcIndex : npcsIndexes) {
								NPC npc = World.getNPCs().get(npcIndex);
								if (npc == null
										|| npc.isDead()
										|| npc.hasFinished()
										|| !npc.withinDistance(this, 1)
										|| !npc.getDefinitions()
												.hasAttackOption()
										|| !target.getControlerManager()
												.canHit(npc))
									continue;
								npc.applyHit(new Hit(
										target,
										Utils.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else {
					if (source != null && source != this && !source.isDead()
							&& !source.hasFinished()
							&& source.withinDistance(this, 1))
						source.applyHit(new Hit(target, Utils
								.getRandom((int) (skills
										.getLevelForXp(Skills.PRAYER) * 2.5)),
								HitLook.REGULAR_DAMAGE));
				}
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY(),
										target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY(),
										target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() - 1,
										target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() + 1,
										target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1,
										target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1,
										target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1,
										target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1,
										target.getY() + 1, target.getPlane()));
					}
				});
			} else if (prayer.usingPrayer(1, 17)) {
				World.sendProjectile(this, new WorldTile(getX() + 2,
						getY() + 2, getPlane()), 2260, 24, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(),
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2,
						getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX() - 2,
						getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(),
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2,
						getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2,
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2,
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				final Player target = this;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						setNextGraphics(new Graphics(2259));

						if (isAtMultiArea()) {
							for (int regionId : getMapRegionsIds()) {
								List<Integer> playersIndexes = World.getRegion(
										regionId).getPlayerIndexes();
								if (playersIndexes != null) {
									for (int playerIndex : playersIndexes) {
										Player player = World.getPlayers().get(
												playerIndex);
										if (player == null
												|| !player.hasStarted()
												|| player.isDead()
												|| player.hasFinished()
												|| !player.withinDistance(
														target, 2)
												|| !target
														.getControlerManager()
														.canHit(player))
											continue;
										player.applyHit(new Hit(
												target,
												Utils.getRandom((int) (skills
														.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
									}
								}
								List<Integer> npcsIndexes = World.getRegion(
										regionId).getNPCsIndexes();
								if (npcsIndexes != null) {
									for (int npcIndex : npcsIndexes) {
										NPC npc = World.getNPCs().get(npcIndex);
										if (npc == null
												|| npc.isDead()
												|| npc.hasFinished()
												|| !npc.withinDistance(target,
														2)
												|| !npc.getDefinitions()
														.hasAttackOption()
												|| !target
														.getControlerManager()
														.canHit(npc))
											continue;
										npc.applyHit(new Hit(
												target,
												Utils.getRandom((int) (skills
														.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
									}
								}
							}
						} else {
							if (source != null && source != target
									&& !source.isDead()
									&& !source.hasFinished()
									&& source.withinDistance(target, 2))
								source.applyHit(new Hit(
										target,
										Utils.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 3)),
										HitLook.REGULAR_DAMAGE));
						}

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() + 2,
										getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() - 2,
										getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() + 2,
										getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() - 2,
										getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX(), getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX(), getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() + 1,
										getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() - 1,
										getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() + 1,
										getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() - 1,
										getPlane()));
					}
				});
			}
		}
		setNextAnimation(new Animation(-1));
		if (!controlerManager.sendDeath())
			return;
		addStopDelay(7);
		stopAll();
		if (familiar != null)
			familiar.sendDeath(this);
		final Player thisPlayer = this;
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(836));
					
				} else if (loop == 1) {
					getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = getMostDamageReceivedSourcePlayer();
					
					// killer.inventory.addItem(24158, 2);
					//im confused? lemme tryk im a idiot...
					if (killer != null) { //good call
						killer.removeDamage(thisPlayer);// lets make quick command to check
						killer.increaseKillCount(thisPlayer);
						sendItemsOnDeath(killer);
						getPackets().sendGameMessage(
								"<col=ff0000>You have killed "
										+ ", you have now " + killCount + " kills.");
					}
					equipment.init();
					inventory.init();
					reset();

					setNextWorldTile(new WorldTile(
							Settings.RESPAWN_PLAYER_LOCATION));
					setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void sendItemsOnDeath(Player killer) {
		charges.die();	//	Removes charges
		auraManager.removeAura();	//	Depletets aura
		CopyOnWriteArrayList<Item> containedItems = new CopyOnWriteArrayList<Item>();	//	Checks wich items you are carrying
		
		/** Checks what your equipping **/
		for (int i = 0; i < 14; i++) {
			if (equipment.getItem(i) != null
				&& equipment.getItem(i).getId() != -1
				&& equipment.getItem(i).getAmount() != -1)
					containedItems.add(new Item(equipment.getItem(i).getId(),
					equipment.getItem(i).getAmount()));
		}
		
		/** Checks whats in your inventory **/
		for (int i = 0; i < 28; i++) {
			if (inventory.getItem(i) != null
				&& inventory.getItem(i).getId() != -1
				&& inventory.getItem(i).getAmount() != -1)
				containedItems.add(new Item(getInventory().getItem(i).getId(),
				getInventory().getItem(i).getAmount()));
		}
		
		/** Checks your contained items from above **/
		if (containedItems.isEmpty())
			return;

		/** This will remove 'Donator items' if you die with them **/
		for (Item item : containedItems) {
			if (item != null) {
				for (String string : Settings.DONATOR_ITEMS) {
					if (item.getDefinitions().getName().toLowerCase() .contains(string)) {
						containedItems.remove(item);
					} 
				}
			}
		}

		int keptAmount = 3;	// Amount of items to keep if you die
		if (hasSkull())
			keptAmount = 0;	//	Amount of items to keep when Skulled
			
		/** Amount of Items to keep when protection of item is ACTIVATED **/
		if (prayer.usingPrayer(0, 10) || prayer.usingPrayer(1, 0))
			keptAmount++;

		/** Amount of Extra Items to keep for donators **/
		if (donator && Utils.random(2) == 0)
			keptAmount += 1;

		CopyOnWriteArrayList<Item> keptItems = new CopyOnWriteArrayList<Item>();
		Item lastItem = new Item(1, 1);	//	Adds 'lastItem'

		/** This checks the Item Value's of carried items which is checked a few lines above **/
		for (int i = 0; i < keptAmount; i++) {
			for (Item item : containedItems) {
				int price = item.getDefinitions().getValue();
				if (price >= lastItem.getDefinitions().getValue()) {
					lastItem = item;	//	Sets the most valued item as 'lastItem'
				}
			}
			keptItems.add(lastItem);	//	Which item that should be keptm, in this case 'lastItem'
			containedItems.remove(lastItem);	//	removes the contained item from dropping, in this case the item the player keeps ('lastItem')
			lastItem = new Item(1, 1);	//	this adds the 'lastItem' to the victim (the player that just died)
		}

		inventory.reset();	//	 removes everything in the inventory
		equipment.reset();	//	removes everything the victim is equipping

		/** This makes the 'lastItem' that you keep get in your Inventory when respawning **/
		for (Item item : keptItems) {
			getInventory().addItem(item);
		}

		/** This Checks which items that is listed in the 'PROTECT_ON_DEATH' **/
		for (Item item : containedItems) {	// This checks the items you had in your inventory or equipped
			for (String string : Settings.PROTECT_ON_DEATH) {	//	This checks the matched items from the list 'PROTECT_ON_DEATH'
				if (item.getDefinitions().getName().toLowerCase().contains(string)) {
					getInventory().addItem(item);	//	This adds the items that is matched and listed in 'PROTECT_ON_DEATH'
					containedItems.remove(item);	//	This remove the whole list of the contained items that is matched
				}
			}
		}

		/** This to avoid items to be dropped in the list 'PROTECT_ON_DEATH' **/
		for (Item item : containedItems) {	//	This checks the items you had in your inventory or equipped
			for (String string : Settings.PROTECT_ON_DEATH) {	//	This checks the matched items from the list 'PROTECT_ON_DEATH'
				if (item.getDefinitions().getName().toLowerCase().contains(string)) {
					containedItems.remove(item);	//	This remove the whole list of the contained items that is matched
				}
			}
			World.addGroundItem(item, getLastWorldTile(), killer, true, 180, true);	//	This dropps the items to the killer, and is showed for 180 seconds
		}
		/**
		* By: ENZp
		* @Rune-Server.org
		**/
	}

	public void increaseKillCount(Player killed) {
		killed.deathCount++;
		if (killed.getSession().getIP().equals(getSession().getIP()))
			return;
		killCount++;
		getPackets().sendGameMessage(
				"<col=ff0000>You have killed " + killed.getDisplayName()
						+ ", you have now " + killCount + " kills.");
		PkRank.checkRank(this);
	}
	private double dropBoost;
	private long totalDonated;
	private long boostTime;
	
	public long getBoostTime() {
		return boostTime;
	}
	
	public void setBoostTime(long time) {
		this.boostTime = time;
	}
	
	public double getDropBoost() {
		return dropBoost;
	}
	
	public long getTotalDonatedToWell() {
		return totalDonated;
	}
	
	public void setDropBoost(double amount) {
		this.dropBoost = amount;
	}
	
	public void setTotalDonatedToWell(long amount) {
		this.totalDonated = amount;
	}
	public void sendRandomJail(Player p) {
		p.resetWalkSteps();
		switch (Utils.getRandom(6)) {
		case 0:
			p.setNextWorldTile(new WorldTile(3014, 3195, 0));
			break;
		case 1:
			p.setNextWorldTile(new WorldTile(3015, 3189, 0));
			break;
		case 2:
			p.setNextWorldTile(new WorldTile(3014, 3189, 0));
			break;
		case 3:
			p.setNextWorldTile(new WorldTile(3014, 3192, 0));
			break;
		case 4:
			p.setNextWorldTile(new WorldTile(3018, 3180, 0));
			break;
		case 5:
			p.setNextWorldTile(new WorldTile(3018, 3189, 0));
			break;
		case 6:
			p.setNextWorldTile(new WorldTile(3018, 3189, 0));
			break;
		}
	}
	

	@Override
	public int getSize() {
		return appearence.getSize();
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
		appearence.generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public long getStopDelay() {
		return stopDelay;
	}

	public void setInfiniteStopDelay() {
		stopDelay = Long.MAX_VALUE;
	}

	public void resetStopDelay() {
		stopDelay = 0;
	}

	public void addStopDelay(long delay) {
		stopDelay = Utils.currentTimeMillis() + (delay * 600);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay,
			int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay,
			int totalDelay, final String message) {
		stopAll();
		addStopDelay(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextWorldTile(dest);
		else {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead())
						return;
					setNextWorldTile(dest);
					if (message != null)
						getPackets().sendGameMessage(message);
				}
			}, useDelay - 1);
		}
	}

	public Bank getBank() {
		return bank;
	}

	public ControlerManager getControlerManager() {
		return controlerManager;
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}

	public void refreshAllowChatEffects() {
		getPackets().sendConfig(171, allowChatEffects ? 0 : 1);
	}

	public void refreshMouseButtons() {
		getPackets().sendConfig(170, mouseButtons ? 0 : 1);
	}

	public void refreshPrivateChatSetup() {
		getPackets().sendConfig(287, privateChatSetup);
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	/*
	 * do not use this, only used by pm
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public void setDisplayName(String displayName) {
		if (Utils.formatPlayerNameForDisplay(username).equals(displayName))
			this.displayName = null;
		else
			this.displayName = displayName;
	}

	public void addPotDelay(long time) {
		potDelay = time + Utils.currentTimeMillis();
	}

	public long getPotDelay() {
		return potDelay;
	}

	public void addFoodDelay(long time) {
		foodDelay = time + Utils.currentTimeMillis();
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public long getBoneDelay() {
		return boneDelay;
	}

	public void addBoneDelay(long time) {
		boneDelay = time + Utils.currentTimeMillis();
	}

	public void addPoisonImmune(long time) {
		poisonImmune = time + Utils.currentTimeMillis();
		getPoison().reset();
	}

	public long getPoisonImmune() {
		return poisonImmune;
	}

	public void addFireImmune(long time) {
		fireImmune = time + Utils.currentTimeMillis();
	}

	public long getFireImmune() {
		return fireImmune;
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public int getLastVeng() {
		return lastVeng;
	}

	public void setLastVeng(int lastVeng) {
		this.lastVeng = lastVeng;
	}

	public boolean isCastVeng() {
		return castedVeng;
	}

	public void setCastVeng(boolean castVeng) {
		this.castedVeng = castVeng;
	}

	public int getKillCount() {
		return killCount;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public int setKillCount(int killCount) {
		return this.killCount = killCount;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public int setDeathCount(int deathCount) {
		return this.deathCount = deathCount;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public void setInterfaceListenerEvent(Runnable listener) {
		this.interfaceListenerEvent = listener;
	}

	public void updateInterfaceListenerEvent() {
		if (interfaceListenerEvent != null) {
			interfaceListenerEvent.run();
			interfaceListenerEvent = null;
		}
	}

	public long getMuted() {
		return muted;
	}

	public void setMuted(long muted) {
		this.muted = muted;
	}

	public long getJailed() {
		return jailed;
	}

	public void setJailed(long jailed) {
		this.jailed = jailed;
	}

	public boolean isPermBanned() {
		return permBanned;
	}

	public void setPermBanned(boolean permBanned) {
		this.permBanned = permBanned;
	}

	public long getBanned() {
		return banned;
	}

	public void setBanned(long banned) {
		this.banned = banned;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public boolean[] setKilledBarrowBrothers(boolean[] b) {
		return this.killedBarrowBrothers = b;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public boolean isDonator() {
		return donator || donatorTill > Utils.currentTimeMillis();
	}

	@SuppressWarnings("deprecation")
	public void makeDonator(int months) {
		if (donatorTill < Utils.currentTimeMillis())
			donatorTill = Utils.currentTimeMillis();
		Date date = new Date(donatorTill);
		date.setMonth(date.getMonth() + months);
		donatorTill = date.getTime();
	}

	@SuppressWarnings("deprecation")
	public String getDonatorTill() {
		return (donator ? "never" : new Date(donatorTill).toGMTString()) + ".";
	}

	public void setDonator(boolean donator) {
		this.donator = donator;
	}

	public String getRecovQuestion() {
		return recovQuestion;
	}

	public void setRecovQuestion(String recovQuestion) {
		this.recovQuestion = recovQuestion;
	}

	public String getRecovAnswer() {
		return recovAnswer;
	}

	public void setRecovAnswer(String recovAnswer) {
		this.recovAnswer = recovAnswer;
	}

	public int[] getPouches() {
		return pouches;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public String getLastIP() {
		return lastIP;
	}

	public PriceCheckManager getPriceCheckManager() {
		return priceCheckManager;
	}

	public DuelConfigurations getDuelConfigurations() {
		return duelConfigurations;
	}

	public DuelConfigurations setDuelConfigurations(
			DuelConfigurations duelConfigurations) {
		return this.duelConfigurations = duelConfigurations;
	}

	public boolean isDueling() {
		return duelConfigurations != null;
	}

	public void setPestPoints(int pestPoints) {
		this.pestPoints = pestPoints;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	public boolean isUpdateMovementType() {
		return updateMovementType;
	}

	public long getLastPublicMessage() {
		return lastPublicMessage;
	}

	public void setLastPublicMessage(long lastPublicMessage) {
		this.lastPublicMessage = lastPublicMessage;
	}

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public void kickPlayerFromFriendsChannel(String name) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.kickPlayerFromChat(this, name);
	}

	public void sendFriendsChannelMessage(String message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendMessage(this, message);
	}

	public void sendFriendsChannelQuickMessage(QuickChatMessage message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendQuickMessage(this, message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null
						|| !p.hasStarted()
						|| p.hasFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null)
					continue;
				p.getPackets().sendPublicMessage(this, message);
			}
		}
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	// public int getGESlot() {
	// return GESlot;
	// }
	//
	// public void setGESlot(int gESlot) {
	// GESlot = gESlot;
	// }

	// public ItemOffer[] getGeOffers() {
	// return geOffers;
	// }
	//
	// public void setGeOffers(ItemOffer[] geOffers) {
	// this.geOffers = geOffers;
	// }

	public boolean withinDistance(Player tile) {
		if (cutscenesManager.hasCutscene())
			return getMapRegionsIds().contains(tile.getRegionId());
		else {
			if (tile.getPlane() != getPlane())
				return false;
			return Math.abs(tile.getX() - getX()) <= 14
					&& Math.abs(tile.getY() - getY()) <= 14;
		}
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public int getSkullId() {
		return skullId;
	}

	public boolean isFilterGame() {
		return filterGame;
	}

	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		for (LogicPacket p : logicPackets) {
			if (p.getId() == packet.getId()) {
				logicPackets.remove(p);
				break;
			}
		}
		logicPackets.add(packet);
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	public int getOverloadDelay() {
		return overloadDelay;
	}

	public void setOverloadDelay(int overloadDelay) {
		this.overloadDelay = overloadDelay;
	}

	public Trade getTrade() {
		return trade;
	}

	public Trade setTrade(Trade trade) {
		return this.trade = trade;
	}

	public void setTeleBlockDelay(long teleDelay) {
		getTemporaryAttributtes().put("TeleBlocked",
				teleDelay + Utils.currentTimeMillis());
	}

	public long getTeleBlockDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("TeleBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}
	public double getDropBonus() {
		double bonus = 1.00;
		if (getDropBoost() > 1.00 && getBoostTime() > Utils.currentTimeMillis())
			bonus += getDropBoost();
		if (getEquipment().getRingId() == 2572)
	    		bonus += 0.10;
		return bonus;
	}
	public void setPrayerDelay(long teleDelay) {
		getTemporaryAttributtes().put("PrayerBlocked",
				teleDelay + Utils.currentTimeMillis());
		prayer.closeAllPrayers();
	}

	public long getPrayerDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("PrayerBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public FriendChatsManager getCurrentFriendChat() {
		return currentFriendChat;
	}

	public void setCurrentFriendChat(FriendChatsManager currentFriendChat) {
		this.currentFriendChat = currentFriendChat;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
	}

	// Clans

	public ClanChat getClanChat() {
		return currentClanChat;
	}

	public void setClanchat(ClanChat currentClanChat) {
		this.currentClanChat = currentClanChat;
	}

	public void setClanChatOwner(String currentClanChatOwner) {
		this.currentClanChatOwner = currentClanChatOwner;
	}

	public PestControl getPestControl() {
		return pestControl;
	}

	public void setPestControl(PestControl pestControl) {
		this.pestControl = pestControl;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public boolean canSpawn() {
		
		  if (Wilderness.isAtWild(this) || PitsControler.isInFightPits(this) ||
		  getControlerManager().getControler() instanceof CorpBeastControler ||
		  getControlerManager().getControler() instanceof PestControler ||
		  getControlerManager().getControler() instanceof ZGDControler ||
		  getControlerManager().getControler() instanceof GodWars ||
		  getControlerManager().getControler() instanceof DTControler ||
		  getControlerManager().getControler() instanceof Duelarena ||
		  getControlerManager().getControler() instanceof CastleWarsPlaying ||
		  getControlerManager().getControler() instanceof CastleWarsWaiting || 
		  getControlerManager().getControler() instanceof SafeFree || 
		  
		  getControlerManager().getControler() instanceof TowersPkControler &&
		  getPlane() != 0) return false;
		 
		return true;
	}

	public void setTrapAmount(int trapAmount) {
		this.trapAmount = trapAmount;
	}

	public int getTrapAmount() {
		return trapAmount;
	}

	public long getPolDelay() {
		return polDelay;
	}

	public void addPolDelay(long delay) {
		polDelay = delay + Utils.currentTimeMillis();
	}

	public void setPolDelay(long delay) {
		this.polDelay = delay;
	}

	public boolean isUsingTicket() {
		return usingTicket;
	}

	public void setUsingTicket(boolean usingTicket) {
		this.usingTicket = usingTicket;
	}

	public List<Integer> getSwitchItemCache() {
		return switchItemCache;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public int getMovementType() {
		if (getTemporaryMoveType() != -1)
			return getTemporaryMoveType();
		return isRunning() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) // temporary
			ownedObjectsManagerKeys = new LinkedList<String>();
		return ownedObjectsManagerKeys;
	}

	public ClanWars getClanWars() {
		return clanWars;
	}

	public ClanWars setClanWars(ClanWars clanWars) {
		return this.clanWars = clanWars;
	}

	public boolean hasInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			getPackets().sendGameMessage("You don't have enough power left.");
			combatDefinitions.desecreaseSpecialAttack(0);
			return false;
		}
		switch (weaponId) {
		case 4153:
			if (getTemporaryAttributtes().get("InstantSpecial") == null)
				getTemporaryAttributtes().put("InstantSpecial", 4153);
			else
				getTemporaryAttributtes().remove("InstantSpecial");
			combatDefinitions.switchUsingSpecialAttack();
			return true;
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
			setNextAnimation(new Animation(12804));
			setNextGraphics(new Graphics(2319));// 2320
			setNextGraphics(new Graphics(2321));
			addPolDelay(60000);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			return true;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			int defence = (int) (skills.getLevel(Skills.DEFENCE) * 0.90D);
			int attack = (int) (skills.getLevel(Skills.ATTACK) * 0.90D);
			int range = (int) (skills.getLevel(Skills.RANGE) * 0.90D);
			int magic = (int) (skills.getLevel(Skills.MAGIC) * 0.90D);
			int strength = (int) (skills.getLevel(Skills.STRENGTH) * 1.2D);
			skills.set(Skills.DEFENCE, defence);
			skills.set(Skills.ATTACK, attack);
			skills.set(Skills.RANGE, range);
			skills.set(Skills.MAGIC, magic);
			skills.set(Skills.STRENGTH, strength);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			return true;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextGraphics(new Graphics(247));
			setNextForceTalk(new ForceTalk("For ZENITH!"));
			final boolean enhanced = weaponId == 14632;
			skills.set(
					Skills.DEFENCE,
					enhanced ? (int) (skills.getLevelForXp(Skills.DEFENCE) * 1.15D)
							: (skills.getLevel(Skills.DEFENCE) + 8));
			WorldTasksManager.schedule(new WorldTask() {
				int count = 5;

				@Override
				public void run() {
					if (isDead() || hasFinished()
							|| getHitpoints() >= getMaxHitpoints()) {
						stop();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						stop();
						return;
					}
				}
			}, 4, 2);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			return true;
		}
		return false;
	}

	public void incrementMessageAmount() {
		getTemporaryAttributtes().put("Message", getMessageAmount() + 1);
	}

	public int getMessageAmount() {
		Integer messageAmount = (Integer) getTemporaryAttributtes().get(
				"Message");
		if (messageAmount == null)
			return 0;
		return messageAmount;
	}

	public void resetMessageAmount() {
		getTemporaryAttributtes().put("Message", 0);
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public void addDisplayTime(long i) {
		this.displayTime = i + Utils.currentTimeMillis();
	}

	public long getDisplayTime() {
		return displayTime;
	}

	public War getOwnedWar() {
		return (getCurrentFriendChatOwner() != null
				&& getCurrentFriendChatOwner().equalsIgnoreCase(getUsername()) && getCurrentFriendChat()
				.getWar() != null) ? getCurrentFriendChat().getWar() : null;
	}

	public Player getTradePartner() {
		return tradePartner;

	}

	public void setTradePartner(Player tradePartner) {
		this.tradePartner = tradePartner;

	}

	public Trade getTradeSession() {
		return tradeSession;

	}

	public void setTradeSession(Trade session2) {
		this.tradeSession = session2;

	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	public void switchItemsLook() {
		// TODO Auto-generated method stub
		
	}

	public boolean[] getSeenDungeon() {
		return seenDungeon;
	}
	public String rocksMined() {
		// TODO Auto-generated method stub
		return null;
	}

}