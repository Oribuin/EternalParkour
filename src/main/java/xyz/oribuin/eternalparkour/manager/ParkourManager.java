package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.action.PluginAction;
import xyz.oribuin.eternalparkour.event.PlayerFinishLevelEvent;
import xyz.oribuin.eternalparkour.event.PlayerStartLevelEvent;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.parkour.Checkpoint;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.ParkourPlayer;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.parkour.RunSession;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ParkourManager extends Manager {

    // Map of all levels
    private final Map<String, Level> levelData = new HashMap<>(); // All level data
    private final Map<UUID, EditSession> levelEditors = new HashMap<>(); // List of players editing a level
    private final Map<UUID, RunSession> activeRunners = new HashMap<>(); // List of players currently running a parkour

    // Other ~ funny ~ stuff
    private final DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
    private final SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SSS");
    private CommentedFileConfiguration levelConfig;

    public ParkourManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {

        // Load all the levels from the levels folder
        this.levelData.clear();
        File file = new File(this.rosePlugin.getDataFolder(), "levels.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.levelConfig = CommentedFileConfiguration.loadConfiguration(file);
        this.loadLevels();
        this.dataManager.loadUserData();
    }

    /**
     * Load all the levels data from the levels.yml file
     */
    public void loadLevels() {
        // Check if the level config is null
        if (this.levelConfig == null) {
            this.rosePlugin.getLogger().severe("The level config doesn't exist, cannot load levels.");
            return;
        }

        CommentedConfigurationSection levels = this.levelConfig.getConfigurationSection("levels");

        if (levels == null) {
            this.rosePlugin.getLogger().info("We couldn't find any levels in the levels.yml file.");
            return;
        }

        // Load all the level data.
        for (String key : levels.getKeys(false)) {
            Level level = new Level(key);

            // Load the spawn location
            String spawnWorldName = levels.getString(key + ".spawn.world");
            double spawnX = levels.getDouble(key + ".spawn.x");
            double spawnY = levels.getDouble(key + ".spawn.y");
            double spawnZ = levels.getDouble(key + ".spawn.z");
            double spawnYaw = levels.getDouble(key + ".spawn.yaw");
            double spawnPitch = levels.getDouble(key + ".spawn.pitch");

            if (spawnWorldName == null) {
                this.rosePlugin.getLogger().severe("The spawn location is not set in the levels.yml file. This is required.");
                return;
            }

            World spawnWorld = Bukkit.getWorld(spawnWorldName);
            if (spawnWorld == null) {
                this.rosePlugin.getLogger().severe("The spawn world is not loaded. This is required.");
                return;
            }

            level.setTeleport(new Location(spawnWorld, spawnX, spawnY, spawnZ, (float) spawnYaw, (float) spawnPitch));

            // Load the parkour regions
            CommentedConfigurationSection regions = levels.getConfigurationSection(key + ".regions");
            if (regions != null) {

                // This is the region where the player starts and does the parkour
                CommentedConfigurationSection parkourRegions = regions.getConfigurationSection("level");
                if (parkourRegions != null) {

                    // Load all the parkour regions
                    for (String regionKey : parkourRegions.getKeys(false)) {
                        double firstPosX = parkourRegions.getDouble(regionKey + ".pos-1.x");
                        double firstPosY = parkourRegions.getDouble(regionKey + ".pos-1.y");
                        double firstPosZ = parkourRegions.getDouble(regionKey + ".pos-1.z");
                        Location firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                        double secondPosX = parkourRegions.getDouble(regionKey + ".pos-2.x");
                        double secondPosY = parkourRegions.getDouble(regionKey + ".pos-2.y");
                        double secondPosZ = parkourRegions.getDouble(regionKey + ".pos-2.z");
                        Location secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);

                        level.getLevelRegions().add(new Region(firstPos, secondPos));
                    }
                }

                // Load the finish region
                CommentedConfigurationSection finishRegion = regions.getConfigurationSection("finish");
                if (finishRegion != null) {
                    double firstPosX = finishRegion.getDouble("pos-1.x");
                    double firstPosY = finishRegion.getDouble("pos-1.y");
                    double firstPosZ = finishRegion.getDouble("pos-1.z");
                    Location firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                    double secondPosX = finishRegion.getDouble("pos-2.x");
                    double secondPosY = finishRegion.getDouble("pos-2.y");
                    double secondPosZ = finishRegion.getDouble("pos-2.z");
                    Location secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);

                    level.setFinishRegion(new Region(firstPos, secondPos));
                }

                CommentedConfigurationSection spawnRegion = regions.getConfigurationSection("spawn");
                if (spawnRegion != null) {
                    double firstPosX = spawnRegion.getDouble("pos-1.x");
                    double firstPosY = spawnRegion.getDouble("pos-1.y");
                    double firstPosZ = spawnRegion.getDouble("pos-1.z");
                    Location firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                    double secondPosX = spawnRegion.getDouble("pos-2.x");
                    double secondPosY = spawnRegion.getDouble("pos-2.y");
                    double secondPosZ = spawnRegion.getDouble("pos-2.z");
                    Location secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);

                    level.setStartRegion(new Region(firstPos, secondPos));
                }
            }

            // Okay, now that we have all the region data, lets load the checkpoints
            CommentedConfigurationSection checkpoints = levels.getConfigurationSection(key + ".checkpoints");
            if (checkpoints != null) {
                for (String checkpointKey : checkpoints.getKeys(false)) {
                    Integer checkpointNumber = PluginUtils.getInteger(checkpointKey);

                    if (checkpointNumber == null) {
                        this.rosePlugin.getLogger().severe("The checkpoint number is not a number. Skipping checkpoint" + checkpointKey);
                        continue;
                    }

                    Checkpoint checkpoint = new Checkpoint(checkpointNumber);

                    // Teleport location
                    double x = checkpoints.getDouble(checkpointKey + ".teleport.x");
                    double y = checkpoints.getDouble(checkpointKey + ".teleport.y");
                    double z = checkpoints.getDouble(checkpointKey + ".teleport.z");
                    double yaw = checkpoints.getDouble(checkpointKey + ".teleport.yaw");
                    double pitch = checkpoints.getDouble(checkpointKey + ".teleport.pitch");

                    Location teleport = new Location(spawnWorld, x, y, z, (float) yaw, (float) pitch);
                    checkpoint.setTeleport(teleport);

                    // Load the checkpoint regions
                    CommentedConfigurationSection checkpointRegions = checkpoints.getConfigurationSection(checkpointKey + ".region");
                    if (checkpointRegions != null) {
                        double firstPosX = checkpointRegions.getDouble("pos-1.x");
                        double firstPosY = checkpointRegions.getDouble("pos-1.y");
                        double firstPosZ = checkpointRegions.getDouble("pos-1.z");
                        Location firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                        double secondPosX = checkpointRegions.getDouble("pos-2.x");
                        double secondPosY = checkpointRegions.getDouble("pos-2.y");
                        double secondPosZ = checkpointRegions.getDouble("pos-2.z");
                        Location secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);

                        checkpoint.setRegion(new Region(firstPos, secondPos));
                    }

                    level.getCheckpoints().put(checkpointNumber, checkpoint);
                }
            }

            // Load the commands
            List<String> commands = levels.getStringList(key + ".commands");
            level.setCommands(
                    commands.stream().map(PluginAction::parse)
                            .filter(Objects::nonNull)
                            .toList()
            );

            // Load reward cooldown
            int cooldown = levels.getInt(key + ".cooldown");
            level.setCooldown(cooldown);

            // Check if the level is enabled
            boolean enabled = levels.getBoolean(key + ".enabled");
            level.setEnabled(enabled);

            // Get the amount of times the level has been completed
            int completed = levels.getInt(key + ".times-completed");
            level.setTimesCompleted(completed);

            // Get the max amount of times the level can be completed
            int maxCompleted = levels.getInt(key + ".max-completions");
            level.setMaxCompletions(maxCompleted);

            // Get the max amount of times the level can be attempted
            int maxAttempts = levels.getInt(key + ".max-attempts");
            level.setMaxAttempts(maxAttempts);

            // Calculate the level's leaderboard
            this.calculateLevel(level);

            // Add the level to the list
            this.levelData.put(key, level);

        }
    }

    /**
     * Save the general level data to the levels.yml file
     *
     * @param level The level to save
     */
    public void saveLevel(@NotNull Level level) {

        this.levelData.put(level.getId(), level);

        String startPath = "levels." + level.getId();

        // Save the level data
        this.levelConfig.set(startPath + ".enabled", level.isEnabled());
        this.levelConfig.set(startPath + ".cooldown", level.getCooldown());
        this.levelConfig.set(startPath + ".times-completed", level.getTimesCompleted());
        this.levelConfig.set(startPath + ".max-attempts", level.getMaxAttempts());
        this.levelConfig.set(startPath + ".max-completions", level.getMaxCompletions());

        // Save the commands to run
        this.levelConfig.set(startPath + ".commands", level.getCommands().stream()
                .map(PluginAction::serialize)
                .toList()
        );


        // Save the primary teleport location
        if (level.getTeleport() != null) {
            Location teleport = level.getTeleport();
            this.levelConfig.set(startPath + ".spawn.world", teleport.getWorld().getName());
            this.levelConfig.set(startPath + ".spawn.x", teleport.getX());
            this.levelConfig.set(startPath + ".spawn.y", teleport.getY());
            this.levelConfig.set(startPath + ".spawn.z", teleport.getZ());
            this.levelConfig.set(startPath + ".spawn.yaw", teleport.getYaw());
            this.levelConfig.set(startPath + ".spawn.pitch", teleport.getPitch());
        }

        // Get region index
        AtomicInteger currentIndex = new AtomicInteger(0);

        // Remove all the old region data
        this.levelConfig.set(startPath + ".regions", null);

        // Set all the spawn regions
        level.getLevelRegions().stream()
                .filter(region -> region.getPos1() != null && region.getPos2() != null)
                .forEach(region -> {
                    currentIndex.getAndIncrement();

                    this.levelConfig.set(startPath + ".regions.level." + currentIndex.get() + ".pos-1.x", region.getPos1().getX());
                    this.levelConfig.set(startPath + ".regions.level." + currentIndex.get() + ".pos-1.y", region.getPos1().getY());
                    this.levelConfig.set(startPath + ".regions.level." + currentIndex.get() + ".pos-1.z", region.getPos1().getZ());

                    // Save the second position
                    this.levelConfig.set(startPath + ".regions.level." + currentIndex.get() + ".pos-2.x", region.getPos2().getX());
                    this.levelConfig.set(startPath + ".regions.level." + currentIndex.get() + ".pos-2.y", region.getPos2().getY());
                    this.levelConfig.set(startPath + ".regions.level." + currentIndex.get() + ".pos-2.z", region.getPos2().getZ());
                });

        // Save the finish region
        Region finishRegion = level.getFinishRegion();
        if (finishRegion != null && finishRegion.getPos1() != null && finishRegion.getPos2() != null) {
            this.levelConfig.set(startPath + ".regions.finish.pos-1.x", finishRegion.getPos1().getX());
            this.levelConfig.set(startPath + ".regions.finish.pos-1.y", finishRegion.getPos1().getY());
            this.levelConfig.set(startPath + ".regions.finish.pos-1.z", finishRegion.getPos1().getZ());

            // Save the second position
            this.levelConfig.set(startPath + ".regions.finish.pos-2.x", finishRegion.getPos2().getX());
            this.levelConfig.set(startPath + ".regions.finish.pos-2.y", finishRegion.getPos2().getY());
            this.levelConfig.set(startPath + ".regions.finish.pos-2.z", finishRegion.getPos2().getZ());
        }

        // Save spawn region
        Region spawnRegion = level.getStartRegion();
        if (spawnRegion != null && spawnRegion.getPos1() != null && spawnRegion.getPos2() != null) {
            this.levelConfig.set(startPath + ".regions.spawn.pos-1.x", spawnRegion.getPos1().getX());
            this.levelConfig.set(startPath + ".regions.spawn.pos-1.y", spawnRegion.getPos1().getY());
            this.levelConfig.set(startPath + ".regions.spawn.pos-1.z", spawnRegion.getPos1().getZ());

            // Save the second position
            this.levelConfig.set(startPath + ".regions.spawn.pos-2.x", spawnRegion.getPos2().getX());
            this.levelConfig.set(startPath + ".regions.spawn.pos-2.y", spawnRegion.getPos2().getY());
            this.levelConfig.set(startPath + ".regions.spawn.pos-2.z", spawnRegion.getPos2().getZ());
        }

        // Save the spawn location
        if (level.getTeleport() != null) {
            this.levelConfig.set(startPath + ".spawn.x", level.getTeleport().getX());
            this.levelConfig.set(startPath + ".spawn.y", level.getTeleport().getY());
            this.levelConfig.set(startPath + ".spawn.z", level.getTeleport().getZ());
            this.levelConfig.set(startPath + ".spawn.yaw", level.getTeleport().getYaw());
            this.levelConfig.set(startPath + ".spawn.pitch", level.getTeleport().getPitch());
        }

        // Remove all the old checkpoint data
        this.levelConfig.set(startPath + ".checkpoints", null);

        // Save the checkpoints
        level.getCheckpoints().forEach((id, checkpoint) -> {
            Region region = checkpoint.getRegion();
            if (region != null && region.getPos1() != null && region.getPos2() != null) {
                this.levelConfig.set(startPath + ".checkpoints." + id + ".region.pos-1.x", region.getPos1().getX());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".region.pos-1.y", region.getPos1().getY());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".region.pos-1.z", region.getPos1().getZ());

                // Save the second position
                this.levelConfig.set(startPath + ".checkpoints." + id + ".region.pos-2.x", region.getPos2().getX());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".region.pos-2.y", region.getPos2().getY());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".region.pos-2.z", region.getPos2().getZ());
            }

            if (checkpoint.getTeleport() != null) {
                Location teleport = checkpoint.getTeleport();
                this.levelConfig.set(startPath + ".checkpoints." + id + ".teleport.world", teleport.getWorld().getName());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".teleport.x", teleport.getX());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".teleport.y", teleport.getY());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".teleport.z", teleport.getZ());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".teleport.yaw", teleport.getYaw());
                this.levelConfig.set(startPath + ".checkpoints." + id + ".teleport.pitch", teleport.getPitch());
            }
        });


        // Save the file
        this.levelConfig.save();
    }

    /**
     * Cache a level in memory for faster access
     *
     * @param level The level to cache
     */
    public void cacheLevel(@NotNull Level level) {
        this.levelData.put(level.getId(), level);
    }

    /**
     * Delete all a level's data from the levels.yml file and database
     *
     * @param level The level to delete
     */
    public void deleteLevel(@NotNull Level level) {
        this.levelData.remove(level.getId());
        this.levelConfig.set("levels." + level.getId(), null);
        this.levelConfig.save();

        // Delete all active editor sessions for this level
        this.levelEditors.entrySet().removeIf(entry -> entry.getValue().getLevel().equals(level));

        // Delete all active player sessions for this level
        this.activeRunners.entrySet().removeIf(entry -> entry.getValue().getLevel().equals(level));

        // Delete all the level data from the database
        this.dataManager.deleteLevel(level.getId());
    }

    /**
     * Get a level by its id
     *
     * @param id The id of the level
     * @return The level
     */
    @Nullable
    public Level getLevel(@NotNull String id) {
        return this.levelData.get(id);
    }

    /**
     * Get all the levels
     *
     * @return A list of all the levels
     */
    @NotNull
    public List<Level> getLevels() {
        return new ArrayList<>(this.levelData.values());
    }

    /**
     * Get all the levels that are enabled
     *
     * @return A list of all the enabled levels
     */
    @NotNull
    public List<Level> getEnabledLevels() {
        return this.levelData.values().stream()
                .filter(Level::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * Get all the levels that are disabled
     *
     * @return A list of all the disabled levels
     */
    @NotNull
    public List<Level> getDisabledLevels() {
        return this.levelData.values().stream()
                .filter(level -> !level.isEnabled())
                .collect(Collectors.toList());
    }

    /**
     * Get the data for a player on a level
     *
     * @param uuid  The uuid of the player
     * @param level The level
     * @return The player's level data
     */
    @NotNull
    public UserData getUser(@NotNull UUID uuid, @NotNull String level) {
        return this.dataManager.getData(uuid, level);
    }

    /**
     * Get all data for a player across all levels
     *
     * @param uuid The uuid of the player
     * @return A list of all the player's level data
     */
    @NotNull
    public Map<String, UserData> getUser(@NotNull UUID uuid) {
        return this.dataManager.getData(uuid);
    }

    /**
     * Save a player's level data
     *
     * @param userData The player's level data
     */
    public void saveUserData(@NotNull UserData userData) {
        this.dataManager.saveUser(userData);
    }

    /**
     * Delete a user's level data for a level
     *
     * @param uuid  The uuid of the player
     * @param level The level
     */
    public void deleteUser(@NotNull UUID uuid, @NotNull Level level) {
        UserData data = this.dataManager.getData(uuid).get(level.getId());

        if (data != null)
            this.dataManager.deleteUser(data);
    }

    /**
     * Delete a player's level data
     *
     * @param userData The player's level data
     */
    public void deleteUser(@NotNull UserData userData) {
        this.dataManager.deleteUser(userData);
    }

    /**
     * Deletes all a player's level data
     *
     * @param uuid The uuid of the player
     */
    public void deleteUser(@NotNull UUID uuid) {
        this.dataManager.deleteUser(uuid);
    }

    /**
     * Cache a player's level data in memory for faster access
     *
     * @param userData The player's level data
     */
    public void cacheUser(@NotNull UserData userData) {
        this.dataManager.cacheUser(userData);
    }

    /**
     * Get the level that a player is currently playing
     *
     * @param uuid The uuid of the player
     * @return The level the player is playing
     */
    @Nullable
    public Level getPlayingLevel(@NotNull UUID uuid) {
        RunSession session = this.activeRunners.get(uuid);
        if (session == null) {
            return null;
        }

        return this.levelData.get(session.getLevel().getId());
    }

    /**
     * Check if a location is currently in a level's parkour region
     *
     * @param level    The level
     * @param location The location
     * @return True if the location is in the parkour region
     */
    public boolean isInsideParkourRegion(@NotNull Level level, @NotNull Location location) {
        return level.getLevelRegions().stream().anyMatch(region -> region.isInside(location));
    }

    /**
     * Check if a location is currently in a level's finish region
     *
     * @param level    The level
     * @param location The location
     * @return True if the location is in the finish region
     */
    public boolean isFinished(@NotNull Level level, @NotNull Location location) {
        Region region = level.getRegionAt(location);
        return region != null && level.isFinishRegion(region);
    }

    /**
     * Check if a location is currently a checkpoint
     *
     * @param level    The level
     * @param location The location
     * @return True if the location is a checkpoint
     */
    public boolean atCheckpoint(@NotNull Level level, @NotNull Location location) {
        return level.getCheckpoints().values().stream().anyMatch(checkpoint -> checkpoint.equals(location));
    }

    /**
     * Get the checkpoint number of a location
     *
     * @param level    The level
     * @param location The location
     * @return The checkpoint number
     */
    public int getCheckpoint(@NotNull Level level, @NotNull Location location) {
        return level.getCheckpoints().entrySet().stream()
                .filter(entry -> entry.getValue().equals(location))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(-1);
    }


    /**
     * Check if a location is currently in any level's parkour region
     *
     * @param location The location
     * @return True if the location is in a parkour region
     */
    public boolean isInsideAnyLevel(@NotNull Location location) {
        return this.levelData.values().stream().anyMatch(level -> level.getRegionAt(location) != null);
    }

    /**
     * Get the level that a location is currently in
     *
     * @param location The location
     * @return The level
     */
    @Nullable
    public Level getLevel(@NotNull Location location) {
        return this.levelData.values().stream()
                .filter(level -> level.getRegionAt(location) != null)
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if a player is currently playing in a level
     *
     * @param uuid The uuid of the player
     * @return True if the player is in a level
     */
    public boolean isPlaying(@NotNull UUID uuid) {
        return this.activeRunners.containsKey(uuid);
    }

    /**
     * Calculate a level's data for leaderboards.
     *
     * @param level The level
     */
    @NotNull
    public Level calculateLevel(@NotNull Level level) {
        // Get all players who have completed the level
        List<UserData> levelData = this.dataManager.getLevelData(level.getId());

        // Calculate average total time completions
        List<Long> times = new ArrayList<>();

        for (UserData userData : levelData) {
            times.addAll(userData.getTotalTimes());
        }

        long averageBestTime = (long) times.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        level.setAverageTime(averageBestTime);

        // Calculate the top times for the level into Map<Integer, UserData>
        Map<Integer, UserData> topTimes = new HashMap<>();
        List<UserData> sortedTimes = levelData.stream()
                .filter(userData -> userData.getBestTime() != -1)
                .sorted(Comparator.comparingLong(UserData::getBestTime))
                .limit(Setting.LEADERBOARD_MAX_SIZE.getInt() == -1 ? levelData.size() : Setting.LEADERBOARD_MAX_SIZE.getInt()) // If the max size is -1, show all players
                .toList();

        for (int i = 0; i < sortedTimes.size(); i++) {
            topTimes.put(i + 1, sortedTimes.get(i));
        }

        level.setTopUsers(topTimes);
        this.cacheLevel(level);
        return level;
    }

    /**
     * Set a player to start a level
     *
     * @param player The player
     * @param level  The level
     */
    @Nullable
    public RunSession startRun(@NotNull Player player, @NotNull Level level) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        ParkourPlayer pplayer = this.getPPlayer(player);

        // Check if the player is already playing a level, if so cancel the run
        if (this.isPlaying(player.getUniqueId())) {
            this.cancelRun(player, false);
        }

        // Check if the player is editing a level, if the player is viewing the level, who cares
        if (this.levelEditors.containsKey(player.getUniqueId()) && this.levelEditors.get(player.getUniqueId()).getType() != EditType.VIEWING)
            return null;

        // Don't allow players to start a level if they are in spectator mode or flying
        if (player.getGameMode() == GameMode.SPECTATOR || player.isFlying()) {
            return null;
        }

        // Check if the player is in the level's finish region
        if (this.isFinished(level, player.getLocation()) && this.isPlaying(player.getUniqueId())) {
            this.finishRun(player);
            return null;
        }

        // current data for the player in the level
        UserData levelData = this.getUser(player.getUniqueId(), level.getId());
        final StringPlaceholders plc = StringPlaceholders.builder()
                .addPlaceholder("level", level.getId())
                .addPlaceholder("attempts", levelData.getAttempts())
                .addPlaceholder("completions", levelData.getCompletions())
                .addPlaceholder("max_attempts", level.getMaxAttempts())
                .addPlaceholder("max_completions", level.getMaxCompletions())
                .build();


        if (level.getMaxAttempts() > 0 && levelData.getAttempts() >= level.getMaxAttempts()) {
            locale.sendMessage(player, "parkour-max-attempts", plc);

            // Teleport the player to the level's spawn
            if (level.getTeleport() != null) {
                this.teleport(player, level.getTeleport());
            }

            return null;
        }

        if (level.getMaxCompletions() > 0 && levelData.getCompletions() >= level.getMaxCompletions()) {
            locale.sendMessage(player, "parkour-max-completions", plc);

            // Teleport the player to the level's spawn
            if (level.getTeleport() != null) {
                this.teleport(player, level.getTeleport());
            }

            return null;
        }

        // Create a new parkour session
        RunSession parkourRun = new RunSession(player.getUniqueId(), level);

        PlayerStartLevelEvent event = new PlayerStartLevelEvent(player, level, parkourRun);
        this.rosePlugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        this.activeRunners.put(player.getUniqueId(), parkourRun);
        return parkourRun;
    }

    /**
     * Set a player to finish a level
     * This will also save the player's data
     *
     * @param player The player
     */
    public void finishRun(@NotNull Player player) {
        ParkourPlayer pplayer = this.getPPlayer(player);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Level level = this.getPlayingLevel(player.getUniqueId());
        UUID uuid = player.getUniqueId();
        long finishTime = System.currentTimeMillis(); // We're declaring it here, so we can get the most accurate time

        // Player is not playing a level
        if (level == null) {
            return;
        }

        // Check if the player is editing a level, if the player is viewing the level, who cares
        if (this.levelEditors.containsKey(uuid) && this.levelEditors.get(uuid).getType() != EditType.VIEWING)
            return;

        // Don't allow players to finish a level if they are in spectator mode or flying
        if (player.getGameMode() == GameMode.SPECTATOR || player.isFlying()) {
            return;
        }

        RunSession parkourRun = this.activeRunners.get(uuid);
        parkourRun.setEndTime(finishTime);

        PlayerFinishLevelEvent event = new PlayerFinishLevelEvent(player, level, parkourRun);
        this.rosePlugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.activeRunners.remove(uuid); // Remove the player from the active runners

        UserData data = this.getUser(player.getUniqueId(), level.getId());
        if (level.getTeleport() != null)
            this.teleport(player, level.getTeleport());

        // Check if the player has finished the level before the cooldown ends
        if (System.currentTimeMillis() - data.getLastCompletion() > level.getCooldown()) {
            StringPlaceholders plc = StringPlaceholders.single("cooldown", PluginUtils.parseFromTime(System.currentTimeMillis() - level.getCooldown()));
            level.getCommands().forEach(command -> command.execute(player, plc));
        }

        long completionTime = (parkourRun.getEndTime() - parkourRun.getStartTime());

        // Update the player's data
        data.setName(player.getName());
        data.setLastTime(completionTime);
        data.setLastCompletion(parkourRun.getEndTime());
        data.setAttempts(data.getAttempts() + 1);
        data.setCompletions(data.getCompletions() + 1);
        data.getTotalTimes().add(data.getLastTime());

        final StringPlaceholders plc = StringPlaceholders.builder()
                .addPlaceholder("level", level.getId())
                .addPlaceholder("time", formatter.format(new Date(completionTime)))
                .addPlaceholder("completion", data.getCompletions())
                .addPlaceholder("attempts", data.getAttempts())
                .addPlaceholder("best", formatter.format(new Date(data.getBestTime())))
                .build();

        boolean newBestTime = false;
        if (completionTime > data.getBestTime() && data.getBestTime() != 0) {
            this.cacheUser(data);
        } else {
            newBestTime = true;
            data.setBestTime(completionTime);
            data.setBestTimeAchieved(parkourRun.getEndTime());
            this.cacheUser(data);

            this.calculateLevel(level); // Calculate the level again, since the best time has changed and who knows, maybe the player is now in the top 10
        }

        // Update the level's data
        level.setTimesCompleted(level.getTimesCompleted() + 1);
        this.saveLevel(level);

        if (newBestTime) {
            locale.sendMessage(player, "parkour-finish-new-best", plc);
        } else {
            locale.sendMessage(player, "parkour-finish", plc);
        }


    }

    /**
     * Cancel a player's parkour run, this will not save the player's data
     *
     * @param player The player
     */
    public void cancelRun(@NotNull Player player, boolean teleport) {

        // Player is not playing a level
        ParkourPlayer pplayer = this.getPPlayer(player);
        RunSession run = this.activeRunners.get(player.getUniqueId());

        if (run == null)
            return;

        // Send cancel message here
        this.activeRunners.remove(player.getUniqueId());

        if (run.getLevel().getTeleport() != null && teleport)
            this.teleport(player, run.getLevel().getTeleport());
    }

    /**
     * Fail a player's parkour run, this will cache a player's data with a failed attempt
     *
     * @param player The player
     */
    public void failRun(@NotNull Player player) {
        ParkourPlayer pplayer = this.getPPlayer(player);
        Level level = this.getPlayingLevel(player.getUniqueId());
        RunSession session = this.getRunSession(player.getUniqueId());

        // Player is not playing a level
        if (level == null || session == null) {
            return;
        }

        // Teleport the player back to the checkpoint if they have one
        if (session.getCheckpoint() != null && level.getCheckpoints().size() > 0) {
            session.setAttempts(session.getAttempts() + 1);
            this.activeRunners.put(player.getUniqueId(), session);

            Checkpoint current = session.getCheckpoint();
            // This is redundant, but it's here because intellij was complaining
            if (current.getTeleport() != null)
                this.teleport(player, PluginUtils.asCenterLoc(current.getTeleport(), player.getLocation().getYaw(), player.getLocation().getPitch()));

            return;
        }

        // Update the player's data
        UserData data = this.getUser(player.getUniqueId(), level.getId());
        data.setAttempts(data.getAttempts() + 1);

        this.dataManager.cacheUser(data);

        // Send fail message here
        this.activeRunners.remove(player.getUniqueId());
        if (level.getTeleport() != null)
            this.teleport(player, level.getTeleport());
    }

    /**
     * Teleport a player to a location
     *
     * @param player   The player
     * @param location The location
     */
    public void teleport(@NotNull Player player, @NotNull Location location) {
        if (PluginUtils.usingPaper()) {
            player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            return;
        }

        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Get a player's parkour run
     *
     * @param player The player
     * @return The player's parkour run
     */
    @Nullable
    public RunSession getRunSession(@NotNull ParkourPlayer player) {
        return this.activeRunners.get(player.getUUID());
    }

    /**
     * Get a player's parkour run
     *
     * @param uuid The player's UUID
     * @return The player's parkour run
     */
    @Nullable
    public RunSession getRunSession(@NotNull UUID uuid) {
        return this.activeRunners.get(uuid);
    }

    /**
     * Get a player's parkour run
     *
     * @param player The player
     * @return The player's parkour run
     */
    @Nullable
    public RunSession getRunSession(@NotNull Player player) {
        return this.activeRunners.get(player);
    }

    /**
     * Cache a player's current run session
     *
     * @param session The session
     */
    public void saveRunSession(@NotNull RunSession session) {
        this.activeRunners.put(session.getPlayer(), session);
    }

    /**
     * End a player's parkour run
     *
     * @param player The player
     * @return The player's parkour run
     */
    public boolean endRunSession(@NotNull ParkourPlayer player) {
        RunSession session = this.getRunSession(player);
        if (session == null)
            return false;

        this.activeRunners.remove(player.getUUID());
        return true;
    }

    /**
     * Start editing a parkour level
     *
     * @param player The player
     * @param level  The level
     * @param type   The type of edit
     */
    @Nullable
    public Level startEditing(@NotNull Player player, @Nullable Level level, @NotNull EditType type) {
        ParkourPlayer pplayer = this.getPPlayer(player);
        EditSession session = this.levelEditors.get(player.getUniqueId());

        if (this.levelEditors.containsKey(player.getUniqueId())) {
            this.saveEditSession(player);
        }

        if (level == null) {
            if (session == null) {
                this.rosePlugin.getManager(LocaleManager.class).sendMessage(player, "argument-handler-level");
                return null;
            }

            level = session.getLevel();
        }

        this.levelEditors.put(player.getUniqueId(), new EditSession(level, type));
        return level;
    }

    /**
     * Stop editing a player's parkour level
     *
     * @param player The player
     */
    public void stopEditing(@NotNull Player player) {
        ParkourPlayer pplayer = this.getPPlayer(player);
        EditSession session = this.levelEditors.get(player.getUniqueId());
        if (session == null)
            return;

        this.saveEditSession(player);
        this.levelEditors.remove(player.getUniqueId());
    }

    /**
     * Get a player's edit session
     *
     * @param player The player
     * @return The player's edit session
     */
    @Nullable
    public EditSession getEditSession(@NotNull Player player) {
        return this.levelEditors.get(player.getUniqueId());
    }

    /**
     * Get a player's edit session
     *
     * @param uuid The player's UUID
     * @return The player's edit session
     */
    @Nullable
    public EditSession getEditSession(@NotNull UUID uuid) {
        return this.levelEditors.get(uuid);
    }

    /**
     * Save a player's current edit session to the level file
     *
     * @param player The player
     */
    public void saveEditSession(@NotNull Player player) {
        ParkourPlayer pplayer = this.getPPlayer(player);
        EditSession session = this.levelEditors.get(player.getUniqueId());
        if (session == null)
            return;

        Level sessionLevel = session.getLevel();
        Level cacheLevel = this.getLevel(sessionLevel.getId());
        if (cacheLevel == null)
            return;

        // We're only looking to edit specific things
        cacheLevel.setTeleport(sessionLevel.getTeleport());
        cacheLevel.setLevelRegions(sessionLevel.getLevelRegions());
        cacheLevel.setFinishRegion(sessionLevel.getFinishRegion());
        cacheLevel.setTeleport(sessionLevel.getTeleport());
        cacheLevel.setCheckpoints(sessionLevel.getCheckpoints());

        // Save the level
        this.saveLevel(cacheLevel);
    }


    /**
     * Get all the users inside a level
     *
     * @param level The level
     * @return The users
     */
    public Map<UUID, UserData> getUsersInLevel(Level level) {
        Map<UUID, UserData> users = new HashMap<>();
        for (RunSession user : this.activeRunners.values()) {
            if (!user.getLevel().getId().equals(level.getId()))
                return users;

            UserData data = this.getUser(user.getPlayer(), level.getId());
            users.put(user.getPlayer(), data);
        }

        return users;
    }

    /**
     * Check if the player's username is valid
     *
     * @param player The player
     */
    public void checkUserName(@NotNull OfflinePlayer player) {
        Map<String, UserData> dataMap = new HashMap<>(this.getUser(player.getUniqueId()));
        for (UserData data : dataMap.values()) {
            if (data.checkUser(player))
                this.saveUserData(data);
        }
    }

    /**
     * Delete all playerdata for a level
     *
     * @param level The level
     */
    public void deleteLevelData(@NotNull Level level) {
        this.dataManager.deleteLevel(level.getId());
        this.calculateLevel(level);

        level.setTimesCompleted(0);
        this.saveLevel(level);
    }

    /**
     * Short function to get a player's parkour data
     *
     * @param player The player
     * @return The player's parkour data
     */
    public ParkourPlayer getPPlayer(@NotNull Player player) {
        return this.dataManager.getCachedPlayer(player);
    }

    /**
     * Short function to get a player's parkour data
     *
     * @param uuid The player's UUID
     * @return The player's parkour data
     */
    public ParkourPlayer getPPlayer(@NotNull UUID uuid) {
        return this.dataManager.getCachedPlayer(uuid);
    }

    public boolean isEditing(Player player) {
        return this.levelEditors.containsKey(player.getUniqueId());
    }

    @Override
    public void disable() {
        this.levelData.clear();
        this.levelEditors.clear();
        this.activeRunners.clear();
    }

    public Map<String, Level> getLevelData() {
        return this.levelData;
    }

    public Map<UUID, EditSession> getLevelEditors() {
        return this.levelEditors;
    }

    public Map<UUID, RunSession> getActiveRunners() {
        return this.activeRunners;
    }

}
