package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Level {

    private @NotNull String id; // The ID of the level
    private @Nullable Location spawn; // The spawn location of the level
    private @Nullable Region startRegion; // The spawn location of the level
    private @NotNull List<Region> levelRegions; // List of regions for the level
    private @Nullable Region finishRegion; // Region that the player must stand in to finish the level
    private @NotNull Map<Integer, Location> checkpoints; // Map of checkpoints for the level
    private @NotNull List<String> commands; // Commands to run when the player finishes the level
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
        this.spawn = null;
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
        for (var region : this.levelRegions) {
            if (region.isInside(location))
                return region;
        }

        return null;
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

    public @NotNull List<String> getCommands() {
        return commands;
    }

    public void setCommands(@NotNull List<String> commands) {
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

    public @Nullable Location getSpawn() {
        return spawn;
    }

    public void setSpawn(@Nullable Location spawn) {
        this.spawn = spawn;
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
