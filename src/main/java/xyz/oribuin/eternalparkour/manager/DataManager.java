package xyz.oribuin.eternalparkour.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.database.migration._1_CreateInitialTables;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.util.TimesCompleted;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManager extends AbstractDataManager {

    // UUID = Player's UUID, String = Parkour ID, UserData = Parkour Data
    private final Table<UUID, String, UserData> userData = HashBasedTable.create();
    private final Gson gson = new Gson();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Get all the user data for a level
     *
     * @param level The level name to get the data for
     * @return A list of all the user data for the level
     */
    public @NotNull List<UserData> getLevelData(@NotNull String level) {
        return this.userData.values().stream()
                .filter(data -> data.getLevel().equals(level))
                .toList();
    }

    /**
     * Cache the user's existing data
     *
     * @param data The data to cache
     */
    public void cacheUser(@NotNull UserData data) {
        this.userData.put(data.getPlayer(), data.getLevel(), data);
    }

    /**
     * Save all of a player's data to the database
     *
     * @param player The player to save
     */
    public void saveUser(@NotNull Player player) {
        List<UserData> data = this.userData.values().stream()
                .filter(x -> x.getPlayer().equals(player.getUniqueId()))
                .toList();

        if (data.isEmpty())
            return;

        this.async(task -> this.databaseConnector.connect(connection -> {
            for (var entry : this.userData.values()) {
                final String update = "REPLACE INTO " + this.getTablePrefix() + "data (" +
                        "player, " +
                        "`level`, " +
                        "`username`, " +
                        "completed, " +
                        "attempts, " +
                        "bestTime, " +
                        "bestTimeAchieved, " +
                        "lastTime, " +
                        "lastCompletion, " +
                        "totalTimes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (var statement = connection.prepareStatement(update)) {
                    statement.setString(1, entry.getPlayer().toString());
                    statement.setString(2, entry.getLevel().toLowerCase());
                    statement.setString(3, entry.getName());
                    statement.setInt(4, entry.getCompletions());
                    statement.setInt(5, entry.getAttempts());
                    statement.setLong(6, entry.getBestTime());
                    statement.setLong(7, entry.getBestTimeAchieved());
                    statement.setLong(8, entry.getLastTime());
                    statement.setLong(9, entry.getLastCompletion());
                    statement.setString(10, this.gson.toJson(new TimesCompleted(entry.getTotalTimes())));
                    statement.executeUpdate();
                }
            }
        }));
    }

    /**
     * Save a user's level data to the database
     *
     * @param data The user's data
     */
    public void saveUser(@NotNull UserData data) {
        this.cacheUser(data);

        this.async(task -> this.databaseConnector.connect(connection -> {
            final String update = "REPLACE INTO " + this.getTablePrefix() + "data (" +
                    "player, " +
                    "`level`, " +
                    "`username`, " +
                    "completed, " +
                    "attempts, " +
                    "bestTime, " +
                    "bestTimeAchieved, " +
                    "lastTime, " +
                    "lastCompletion, " +
                    "totalTimes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (var statement = connection.prepareStatement(update)) {
                statement.setString(1, data.getPlayer().toString());
                statement.setString(2, data.getLevel().toLowerCase());
                statement.setString(3, data.getName());
                statement.setInt(4, data.getCompletions());
                statement.setInt(5, data.getAttempts());
                statement.setLong(6, data.getBestTime());
                statement.setLong(7, data.getBestTimeAchieved());
                statement.setLong(9, data.getLastTime());
                statement.setLong(10, data.getLastCompletion());
                statement.setString(11, this.gson.toJson(new TimesCompleted(data.getTotalTimes())));
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete a user's level data from the database
     *
     * @param data The user's data
     */
    public void deleteUser(@NotNull UserData data) {
        this.userData.remove(data.getPlayer(), data.getLevel());

        final String delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE player = ? AND level = ?";
        this.async(task -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement(delete)) {
                statement.setString(1, data.getPlayer().toString());
                statement.setString(2, data.getLevel().toLowerCase());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete all of a player's data from the database
     *
     * @param player The player to delete
     */
    public void deleteUser(@NotNull UUID player) {
        this.userData.row(player).clear();

        final String delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE player = ?";
        this.async(task -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement(delete)) {
                statement.setString(1, player.toString());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete all level data from the database
     *
     * @param level The level to delete
     */
    public void deleteLevel(@NotNull String level) {
        this.userData.column(level).clear();

        final String delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE level = ?";
        this.async(task -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement(delete)) {
                statement.setString(1, level.toLowerCase());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Get a player's data from the cache for a specific level
     *
     * @param player The player to get data for
     * @param level  The level to get data for
     * @return The player's data for the level
     */
    @NotNull
    public UserData getData(@NotNull UUID player, @NotNull String level) {
        UserData data = this.userData.get(player, level);
        if (data == null)
            data = new UserData(player, level);

        return data;
    }

    /**
     * Get all of a player's data from the cache
     *
     * @param player The player to get data for
     * @return The player's data
     */
    @NotNull
    public Map<String, UserData> getData(@NotNull UUID player) {
        return this.userData.row(player);
    }

    /**
     * Load every single user into memory
     */
    public void loadUserData() {
        this.userData.clear();

        this.async(task -> this.databaseConnector.connect(connection -> {
            final var select = "SELECT * FROM " + this.getTablePrefix() + "data";

            try (var statement = connection.prepareStatement(select)) {
                var results = statement.executeQuery();
                while (results.next()) {
                    var uuid = UUID.fromString(results.getString("player"));
                    var data = new UserData(uuid, results.getString("level").toLowerCase());
                    data.setName(results.getString("username"));
                    data.setCompletions(results.getInt("completed"));
                    data.setAttempts(results.getInt("attempts"));
                    data.setBestTime(results.getLong("bestTime"));
                    data.setBestTimeAchieved(results.getLong("bestTimeAchieved"));
                    data.setLastTime(results.getLong("lastTime"));
                    data.setLastCompletion(results.getLong("lastCompletion"));
                    data.setTotalTimes(this.gson.fromJson(results.getString("totalTimes"), TimesCompleted.class).getTimes());
                    this.userData.put(uuid, data.getLevel(), data);
                }
            }
        }));
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables.class);
    }

    /**
     * Run a task asynchronously
     *
     * @param callback The callback to run
     */
    private void async(Consumer<BukkitTask> callback) {
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(rosePlugin, callback);
    }

    public Table<UUID, String, UserData> getUserData() {
        return userData;
    }

}
