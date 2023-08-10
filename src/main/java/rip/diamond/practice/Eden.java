package rip.diamond.practice;

import com.google.gson.Gson;
import io.github.epicgo.sconey.SconeyHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.command.ArenaCommand;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.database.MongoManager;
import rip.diamond.practice.debug.TestCommand;
import rip.diamond.practice.debug.TestListener;
import rip.diamond.practice.duel.DuelRequest;
import rip.diamond.practice.duel.DuelRequestManager;
import rip.diamond.practice.duel.command.DuelCommand;
import rip.diamond.practice.events.command.EventCommand;
import rip.diamond.practice.events.command.JoinEventCommand;
import rip.diamond.practice.events.listener.EventListener;
import rip.diamond.practice.hook.HookManager;
import rip.diamond.practice.kiteditor.KitEditorListener;
import rip.diamond.practice.kiteditor.KitEditorManager;
import rip.diamond.practice.kiteditor.command.EditKitsCommand;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitListener;
import rip.diamond.practice.kits.command.EnchantCommand;
import rip.diamond.practice.kits.command.GoldenHeadCommand;
import rip.diamond.practice.kits.command.KitCommand;
import rip.diamond.practice.layout.NameTagAdapter;
import rip.diamond.practice.layout.ScoreboardAdapter;
import rip.diamond.practice.layout.TabAdapter;
import rip.diamond.practice.leaderboard.LeaderboardManager;
import rip.diamond.practice.leaderboard.command.ReloadLeaderboardCommand;
import rip.diamond.practice.lobby.LobbyManager;
import rip.diamond.practice.lobby.LobbyMovementHandler;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchMovementHandler;
import rip.diamond.practice.match.command.*;
import rip.diamond.practice.match.listener.MatchListener;
import rip.diamond.practice.match.listener.SpectateListener;
import rip.diamond.practice.misc.commands.*;
import rip.diamond.practice.misc.listeners.ChatListener;
import rip.diamond.practice.misc.listeners.GeneralListener;
import rip.diamond.practice.party.PartyListener;
import rip.diamond.practice.party.command.ChooseMatchTypeCommand;
import rip.diamond.practice.party.command.OtherPartiesCommand;
import rip.diamond.practice.party.command.PartyCommand;
import rip.diamond.practice.party.fight.PartyFightManager;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.command.SettingsCommand;
import rip.diamond.practice.profile.command.StatsCommand;
import rip.diamond.practice.profile.command.settings.*;
import rip.diamond.practice.profile.listener.ProfileListener;
import rip.diamond.practice.profile.procedure.ProcedureListener;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueListener;
import rip.diamond.practice.queue.command.QueueCommand;
import rip.diamond.practice.util.BasicConfigFile;
import rip.diamond.practice.util.EntityHider;
import rip.diamond.practice.util.InventoryUtil;
import rip.diamond.practice.util.command.CommandManager;
import rip.diamond.practice.util.menu.Menu;
import rip.diamond.practice.util.menu.MenuListener;
import rip.diamond.practice.util.nametags.NameTagManager;
import rip.diamond.practice.util.tablist.ImanityTabHandler;
import rip.diamond.spigotapi.SpigotAPI;

import java.text.DecimalFormat;
import java.util.Arrays;

@Getter
public class Eden extends JavaPlugin {

    public static Eden INSTANCE;
    public static DecimalFormat DECIMAL = new DecimalFormat("0.##");
    public static final Gson GSON = new Gson();

    private BasicConfigFile configFile;
    private BasicConfigFile languageFile;
    private BasicConfigFile locationFile;
    private BasicConfigFile itemFile;
    private BasicConfigFile arenaFile;
    private BasicConfigFile kitFile;
    private BasicConfigFile soundFile;

    private CommandManager commandManager;
    private MongoManager mongoManager;
    private LobbyManager lobbyManager;
    private KitEditorManager kitEditorManager;
    private DuelRequestManager duelRequestManager;
    private PartyFightManager partyFightManager;
    private LeaderboardManager leaderboardManager;
    private HookManager hookManager;
    private NameTagManager nameTagManager;

    private SpigotAPI spigotAPI;
    private EntityHider entityHider;
    private SconeyHandler scoreboardHandler;
    private ImanityTabHandler tabHandler;
    private EdenCache cache;
    private EdenPlaceholder placeholder;

    @Override
    public void onEnable() {
        INSTANCE = this;

        spigotAPI = new SpigotAPI().init(this);

        loadFiles();
        loadManagers();
        loadListeners();
        loadCommands();
        loadGeneral();
    }

    @Override
    public void onDisable() {
        //Stop all existing thread
        if (tabHandler != null) Eden.INSTANCE.getTabHandler().stop();
        //Clean up matches
        for (Match match : Match.getMatches().values()) {
            match.getArenaDetail().restoreChunk();
            match.getEntities().forEach(matchEntity -> matchEntity.getEntity().remove());
        }
        // Save all kits
        Kit.getKits().forEach(Kit::autoSave);
        // Save all arenas
        Arena.getArenas().forEach(Arena::autoSave);
        //Save all profiles
        if (Config.PROFILE_SAVE_ON_SERVER_STOP.toBoolean()) {
            PlayerProfile.getProfiles().values().forEach(profile -> profile.save(false, (bool) -> {}));
        }
    }

    private void loadFiles() {
        this.configFile = new BasicConfigFile(this, "config.yml");
        this.languageFile = new BasicConfigFile(this, "language.yml");
        this.locationFile = new BasicConfigFile(this, "locations.yml");
        this.itemFile = new BasicConfigFile(this, "item.yml");
        this.arenaFile = new BasicConfigFile(this, "arena.yml");
        this.kitFile = new BasicConfigFile(this, "kit.yml");
        this.soundFile = new BasicConfigFile(this, "sound.yml");

        Config.loadDefault();
    }

    private void loadManagers() {
        this.commandManager = new CommandManager(this);
        this.mongoManager = new MongoManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.kitEditorManager = new KitEditorManager(this);
        this.duelRequestManager = new DuelRequestManager();
        this.partyFightManager = new PartyFightManager();
        this.leaderboardManager = new LeaderboardManager();
        this.hookManager = new HookManager(this);
        this.nameTagManager = new NameTagManager(this);
    }

    private void loadListeners() {
        Arrays.asList(
                new MenuListener(this),
                new EventListener(this),
                new KitListener(),
                new MatchListener(this),
                new ChatListener(this),
                new GeneralListener(this),
                new ProfileListener(this),
                new ProcedureListener(),
                new KitEditorListener(this),
                new PartyListener(),
                new QueueListener(),
                new SpectateListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        if (Config.DEBUG.toBoolean()) {
            getServer().getPluginManager().registerEvents(new TestListener(), this);
        }
    }

    private void loadCommands() {
        new ArenaCommand();
        new EnchantCommand();
        new GoldenHeadCommand();
        new KitCommand();
        new EdenCommand();
        new EloResetCommand();
        new LocationCommand();
        new ToggleItemFlagCommand();
        new QueueCommand();
        new EditKitsCommand();
        new ChooseMatchTypeCommand();
        new OtherPartiesCommand();
        new PartyCommand();
        new DuelCommand();
        new EventCommand();
        new JoinEventCommand();
        new StatsCommand();
        new ForceEndCommand();
        new GiveUpCommand();
        new LeaveSpectateCommand();
        new NoSpeedCommand();
        new RequeueCommand();
        new SpectateCommand();
        new TeleporterCommand();
        new ViewInventoryCommand();
        new ReloadLeaderboardCommand();
        new SettingsCommand();
        new AfternoonCommand();
        new DayCommand();
        new MidnightCommand();
        new NightCommand();
        new PingRangeCommand();
        new ToggleArenaSelectionCommand();
        new ToggleDuelRequestCommand();
        new ToggleEventAnnouncementCommand();
        new ToggleMatchScoreboardCommand();
        new TogglePartyInviteCommand();
        new ToggleSpectatorJoinLeaveMessageCommand();
        new ToggleSpectatorVisibilityCommand();

        if (Config.DEBUG.toBoolean()) {
            new TestCommand();
        }
    }

    private void loadGeneral() {
        Menu.init();
        PlayerProfile.init();
        Kit.init();
        Arena.init();
        Match.init();
        Queue.init();
        DuelRequest.init();
        leaderboardManager.init();

        new LobbyMovementHandler(this);
        new MatchMovementHandler();

        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST).init();
        this.scoreboardHandler = new SconeyHandler(this, new ScoreboardAdapter());
        this.cache = new EdenCache();
        this.placeholder = new EdenPlaceholder(this);
        if (Config.NAMETAG_ENABLED.toBoolean()) this.nameTagManager.registerAdapter(new NameTagAdapter());
        if (Config.FANCY_TABLIST_ENABLED.toBoolean()) tabHandler = new ImanityTabHandler(new TabAdapter());
        if (Config.DISABLE_SAVE_WORLD.toBoolean()) {
            for (World world : Bukkit.getWorlds()) {
                world.setAutoSave(false);
            }
        }

        InventoryUtil.handleRemoveCrafting();
    }
}
