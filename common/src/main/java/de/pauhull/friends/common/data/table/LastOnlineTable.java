package de.pauhull.friends.common.data.table;

import de.pauhull.friends.common.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class LastOnlineTable {

    protected MySQL mySQL;
    protected ExecutorService executorService;
    protected String table;

    public LastOnlineTable(MySQL mySQL, ExecutorService executorService, String tablePrefix) {
        this.mySQL = mySQL;
        this.executorService = executorService;
        this.table = tablePrefix + "last_online";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `last_online` BIGINT, PRIMARY KEY (`id`))";
        mySQL.update(String.format(createTableQuery, table));

    }

    public void getLastOnline(UUID uuid, Consumer<Long> consumer) {
        executorService.execute(() -> consumer.accept(getLastOnlineSync(uuid)));
    }

    public long getLastOnlineSync(UUID uuid) {
        try {

            ResultSet result = mySQL.query(String.format("SELECT last_online FROM `%s` WHERE `uuid`='%s'", table, uuid.toString()));

            if (result.next()) {
                return result.getLong("last_online");
            }

            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setLastOnline(UUID uuid) {
        getLastOnline(uuid, lastOnline -> {

            if (lastOnline == null) {
                mySQL.update(String.format("INSERT INTO `%s` VALUES (0, '%s', %s)", table, uuid.toString(), Long.toString(System.currentTimeMillis())));
            } else {
                mySQL.update(String.format("UPDATE `%s` SET `last_online`=%s WHERE `uuid`='%s'", table, Long.toString(System.currentTimeMillis()), uuid.toString()));
            }

        });
    }

}
