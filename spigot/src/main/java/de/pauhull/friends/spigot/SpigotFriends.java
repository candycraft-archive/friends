package de.pauhull.friends.spigot;

import de.pauhull.friends.common.data.MySQL;
import de.pauhull.friends.common.data.table.LastOnlineTable;
import de.pauhull.friends.common.data.table.SettingsTable;
import de.pauhull.friends.common.util.FriendThreadFactory;
import de.pauhull.friends.spigot.data.SpigotFriendRequestTable;
import de.pauhull.friends.spigot.data.SpigotFriendTable;
import de.pauhull.friends.spigot.inventory.*;
import de.pauhull.friends.spigot.listener.PlayerJoinListener;
import de.pauhull.friends.spigot.util.HeadCache;
import de.pauhull.friends.spigot.util.PartyManager;
import de.pauhull.uuidfetcher.spigot.SpigotUUIDFetcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpigotFriends extends JavaPlugin {

    @Getter
    private static SpigotFriends instance;

    @Getter
    private PlayerViewMenu playerViewMenu;

    @Getter
    private FriendMenu friendMenu;

    @Getter
    private MainMenu mainMenu;

    @Getter
    private SettingsMenu settingsMenu;

    @Getter
    private FriendRequestMenu friendRequestMenu;

    @Getter
    private AcceptMenu acceptMenu;

    @Getter
    private File configFile;

    @Getter
    private FileConfiguration config;

    @Getter
    private MySQL mysql;

    @Getter
    private PartyMenu partyMenu;

    @Getter
    private SpigotFriendTable friendTable;

    @Getter
    private SpigotFriendRequestTable friendRequestTable;

    @Getter
    private SettingsTable settingsTable;

    @Getter
    private ExecutorService executorService;

    @Getter
    private String tablePrefix;

    @Getter
    private SpigotUUIDFetcher uuidFetcher;

    @Getter
    private HeadCache headCache;

    @Getter
    private LastOnlineTable lastOnlineTable;

    @Getter
    private PartyManager partyManager;

    @Override
    public void onEnable() {
        instance = this;

        this.executorService = Executors.newCachedThreadPool(new FriendThreadFactory("SpigotFriends"));
        this.configFile = new File(getDataFolder(), "config.yml");
        this.config = copyAndLoad("config.yml", configFile);
        this.tablePrefix = config.getString("Database.TablePrefix");
        this.mysql = new MySQL(config.getString("Database.MySQL.Host"),
                config.getString("Database.MySQL.Port"),
                config.getString("Database.MySQL.Database"),
                config.getString("Database.MySQL.User"),
                config.getString("Database.MySQL.Password"),
                config.getBoolean("Database.MySQL.SSL"));
        this.uuidFetcher = SpigotUUIDFetcher.getInstance();
        this.headCache = new HeadCache();

        if (!this.mysql.connect()) {
            System.err.println("[Friends] Couldn't connect to MySQL!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getVersion().contains("1.8")) {
            this.friendMenu = new FriendMenu(this);
            this.playerViewMenu = new PlayerViewMenu(this);
            this.settingsMenu = new SettingsMenu(this);
            this.acceptMenu = new AcceptMenu(this);
            this.friendRequestMenu = new FriendRequestMenu(this);
            this.mainMenu = new MainMenu(this);
            this.partyMenu = new PartyMenu(this);
        }

        this.settingsTable = new SettingsTable(mysql, executorService, tablePrefix);
        this.friendTable = new SpigotFriendTable(mysql, executorService, tablePrefix);
        this.lastOnlineTable = new LastOnlineTable(mysql, executorService, tablePrefix);
        this.friendRequestTable = new SpigotFriendRequestTable(mysql, executorService, tablePrefix);

        this.partyManager = new PartyManager(this);

        new PlayerJoinListener(this);
    }

    @Override
    public void onDisable() {
        this.executorService.shutdown();
    }

    private FileConfiguration copyAndLoad(String resource, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

}
