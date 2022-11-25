package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.hook.PAPI;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.parkour.RunSession;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ParkourManager extends Manager {

    private final Map<String, Level> levelData = new HashMap<>(); // All level data
    private final Map<UUID, EditSession> levelEditors = new HashMap<>(); // List of players editing a level
    private final Map<UUID, RunSession> activeRunners = new HashMap<>(); // List of players currently running a parkour

    private CommentedFileConfiguration levelConfig;

    public ParkourManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {

        // Load all the levels from the levels folder
        this.levelData.clear();
        var file = new File(this.rosePlugin.getDataFolder(), "levels.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.levelConfig = CommentedFileConfiguration.loadConfiguration(file);
        this.loadLevels();
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

        var levels = this.levelConfig.getConfigurationSection("levels");

        if (levels == null) {
            this.rosePlugin.getLogger().info("We couldn't find any levels in the levels.yml file.");
            return;
        }

        // Load all the level data.
        for (var key : levels.getKeys(false)) {
            var level = new Level(key);

            // Load the spawn location
            var spawnWorldName = levels.getString(key + ".spawn.world");
            var spawnX = levels.getDouble(key + ".spawn.x");
            var spawnY = levels.getDouble(key + ".spawn.y");
            var spawnZ = levels.getDouble(key + ".spawn.z");
            var spawnYaw = levels.getDouble(key + ".spawn.yaw");
            var spawnPitch = levels.getDouble(key + ".spawn.pitch");

            if (spawnWorldName == null) {
                this.rosePlugin.getLogger().severe("The spawn location is not set in the levels.yml file. This is required.");
                return;
            }

            var spawnWorld = Bukkit.getWorld(spawnWorldName);
            if (spawnWorld == null) {
                this.rosePlugin.getLogger().severe("The spawn world is not loaded. This is required.");
                return;
            }

            level.setTeleport(new Location(spawnWorld, spawnX, spawnY, spawnZ, (float) spawnYaw, (float) spawnPitch));

            // Load the parkour regions
            var regions = levels.getConfigurationSection(key + ".regions");
            if (regions != null) {

                // This is the region where the player starts and does the parkour
                var parkourRegions = regions.getConfigurationSection("level");
                if (parkourRegions != null) {

                    // Load all the parkour regions
                    for (var regionKey : parkourRegions.getKeys(false)) {
                        var firstPosX = parkourRegions.getDouble(regionKey + ".pos-1.x");
                        var firstPosY = parkourRegions.getDouble(regionKey + ".pos-1.y");
                        var firstPosZ = parkourRegions.getDouble(regionKey + ".pos-1.z");
                        var firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                        var secondPosX = parkourRegions.getDouble(regionKey + ".pos-2.x");
                        var secondPosY = parkourRegions.getDouble(regionKey + ".pos-2.y");
                        var secondPosZ = parkourRegions.getDouble(regionKey + ".pos-2.z");
                        var secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);

                        level.getLevelRegions().add(new Region(firstPos, secondPos));
                    }
                }

                // Load the finish region
                var finishRegion = regions.getConfigurationSection("finish");
                if (finishRegion != null) {
                    var firstPosX = finishRegion.getDouble("pos-1.x");
                    var firstPosY = finishRegion.getDouble("pos-1.y");
                    var firstPosZ = finishRegion.getDouble("pos-1.z");

                    var firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                    var secondPosX = finishRegion.getDouble("pos-2.x");
                    var secondPosY = finishRegion.getDouble("pos-2.y");
                    var secondPosZ = finishRegion.getDouble("pos-2.z");

                    var secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);
                    level.setFinishRegion(new Region(firstPos, secondPos));
                }

                var spawnRegion = regions.getConfigurationSection("spawn");
                if (spawnRegion != null) {
                    var firstPosX = spawnRegion.getDouble("pos-1.x");
                    var firstPosY = spawnRegion.getDouble("pos-1.y");
                    var firstPosZ = spawnRegion.getDouble("pos-1.z");

                    var firstPos = new Location(spawnWorld, firstPosX, firstPosY, firstPosZ);

                    var secondPosX = spawnRegion.getDouble("pos-2.x");
                    var secondPosY = spawnRegion.getDouble("pos-2.y");
                    var secondPosZ = spawnRegion.getDouble("pos-2.z");

                    var secondPos = new Location(spawnWorld, secondPosX, secondPosY, secondPosZ);
                    level.setStartRegion(new Region(firstPos, secondPos));
                }
            }

            // Okay, now that we have all the region data, lets load the checkpoints
            var checkpoints = levels.getConfigurationSection(key + ".checkpoints");
            if (checkpoints != null) {
                int currentCheckpoint = 1;
                for (var checkpointKey : checkpoints.getKeys(false)) {
                    var x = checkpoints.getDouble(checkpointKey + ".x");
                    var y = checkpoints.getDouble(checkpointKey + ".y");
                    var z = checkpoints.getDouble(checkpointKey + ".z");
                    var yaw = checkpoints.getDouble(checkpointKey + ".yaw");
                    var pitch = checkpoints.getDouble(checkpointKey + ".pitch");

                    var location = new Location(spawnWorld, x, y, z, (float) yaw, (float) pitch);
                    level.getCheckpoints().put(currentCheckpoint++, location);
                }
            }

            // Load the commands
            var commands = levels.getStringList(key + ".commands");
            level.setCommands(commands);

            // Load reward cooldown
            var cooldown = levels.getInt(key + ".cooldown");
            level.setCooldown(cooldown);

            // Check if the level is enabled
            var enabled = levels.getBoolean(key + ".enabled");
            level.setEnabled(enabled);

            // Get the amount of times the level has been completed
            var completed = levels.getInt(key + ".times-completed");
            level.setTimesCompleted(completed);

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

        var startPath = "levels." + level.getId();

        // Save the level data
        this.levelConfig.set(startPath + ".enabled", level.isEnabled());
        this.levelConfig.set(startPath + ".cooldown", level.getCooldown());
        this.levelConfig.set(startPath + ".commands", level.getCommands());
        this.levelConfig.set(startPath + ".times-completed", level.getTimesCompleted());

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
        var finishRegion = level.getFinishRegion();
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
        var spawnRegion = level.getStartRegion();
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
            this.levelConfig.set(startPath + ".spawn.world", level.getTeleport().getWorld().getName());
            this.levelConfig.set(startPath + ".spawn.x", level.getTeleport().getX());
            this.levelConfig.set(startPath + ".spawn.y", level.getTeleport().getY());
            this.levelConfig.set(startPath + ".spawn.z", level.getTeleport().getZ());
            this.levelConfig.set(startPath + ".spawn.yaw", level.getTeleport().getYaw());
            this.levelConfig.set(startPath + ".spawn.pitch", level.getTeleport().getPitch());
        }

        // Remove all the old checkpoint data
        this.levelConfig.set(startPath + ".checkpoints", null);

        // Save the checkpoints
        level.getCheckpoints().forEach((checkpoint, location) -> {
            this.levelConfig.set(startPath + ".checkpoints." + checkpoint + ".x", location.getX());
            this.levelConfig.set(startPath + ".checkpoints." + checkpoint + ".y", location.getY());
            this.levelConfig.set(startPath + ".checkpoints." + checkpoint + ".z", location.getZ());
            this.levelConfig.set(startPath + ".checkpoints." + checkpoint + ".yaw", location.getYaw());
            this.levelConfig.set(startPath + ".checkpoints." + checkpoint + ".pitch", location.getPitch());
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

        // Delete all the level data from the database
        this.rosePlugin.getManager(DataManager.class).deleteLevel(level.getId());
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
    @Nullable
    public UserData getUser(@NotNull UUID uuid, @NotNull String level) {
        return this.rosePlugin.getManager(DataManager.class).getData(uuid, level);
    }

    /**
     * Get all data for a player across all levels
     *
     * @param uuid The uuid of the player
     * @return A list of all the player's level data
     */
    @Nullable
    public Map<String, UserData> getUser(@NotNull UUID uuid) {
        return this.rosePlugin.getManager(DataManager.class).getData(uuid);
    }

    /**
     * Save a player's level data
     *
     * @param userData The player's level data
     */
    public void saveUserData(@NotNull UserData userData) {
        this.rosePlugin.getManager(DataManager.class).saveUser(userData);
    }

    /**
     * Delete a player's level data
     *
     * @param userData The player's level data
     */
    public void deleteUserData(@NotNull UserData userData) {
        this.rosePlugin.getManager(DataManager.class).deleteUser(userData);
    }

    /**
     * Deletes all a player's level data
     *
     * @param uuid The uuid of the player
     */
    public void deleteUser(@NotNull UUID uuid) {
        this.rosePlugin.getManager(DataManager.class).deleteUser(uuid);
    }

    /**
     * Cache a player's level data in memory for faster access
     *
     * @param userData The player's level data
     */
    public void cacheUser(@NotNull UserData userData) {
        this.rosePlugin.getManager(DataManager.class).cacheUser(userData);
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
        var region = level.getRegionAt(location);
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
    public Level getLevel(@NotNull Location location) {
        return this.levelData.values().stream()
                .filter(level -> level.getRegionAt(location) != null)
                .filter(obj -> true)
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
    public void calculateLevel(@NotNull Level level) {
        // Get all players who have completed the level
        List<UserData> levelData = this.rosePlugin.getManager(DataManager.class).getLevelData(level.getId());

        // Calculate average best time completions
        long averageBestTime = (long) levelData.stream()
                .filter(userData -> userData.getBestTime() != -1)
                .mapToLong(UserData::getBestTime)
                .average()
                .orElse(-1);

        level.setAverageTime(averageBestTime);

        // Calculate the top times for the level into Map<Integer, UserData>
        Map<Integer, UserData> topTimes = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            UserData userData = levelData.stream()
                    .filter(data -> data.getBestTime() != -1)
                    .min(Comparator.comparingLong(UserData::getBestTime))
                    .orElse(null);

            if (userData != null) {
                topTimes.put(i, userData);
                levelData.remove(userData);
            }
        }

        level.setTopUsers(topTimes);
    }

    /**
     * Set a player to start a level
     *
     * @param player The player
     * @param level  The level
     */
    public @Nullable RunSession startRun(@NotNull Player player, @NotNull Level level) {
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        // Check if the player is already playing a level, if so cancel the run
        if (this.isPlaying(player.getUniqueId())) {
            this.rosePlugin.getLogger().info("Player " + player.getName() + " is already playing a level, cancelling run");
            this.cancelRun(player, false);
        }

        // Check if the player is editing a level, if the player is viewing the level, who cares
        if (this.levelEditors.containsKey(player.getUniqueId()) && this.levelEditors.get(player.getUniqueId()).getType() != EditType.VIEWING)
            return null;

        //Don't allow players to start a level if they are in creative mode or spectator mode
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
            return null;
        }

        // Check if the player is in the level's finish region
        if (this.isFinished(level, player.getLocation()) && this.isPlaying(player.getUniqueId())) {
            this.rosePlugin.getLogger().warning("Player " + player.getName() + " is already at the finish region of level " + level.getId());
            this.finishRun(player);
            return null;
        }

        // Create a new parkour session
        RunSession parkourRun = new RunSession(player.getUniqueId(), level);

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
        var locale = this.rosePlugin.getManager(LocaleManager.class);
        var finishTime = System.currentTimeMillis(); // We're declaring it here, so we can get the most accurate time
        var level = this.getPlayingLevel(player.getUniqueId());
        // Player is not playing a level
        if (level == null) {
            this.rosePlugin.getLogger().info("Player " + player.getName() + " is not playing a level (finishRun)");
            return;
        }

        // Check if the player is editing a level, if the player is viewing the level, who cares
        if (this.levelEditors.containsKey(player.getUniqueId()) && this.levelEditors.get(player.getUniqueId()).getType() != EditType.VIEWING)
            return;

        //Don't allow players to start a level if they are in creative mode or spectator mode
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        var parkourRun = this.activeRunners.get(player.getUniqueId());
        parkourRun.setEndTime(finishTime);

        this.activeRunners.remove(player.getUniqueId()); // Remove the player from the active runners

        var data = this.getUser(player.getUniqueId(), level.getId());
        if (data == null)
            data = new UserData(player.getUniqueId(), level.getId());

        if (level.getTeleport() != null)
            this.teleport(player, level.getTeleport());

        // Check if the player has finished the level before the cooldown ends
        if (System.currentTimeMillis() - data.getLastCompletion() > level.getCooldown())
            level.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HexUtils.colorify(PAPI.apply(player, command))));

        // Update the player's data
        data.setLastTime(parkourRun.getEndTime() - parkourRun.getStartTime());
        data.setLastCompletion(parkourRun.getEndTime());
        data.setAttempts(data.getAttempts() + 1);
        data.setCompleted(data.getCompleted() + 1);
        data.getTotalTimes().add(data.getLastTime());

        if (data.getBestTime() < data.getLastTime()) {
            data.setBestTime(data.getLastTime());
            this.saveUserData(data);
            this.rosePlugin.getLogger().info("Player " + player.getName() + " has completed level " + level.getId() + " with a new best time of " + data.getLastTime());
        } else {
            this.cacheUser(data);
            this.rosePlugin.getLogger().info("Player " + player.getName() + " has completed level " + level.getId() + " with a time of " + data.getLastTime());
        }

        // Update the level's data
        level.setTimesCompleted(level.getTimesCompleted() + 1);

        this.saveLevel(level);

        final var plc = StringPlaceholders.builder()
                .addPlaceholder("level", level.getId())
                .addPlaceholder("time", PluginUtils.parseFromTime(parkourRun.getEndTime() - parkourRun.getStartTime()))
                .addPlaceholder("completion", data.getCompleted())
                .addPlaceholder("attempts", data.getAttempts())
                .addPlaceholder("best", PluginUtils.parseFromTime(data.getBestTime()))
                .build();

//
//        // Check if the player has a new best time
//        if (data.getBestTime() == data.getLastTime()) {
//            // Send new best time message here
//            this.rosePlugin.getLogger().info("Player " + player.getName() + " has a new best time of " + data.getBestTime() + " in level " + level.getId());
//        } else {
//            // Send finish message here
//            this.rosePlugin.getLogger().info("Player " + player.getName() + " has finished level " + level.getId() + " in " + data.getLastTime() + "ms");
//        }

    }

    /**
     * Cancel a player's parkour run, this will not save the player's data
     *
     * @param player The player
     */
    public void cancelRun(@NotNull Player player, boolean teleport) {

        // Player is not playing a level
        var run = this.activeRunners.get(player.getUniqueId());
        if (run == null) {
            return;
        }

        // Send cancel message here
        this.rosePlugin.getLogger().info("Player " + player.getName() + " has cancelled their run in level " + run.getLevel().getId());
        this.activeRunners.remove(player.getUniqueId());

        if (run.getLevel().getTeleport() != null && teleport)
            this.teleport(player, run.getLevel().getTeleport());
    }

    /**
     * Fail a player's parkour run, this will cache a player's data with a failed attempt
     *
     * @param player   The player
     * @param teleport Whether to teleport the player to the level's spawn
     */
    public void failRun(@NotNull Player player, boolean teleport) {
        // Player is not playing a level
        var level = this.getPlayingLevel(player.getUniqueId());
        var session = this.getRun(player.getUniqueId());
        // Player is not playing a level
        if (level == null) {
            return;
        }

        // Teleport the player back to the checkpoint if they have one
        var checkpoint = session.getCheckpoint();
        if (level.getCheckpoints().size() > 0 && session.getCheckpoint() <= 0) {
            var checkpointLocation = level.getCheckpoints().get(0);
            if (checkpointLocation != null) {
                this.teleport(player, checkpointLocation);
                return;
            }
        }

        // Update the player's data
        var data = this.getUser(player.getUniqueId(), level.getId());
        if (data == null)
            data = new UserData(player.getUniqueId(), level.getId());

        data.setAttempts(data.getAttempts() + 1);

        // Send fail message here
        this.rosePlugin.getLogger().info("Player " + player.getName() + " has failed their run in level " + level.getId());
        this.activeRunners.remove(player.getUniqueId());
        if (level.getTeleport() != null && teleport)
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
            player.teleportAsync(location);
            return;
        }

        player.teleport(location);
    }

    /**
     * Get a player's parkour run
     *
     * @param player The player
     * @return The player's parkour run
     */
    public RunSession getRun(@NotNull Player player) {
        return this.activeRunners.get(player.getUniqueId());
    }

    /**
     * Get a player's parkour run
     *
     * @param uuid The player's UUID
     * @return The player's parkour run
     */
    public RunSession getRun(@NotNull UUID uuid) {
        return this.activeRunners.get(uuid);
    }

    /**
     * Start editing a parkour level
     *
     * @param player The player
     * @param level  The level
     * @param type   The type of edit
     */
    public void startEditing(@NotNull Player player, @NotNull Level level, @NotNull EditType type) {
        if (this.levelEditors.containsKey(player.getUniqueId())) {
            this.saveEditSession(player);
        }

        this.levelEditors.put(player.getUniqueId(), new EditSession(level, type));
    }

    /**
     * Stop editing a player's parkour level
     *
     * @param player The player
     */
    public void stopEditing(@NotNull Player player) {
        var session = this.levelEditors.get(player.getUniqueId());
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
    public EditSession getEditSession(@NotNull Player player) {
        return this.levelEditors.get(player.getUniqueId());
    }

    /**
     * Get a player's edit session
     *
     * @param uuid The player's UUID
     * @return The player's edit session
     */
    public EditSession getEditSession(@NotNull UUID uuid) {
        return this.levelEditors.get(uuid);
    }

    /**
     * Save a player's current edit session to the level file
     *
     * @param player The player
     */
    public void saveEditSession(@NotNull Player player) {
        var session = this.levelEditors.get(player.getUniqueId());
        if (session == null)
            return;

        var sessionLevel = session.getLevel();
        var cacheLevel = this.getLevel(sessionLevel.getId());
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

    @Override
    public void disable() {
        this.levelData.clear();
        this.levelEditors.clear();
        this.activeRunners.clear();
    }

    public Map<String, Level> getLevelData() {
        return levelData;
    }

    public Map<UUID, EditSession> getLevelEditors() {
        return levelEditors;
    }

    public Map<UUID, RunSession> getActiveRunners() {
        return activeRunners;
    }

}
