package de.pauhull.friends.common.data.table;

import de.pauhull.friends.common.data.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SettingsTable {

    protected MySQL database;
    protected ExecutorService executorService;
    protected String table;

    public SettingsTable(MySQL database, ExecutorService executorService, String tablePrefix) {
        this.database = database;
        this.executorService = executorService;
        this.table = tablePrefix + "settings";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `requests` BOOLEAN, `notifications` BOOLEAN, `jumping` BOOLEAN, `messages` BOOLEAN, `invites` BOOLEAN, `public` BOOLEAN, `status` VARCHAR(255), PRIMARY KEY (`id`))";
        database.update(String.format(createTableQuery, table));

    }

    public void setStatus(UUID uuid, String status) {
        setValue(uuid, "status", status);
    }

    public void getStatus(UUID uuid, Consumer<String> consumer) {
        getValue(uuid, "status", status -> {
            if (status == null) {
                consumer.accept("§7Ich liebe CandyCraft");
            } else {
                consumer.accept((String) status);
            }
        });
    }

    public String getStatusSync(UUID uuid) {
        String status = getStatusSync(uuid);
        if (status == null) {
            return "§7Ich liebe CandyCraft";
        } else {
            return status;
        }
    }

    public void setPublic(UUID uuid, boolean publicParty) {
        setValue(uuid, "public", publicParty);
    }

    public void isPublic(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "public", publicParty -> {
            if (publicParty == null) {
                consumer.accept(false);
            } else {
                consumer.accept((Boolean) publicParty);
            }
        });
    }

    public void setInvites(UUID uuid, boolean invites) {
        setValue(uuid, "invites", invites);
    }

    public void isInvites(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "invites", invites -> {
            if (invites == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) invites);
            }
        });
    }

    public void setRequests(UUID uuid, boolean requests) {
        setValue(uuid, "requests", requests);
    }

    public void isRequests(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "requests", requests -> {
            if (requests == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) requests);
            }
        });
    }

    public void setNotifications(UUID uuid, boolean notifications) {
        setValue(uuid, "notifications", notifications);
    }

    public void isNotifications(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "notifications", notifications -> {
            if (notifications == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) notifications);
            }
        });
    }

    public void setMessages(UUID uuid, boolean messages) {
        setValue(uuid, "messages", messages);
    }

    public void isMessages(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "messages", messages -> {
            if (messages == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) messages);
            }
        });
    }

    public void setJumping(UUID uuid, boolean jumping) {
        setValue(uuid, "jumping", jumping);
    }

    public void isJumping(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "jumping", jumping -> {
            if (jumping == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) jumping);
            }
        });
    }


    public void exists(UUID uuid, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.query(String.format("SELECT * FROM `%s` WHERE `uuid`='%s'", table, uuid.toString()));
                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void createRow(UUID uuid) {
        executorService.execute(() -> {
            try {

                PreparedStatement statement = database.prepare(String.format("INSERT INTO `%s` VALUES (0, ?, true, true, true, true, true, false, ?)", table));
                statement.setString(1, uuid.toString());
                statement.setString(2, "§7Ich liebe CandyCraft");
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setValue(UUID uuid, String column, Object value) {
        exists(uuid, exists -> {
            try {

                if (exists) {

                    PreparedStatement statement = database.prepare(String.format("UPDATE `%s` SET `%s`=? WHERE `uuid`=?", table, column));
                    statement.setObject(1, value);
                    statement.setString(2, uuid.toString());
                    statement.execute();

                } else {
                    createRow(uuid);
                    setValue(uuid, column, value);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void getValue(UUID uuid, String column, Consumer<Object> consumer) {
        executorService.execute(() -> {
            consumer.accept(getValueSync(uuid, column));
        });
    }

    public Object getValueSync(UUID uuid, String column) {
        try {

            ResultSet result = database.query(String.format("SELECT " + column + " FROM `%s` WHERE `uuid`='%s'", table, uuid.toString()));
            if (result.next()) {
                return result.getObject(column);
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
