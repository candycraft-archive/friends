package de.pauhull.friends.spigot.data;

import de.pauhull.friends.common.data.MySQL;
import de.pauhull.friends.common.data.table.FriendTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SpigotFriendTable extends FriendTable {

    public SpigotFriendTable(MySQL mySQL, ExecutorService executorService, String tablePrefix) {
        super(mySQL, executorService, tablePrefix);
    }

    public void getFriends(UUID uuid, int start, int results, Consumer<ArrayList<UUID>> consumer) {
        executorService.execute(() -> {
            try {

                String sql = String.format("SELECT * FROM `%s` WHERE `a`='%s' OR `b`='%s' ORDER BY `time` DESC LIMIT %s, %s",
                        table, uuid.toString(), uuid.toString(), Integer.toString(start), Integer.toString(results));
                ResultSet result = mySQL.query(sql);

                ArrayList<UUID> friends = new ArrayList<>();
                while (result.next()) {
                    UUID a = UUID.fromString(result.getString("a"));
                    UUID b = UUID.fromString(result.getString("b"));
                    if (a.equals(uuid)) {
                        friends.add(b);
                    } else {
                        friends.add(a);
                    }
                }
                consumer.accept(friends);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new ArrayList<>());
            }
        });
    }

}
