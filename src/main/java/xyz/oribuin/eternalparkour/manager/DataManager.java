package xyz.oribuin.eternalparkour.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.database.migration._1_CreateInitialTables;
import xyz.oribuin.eternalparkour.parkour.ParkourPlayer;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.util.TimesCompleted;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DataManager extends AbstractDataManager {

    // UUID = Player's UUID, String = Parkour ID, UserData = Parkour Data
    private final Map<UUID, ParkourPlayer> userData = new HashMap<>();
    //    private final Table<UUID, String, UserData> userData = HashBasedTable.create();
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
    @NotNull
    public List<UserData> getLevelData(@NotNull String level) {
        return this.userData.values().stream()
                .flatMap(x -> x.getUserData().values().stream())
                .filter(x -> x.getLevel().equalsIgnoreCase(level))
                .collect(Collectors.toList());
    }

    /**
     * Get the cached data for a player
     *
     * @param uuid The player's UUID
     * @return The cached data for the player
     */
    public ParkourPlayer getCachedPlayer(@NotNull UUID uuid) {
        return this.userData.getOrDefault(uuid, new ParkourPlayer(uuid));
    }

    /**
     * Get the cached data for a player
     *
     * @param player The player to get the data for
     * @return The cached data for the player
     */
    public ParkourPlayer getCachedPlayer(@NotNull Player player) {
        return this.userData.getOrDefault(player.getUniqueId(), new ParkourPlayer(player));
    }

    /**
     * Cache the user's existing data
     *
     * @param data The data to cache
     */
    public void cacheUser(@NotNull UserData data) {
        ParkourPlayer player = this.userData.getOrDefault(data.getPlayer(), new ParkourPlayer(data.getPlayer()));

        player.getUserData().put(data.getLevel(), data);
        this.userData.put(data.getPlayer(), player);
    }

    /**
     * Save all of a player's data to the database
     *
     * @param player The player to save
     */
    public void saveUser(@NotNull Player player) {

        ParkourPlayer pplayer = this.getCachedPlayer(player);

        this.async(task -> this.databaseConnector.connect(connection -> {
            for (UserData entry : pplayer.getUserData().values()) {
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

                try (PreparedStatement statement = connection.prepareStatement(update)) {
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

            try (PreparedStatement statement = connection.prepareStatement(update)) {
                statement.setString(1, data.getPlayer().toString());
                statement.setString(2, data.getLevel().toLowerCase());
                statement.setString(3, data.getName());
                statement.setInt(4, data.getCompletions());
                statement.setInt(5, data.getAttempts());
                statement.setLong(6, data.getBestTime());
                statement.setLong(7, data.getBestTimeAchieved());
                statement.setLong(8, data.getLastTime());
                statement.setLong(9, data.getLastCompletion());
                statement.setString(10, this.gson.toJson(new TimesCompleted(data.getTotalTimes())));
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
        Optional.ofNullable(this.userData.get(data.getPlayer()))
                .ifPresent(x -> x.getUserData().remove(data.getLevel()));

        final String delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE player = ? AND level = ?";
        this.async(task -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(delete)) {
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
        this.userData.remove(player);

        final String delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE player = ?";
        this.async(task -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(delete)) {
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
        this.userData.values().forEach(x -> x.getUserData().remove(level));

        final String delete = "DELETE FROM " + this.getTablePrefix() + "data WHERE level = ?";
        this.async(task -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(delete)) {
                statement.setString(1, level.toLowerCase());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Get a player's data from the cache for a specific level
     *
     * @param uuid  The player to get data for
     * @param level The level to get data for
     * @return The player's data for the level
     */
    @NotNull
    public UserData getData(@NotNull UUID uuid, @NotNull String level) {
        return this.getCachedPlayer(uuid).getUserData()
                .getOrDefault(level, new UserData(uuid, level));
    }

    /**
     * Get all of a player's data from the cache
     *
     * @param player The player to get data for
     * @return The player's data
     */
    @NotNull
    public Map<String, UserData> getData(@NotNull UUID player) {
        return Optional.ofNullable(this.userData.get(player))
                .map(ParkourPlayer::getUserData)
                .orElseGet(HashMap::new);
    }

    /**
     * Load every single user into memory
     */
    public void loadUserData() {
        this.userData.clear();

        this.async(task -> this.databaseConnector.connect(connection -> {
            final String select = "SELECT * FROM " + this.getTablePrefix() + "data";

            try (PreparedStatement statement = connection.prepareStatement(select)) {
                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    UUID uuid = UUID.fromString(results.getString("player"));
                    UserData data = new UserData(uuid, results.getString("level").toLowerCase());
                    data.setName(results.getString("username"));
                    data.setCompletions(results.getInt("completed"));
                    data.setAttempts(results.getInt("attempts"));
                    data.setBestTime(results.getLong("bestTime"));
                    data.setBestTimeAchieved(results.getLong("bestTimeAchieved"));
                    data.setLastTime(results.getLong("lastTime"));
                    data.setLastCompletion(results.getLong("lastCompletion"));
                    data.setTotalTimes(this.gson.fromJson(results.getString("totalTimes"), TimesCompleted.class).getTimes());

                    ParkourPlayer parkourPlayer = this.userData.getOrDefault(uuid, new ParkourPlayer(uuid));
                    parkourPlayer.getUserData().put(data.getLevel(), data);
                    this.userData.put(uuid, parkourPlayer);
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

    public Map<UUID, ParkourPlayer> getUserData() {
        return userData;
    }

}
