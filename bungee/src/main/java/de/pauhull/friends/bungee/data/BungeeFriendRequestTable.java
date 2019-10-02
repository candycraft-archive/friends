package de.pauhull.friends.bungee.data;

import de.pauhull.friends.common.data.MySQL;
import de.pauhull.friends.common.data.table.FriendRequestTable;
import de.pauhull.friends.common.data.table.FriendTable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BungeeFriendRequestTable extends FriendRequestTable {

    public BungeeFriendRequestTable(MySQL mySQL, ExecutorService executorService, String tablePrefix) {
        super(mySQL, executorService, tablePrefix);
    }

    public void acceptAll(FriendTable friendTable, UUID to, BiConsumer<Collection<ProxiedPlayer>, Integer> consumer) {
        executorService.execute(() -> {
            try {
                Collection<ProxiedPlayer> players = new HashSet<>();

                ResultSet requests = mySQL.query(String.format("SELECT * FROM `%s` WHERE `to`='%s'", table, to.toString()));
                int amount = 0;
                while (requests.next()) {
                    amount++;
                    UUID from = UUID.fromString(requests.getString("from"));
                    acceptFriendRequest(friendTable, from, to);
                    players.add(ProxyServer.getInstance().getPlayer(from));
                }

                consumer.accept(players, amount);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashSet<>(), 0);
            }
        });
    }

    public void denyAll(UUID to, BiConsumer<Collection<ProxiedPlayer>, Integer> consumer) {
        executorService.execute(() -> {
            try {
                Collection<ProxiedPlayer> players = new HashSet<>();

                ResultSet requests = mySQL.query(String.format("SELECT * FROM `%s` WHERE `to`='%s'", table, to.toString()));
                int amount = 0;
                while (requests.next()) {
                    amount++;
                    UUID from = UUID.fromString(requests.getString("from"));
                    denyFriendRequest(from, to);
                    players.add(ProxyServer.getInstance().getPlayer(from));
                }

                consumer.accept(players, amount);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashSet<>(), 0);
            }
        });
    }

    public void isRequested(UUID from, UUID to, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query(String.format("SELECT * FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));
                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void removeRequest(UUID from, UUID to) {
        executorService.execute(() -> {

            mySQL.update(String.format("DELETE FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));

        });
    }

}
