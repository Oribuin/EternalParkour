package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.action.Action;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Level {

    private @NotNull String id; // The ID of the level
    private @Nullable Location teleport; // The fail teleport location
    private @Nullable Region startRegion; // The start region of the level
    private @NotNull List<Region> levelRegions; // List of regions for the level
    private @Nullable Region finishRegion; // Region that the player must stand in to finish the level
    private @NotNull Map<Integer, Location> checkpoints; // Map of checkpoints for the level
    private @NotNull List<Action> commands; // Commands to run when the player finishes the level
    private long averageTime; // Average time to complete the level
    private int timesCompleted; // Times the level has been completed
    private @NotNull Map<Integer, UserData> topUsers; // Top users who have completed the level
    private long cooldown; // The cooldown for the level rewards
    private boolean enabled; // Whether the level is enabled or not

    public Level(@NotNull String id) {
        this.id = id;
        this.levelRegions = new ArrayList<>();
        this.finishRegion = null;
        this.checkpoints = new LinkedHashMap<>();
        this.commands = new ArrayList<>();
        this.averageTime = 0;
        this.timesCompleted = 0;
        this.topUsers = new LinkedHashMap<>();
        this.teleport = null;
        this.cooldown = 0L;
        this.enabled = true;
    }

    /**
     * Get the region at a location in the level.
     *
     * @param location The location to check.
     * @return The region at the location.
     */
    public @Nullable Region getRegionAt(@NotNull Location location) {
        var allRegions = new ArrayList<>(this.levelRegions);
        if (this.startRegion != null)
            allRegions.add(this.startRegion);

        if (this.finishRegion != null)
            allRegions.add(this.finishRegion);

        for (var region : allRegions) {
            if (region.isInside(location))
                return region;
        }

        return null;
    }

    /**
     * Check if the location is inside the start region
     *
     * @param region The region to check
     * @return Whether the location is inside the start region
     */
    public boolean isStartRegion(@NotNull Region region) {
        return this.startRegion != null && this.startRegion.equals(region);
    }

    /**
     * Check if the location is inside the parkour regions
     *
     * @param region The region to check
     * @return True if the location is inside the regions
     */
    public boolean isParkourRegion(@NotNull Region region) {
        return this.levelRegions.contains(region);
    }

    /**
     * Check if the location is inside the finish region
     *
     * @param region The region to check
     * @return Whether the location is inside the finish region
     */
    public boolean isFinishRegion(@NotNull Region region) {
        return this.finishRegion != null && this.finishRegion.equals(region);
    }

    /**
     * Get the checkpoint at a location in the level.
     *
     * @param location The location to check.
     * @return The checkpoint id
     */
    public Map.Entry<Integer, Location> getCheckpoint(@NotNull Location location) {
        // check if location is inside a checkpoint
        for (var entry : this.checkpoints.entrySet()) {
            if (entry.getValue().distance(location) < 1)
                return entry;
        }

        return null;
    }


    public @NotNull String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public @Nullable Region getStartRegion() {
        return startRegion;
    }

    public void setStartRegion(@Nullable Region startRegion) {
        this.startRegion = startRegion;
    }

    public @NotNull List<Region> getLevelRegions() {
        return levelRegions;
    }

    public void setLevelRegions(@NotNull List<Region> levelRegions) {
        this.levelRegions = levelRegions;
    }

    public @Nullable Region getFinishRegion() {
        return finishRegion;
    }

    public void setFinishRegion(@Nullable Region finishRegion) {
        this.finishRegion = finishRegion;
    }

    public @NotNull Map<Integer, Location> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(@NotNull Map<Integer, Location> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public @NotNull List<Action> getCommands() {
        return commands;
    }

    public void setCommands(@NotNull List<Action> commands) {
        this.commands = commands;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }

    public void setTimesCompleted(int timesCompleted) {
        this.timesCompleted = timesCompleted;
    }

    public @NotNull Map<Integer, UserData> getTopUsers() {
        return topUsers;
    }

    public void setTopUsers(@NotNull Map<Integer, UserData> topUsers) {
        this.topUsers = topUsers;
    }

    public @Nullable Location getTeleport() {
        return teleport;
    }

    public void setTeleport(@Nullable Location teleport) {
        this.teleport = teleport;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}