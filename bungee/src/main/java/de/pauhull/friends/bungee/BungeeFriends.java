package de.pauhull.friends.bungee;

import de.pauhull.friends.bungee.command.*;
import de.pauhull.friends.bungee.data.BungeeFriendRequestTable;
import de.pauhull.friends.bungee.data.BungeeFriendTable;
import de.pauhull.friends.bungee.listener.PlayerDisconnectListener;
import de.pauhull.friends.bungee.listener.PostLoginListener;
import de.pauhull.friends.bungee.listener.ServerSwitchListener;
import de.pauhull.friends.bungee.util.BungeeMessageListener;
import de.pauhull.friends.bungee.util.MessageManager;
import de.pauhull.friends.common.data.MySQL;
import de.pauhull.friends.common.data.table.LastOnlineTable;
import de.pauhull.friends.common.data.table.SettingsTable;
import de.pauhull.friends.common.util.FriendThreadFactory;
import de.pauhull.uuidfetcher.bungee.BungeeUUIDFetcher;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BungeeFriends extends Plugin {

    //TODO öffentliche parties (premium)

    @Getter
    private static BungeeFriends instance;

    @Getter
    private static String friendsPrefix;

    @Getter
    private static String partyPrefix;

    @Getter
    private MySQL mySQL;

    @Getter
    private ExecutorService executorService;

    @Getter
    private MessageManager messages;

    @Getter
    private String tablePrefix;

    @Getter
    private File configFile;

    @Getter
    private File messageFile;

    @Getter
    private Configuration config;

    @Getter
    private Configuration messageConfig;

    @Getter
    private BungeeFriendRequestTable friendRequestTable;

    @Getter
    private BungeeFriendTable friendTable;

    @Getter
    private SettingsTable settingsTable;

    @Getter
    private LastOnlineTable lastOnlineTable;

    @Getter
    private BungeeUUIDFetcher uuidFetcher;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new File(getDataFolder(), "config.yml");
        this.messageFile = new File(getDataFolder(), "messages.yml");
        this.config = copyAndLoad("config.yml", configFile);
        this.messageConfig = copyAndLoad("messages.yml", messageFile);
        this.messages = new MessageManager().load(this.messageConfig);
        friendsPrefix = messages.getFriendsPrefix();
        partyPrefix = messages.getPartyPrefix();
        this.uuidFetcher = new BungeeUUIDFetcher();
        this.tablePrefix = config.getString("Database.TablePrefix");
        this.executorService = Executors.newSingleThreadExecutor(new FriendThreadFactory("BungeeFriend"));
        this.mySQL = new MySQL(config.getString("Database.MySQL.Host"),
                config.getString("Database.MySQL.Port"),
                config.getString("Database.MySQL.Database"),
                config.getString("Database.MySQL.User"),
                config.getString("Database.MySQL.Password"),
                config.getBoolean("Database.MySQL.SSL"));

        if (!this.mySQL.connect()) {
            ProxyServer.getInstance().getLogger().severe("[Friends] Couldn't connect to MySQL!");
            return;
        }

        this.friendRequestTable = new BungeeFriendRequestTable(mySQL, executorService, tablePrefix);
        this.friendTable = new BungeeFriendTable(mySQL, executorService, tablePrefix);
        this.settingsTable = new SettingsTable(mySQL, executorService, tablePrefix);
        this.lastOnlineTable = new LastOnlineTable(mySQL, executorService, tablePrefix);

        FriendCommand.register();
        MsgCommand.register();
        PartyCommand.register();
        PartyChatCommand.register();
        PlayerDisconnectListener.register();
        PostLoginListener.register();
        ReplyCommand.register();
        ServerSwitchListener.register();

        new BungeeMessageListener(this);
    }

    @Override
    public void onDisable() {
        this.mySQL.close();
        this.executorService.shutdown();
    }

    private Configuration copyAndLoad(String resource, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResourceAsStream(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void reload() {
        this.messageConfig = copyAndLoad("messages.yml", messageFile);
        this.messages.load(messageConfig);
    }

}
