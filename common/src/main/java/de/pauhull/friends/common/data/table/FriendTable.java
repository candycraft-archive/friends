package de.pauhull.friends.common.data.table;

import de.pauhull.friends.common.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class FriendTable {

    protected MySQL mySQL;
    protected ExecutorService executorService;
    protected String table;

    public FriendTable(MySQL mySQL, ExecutorService executorService, String tablePrefix) {
        this.mySQL = mySQL;
        this.executorService = executorService;
        this.table = tablePrefix + "table";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `a` VARCHAR(255), `b` VARCHAR(255), `time` BIGINT, PRIMARY KEY (`id`))";
        mySQL.update(String.format(createTableQuery, table));

    }

    public void addFriends(UUID a, UUID b) {
        areFriends(a, b, alreadyFriends -> {

            if (!alreadyFriends) {
                mySQL.update(String.format("INSERT INTO `%s` VALUES (0, '%s', '%s', %s)", table, a.toString(), b.toString(), Long.toString(System.currentTimeMillis())));
            } else {
                mySQL.update(String.format("UPDATE `%s` SET `time`=%s WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                        table, Long.toString(System.currentTimeMillis()), a.toString(), a.toString(), b.toString(), b.toString()));
            }

        });
    }

    public void removeFriends(UUID a, UUID b) {
        areFriends(a, b, areFriends -> {

            if (areFriends) {
                mySQL.update(String.format("DELETE FROM `%s` WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                        table, a.toString(), a.toString(), b.toString(), b.toString()));
            }

        });
    }

    public void areFriends(UUID a, UUID b, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                if (a.equals(b)) {
                    consumer.accept(false);
                    return;
                }

                ResultSet result = mySQL.query(String.format("SELECT * FROM `%s` WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                        table, a.toString(), a.toString(), b.toString(), b.toString()));

                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public long getTimeSync(UUID a, UUID b) {
        try {

            if (a.equals(b)) {
                return 0;
            }

            ResultSet result = mySQL.query(String.format("SELECT time FROM `%s` WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                    table, a.toString(), a.toString(), b.toString(), b.toString()));

            if (result.next()) {
                return result.getLong("time");
            }

            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void getTime(UUID a, UUID b, Consumer<Long> consumer) {
        executorService.execute(() -> {
            consumer.accept(getTimeSync(a, b));
        });
    }

}
