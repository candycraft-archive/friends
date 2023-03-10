package de.pauhull.friends.common.data.table;

import de.pauhull.friends.common.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class FriendRequestTable {

    protected MySQL mySQL;
    protected ExecutorService executorService;
    protected String table;

    public FriendRequestTable(MySQL mySQL, ExecutorService executorService, String tablePrefix) {
        this.mySQL = mySQL;
        this.executorService = executorService;
        this.table = tablePrefix + "requests";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `from` VARCHAR(255), `to` VARCHAR(255), `time` BIGINT, PRIMARY KEY (`id`))";
        mySQL.update(String.format(createTableQuery, table));

    }

    public void getTime(UUID from, UUID to, Consumer<Long> consumer) {
        executorService.execute(() -> {
            try {
                ResultSet result = mySQL.query(String.format("SELECT * FROM `%s` WHERE `from`='%s' AND `to`='%s'",
                        table, from.toString(), to.toString()));

                if (result.next()) {
                    consumer.accept(result.getLong("time"));
                } else {
                    consumer.accept(null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void acceptFriendRequest(FriendTable friendTable, UUID from, UUID to) {
        executorService.execute(() -> {

            mySQL.update(String.format("DELETE FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));
            friendTable.addFriends(from, to);

        });
    }

    public void denyFriendRequest(UUID from, UUID to) {
        executorService.execute(() -> {

            mySQL.update(String.format("DELETE FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));

        });
    }

    public void getOpenFriendRequests(UUID to, Consumer<Integer> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query(String.format("SELECT * FROM `%s` WHERE `to`='%s'", table, to.toString()));

                int results = 0;
                while (result.next())
                    results++;

                consumer.accept(results);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(0);
            }
        });
    }

    public void sendFriendRequest(UUID from, UUID to) {
        getTime(from, to, time -> {
            if (time == null) {
                mySQL.update(String.format("INSERT INTO `%s` VALUES (0, '%s', '%s', %s)",
                        table, from.toString(), to.toString(), Long.toString(System.currentTimeMillis())));
            } else {
                mySQL.update(String.format("UPDATE `%s` SET `time`=%s WHERE `from`='%s' AND `to`='%s'",
                        table, Long.toString(System.currentTimeMillis()), from.toString(), to.toString()));
            }
        });
    }

}
