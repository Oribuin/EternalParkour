package xyz.oribuin.eternalparkour.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.database.migration._1_CreateInitialTables;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.util.TimesCompleted;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManager extends AbstractDataManager {

    // UUID = Player's UUID, String = Parkour ID, UserData = Parkour Data
    private final Map<UUID, Map<String, UserData>> userData = new HashMap<>();
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
    public List<UserData> getLevelData(String level) {
        return this.userData.values().stream()
                .map(map -> map.get(level.toLowerCase()))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Cache the user's existing data
     *
     * @param data The data to cache
     */
    public void cacheUser(UserData data) {
        var mapData = this.userData.get(data.getPlayer());
        if (mapData == null)
            mapData = new HashMap<>();

        mapData.put(data.getLevel(), data);
        this.userData.put(data.getPlayer(), mapData);
    }

    /**
     * Save all of a player's data to the database
     *
     * @param player The player to save
     */
    public void saveUser(Player player) {
        var data = this.userData.get(player.getUniqueId());
        if (data == null)
            return;

        this.async(task -> this.databaseConnector.connect(connection -> {
            for (var entry : data.values()) {
                final var update = "REPLACE INTO " + this.getTablePrefix() + "data (" +
                        "player, " +
                        "`level`, " +
                        "completed, " +
                        "attempts, " +
                        "bestTime, " +
                        "lastTime, " +
                        "lastCompletion, " +
                        "totalTimes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (var statement = connection.prepareStatement(update)) {
                    statement.setString(1, entry.getPlayer().toString());
                    statement.setString(2, entry.getLevel().toLowerCase());
                    statement.setInt(3, entry.getCompleted());
                    statement.setInt(4, entry.getAttempts());
                    statement.setLong(5, entry.getBestTime());
                    statement.setLong(6, entry.getLastTime());
                    statement.setLong(7, entry.getLastCompletion());
                    statement.setString(8, this.gson.toJson(new TimesCompleted(entry.getTotalTimes())));
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
    public void saveUser(UserData data) {
        this.cacheUser(data);

        this.async(task -> this.databaseConnector.connect(connection -> {
            final var update = "REPLACE INTO " + this.getTablePrefix() + "data (" +
                    "player, " +
                    "`level`, " +
                    "completed, " +
                    "attempts, " +
                    "bestTime, " +
                    "lastTime, " +
                    "lastCompletion, " +
                    "totalTimes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (var statement = connection.prepareStatement(update)) {
                statement.setString(1, data.getPlayer().toString());
                statement.setString(2, data.getLevel().toLowerCase());
                statement.setInt(3, data.getCompleted());
                statement.setInt(4, data.getAttempts());
                statement.setLong(5, data.getBestTime());
                statement.setLong(6, data.getLastTime());
                statement.setLong(7, data.getLastCompletion());
                statement.setString(8, this.gson.toJson(new TimesCompleted(data.getTotalTimes())));
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete a user's level data from the database
     *
     * @param data The user's data
     */
    public void deleteUser(UserData data) {
        this.userData.remove(data.getPlayer());

        final var delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE player = ? AND level = ?";

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
    public void deleteUser(UUID player) {
        this.userData.remove(player);

        final var delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE player = ?";

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
    public void deleteLevel(String level) {
        final var delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE level = ?";

        this.async(task -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement(delete)) {
                statement.setString(1, level.toLowerCase());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete everyones level data from the database
     */
    public void deleteEveryone() {
        final var delete = "DELETE FROM " + this.getTablePrefix() + "data";

        this.async(task -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement(delete)) {
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
    @Nullable
    public UserData getData(UUID player, String level) {
        var data = this.userData.get(player);
        if (data == null)
            return null;

        return data.get(level);
    }

    /**
     * Get all of a player's data from the cache
     *
     * @param player The player to get data for
     * @return The player's data
     */
    @Nullable
    public Map<String, UserData> getData(UUID player) {
        return this.userData.get(player);
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
                    data.setCompleted(results.getInt("completed"));
                    data.setAttempts(results.getInt("attempts"));
                    data.setBestTime(results.getLong("bestTime"));
                    data.setLastTime(results.getLong("lastTime"));
                    data.setLastCompletion(results.getLong("lastCompletion"));
                    data.setTotalTimes(this.gson.fromJson(results.getString("totalTimes"), TimesCompleted.class).getTimes());
                    this.userData.computeIfAbsent(uuid, k -> new HashMap<>()).put(data.getLevel(), data);
                }
            }
        }));
    }

    /**
     * Save all users data to the database regardless of the level
     */
    public void saveAllUsers() {
        this.async(task -> this.databaseConnector.connect(connection -> {
            for (Map.Entry<UUID, Map<String, UserData>> entry : this.userData.entrySet()) {
                for (UserData data : entry.getValue().values()) {
                    final var update = "REPLACE INTO " + this.getTablePrefix() + "data (" +
                            "player, " +
                            "`level`, " +
                            "completed, " +
                            "attempts, " +
                            "bestTime, " +
                            "lastTime, " +
                            "lastCompletion, " +
                            "totalTimes) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    try (var statement = connection.prepareStatement(update)) {
                        statement.setString(1, data.getPlayer().toString());
                        statement.setString(2, data.getLevel().toLowerCase());
                        statement.setInt(3, data.getCompleted());
                        statement.setInt(4, data.getAttempts());
                        statement.setLong(5, data.getBestTime());
                        statement.setLong(6, data.getLastTime());
                        statement.setLong(7, data.getLastCompletion());
                        statement.setString(8, this.gson.toJson(new TimesCompleted(data.getTotalTimes())));
                        statement.executeUpdate();
                    }
                }
            }
        }));
    }

    /**
     * Save every single's user data to the database for a specific level
     *
     * @param level The level to save
     */
    public void saveAllUsers(String level) {
        this.async(task -> this.databaseConnector.connect(connection -> {
            for (Map.Entry<UUID, Map<String, UserData>> entry : this.userData.entrySet()) {
                for (UserData data : entry.getValue().values()) {
                    if (!data.getLevel().equals(level))
                        continue;

                    final var update = "REPLACE INTO " + this.getTablePrefix() + "data (" +
                            "player, " +
                            "`level`, " +
                            "completed, " +
                            "attempts, " +
                            "bestTime, " +
                            "lastTime, " +
                            "lastCompletion, " +
                            "totalTimes) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    try (var statement = connection.prepareStatement(update)) {
                        statement.setString(1, data.getPlayer().toString());
                        statement.setString(2, data.getLevel().toLowerCase());
                        statement.setInt(3, data.getCompleted());
                        statement.setInt(4, data.getAttempts());
                        statement.setLong(5, data.getBestTime());
                        statement.setLong(6, data.getLastTime());
                        statement.setLong(7, data.getLastCompletion());
                        statement.setString(8, this.gson.toJson(new TimesCompleted(data.getTotalTimes())));
                        statement.executeUpdate();
                    }
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

    public Map<UUID, Map<String, UserData>> getUserData() {
        return userData;
    }

}