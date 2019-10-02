package de.pauhull.friends.bungee.data;

import de.pauhull.friends.common.data.MySQL;
import de.pauhull.friends.common.data.table.FriendTable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BungeeFriendTable extends FriendTable {

    public BungeeFriendTable(MySQL mySQL, ExecutorService executorService, String tablePrefix) {
        super(mySQL, executorService, tablePrefix);
    }

    public void getFriends(UUID uuid, Consumer<Collection<ProxiedPlayer>> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query(String.format("SELECT * FROM `%s` WHERE `a`='%s' OR `b`='%s'", table, uuid.toString(), uuid.toString()));

                Set<ProxiedPlayer> players = new HashSet<>();
                while (result.next()) {
                    UUID a = UUID.fromString(result.getString("a"));
                    UUID b = UUID.fromString(result.getString("b"));
                    ProxiedPlayer player;

                    if (a.equals(uuid)) {
                        player = ProxyServer.getInstance().getPlayer(b);
                    } else {
                        player = ProxyServer.getInstance().getPlayer(a);
                    }

                    if (player != null) {
                        players.add(player);
                    }
                }

                consumer.accept(players);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashSet<>());
            }
        });
    }

}
