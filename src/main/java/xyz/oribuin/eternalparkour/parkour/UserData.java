package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserData {

    private final @NotNull String level; // Id of the level
    private final @NotNull UUID player; // UUID of the player
    private @Nullable String name; // Name of the player, this is for caching purposes to prevent Bukkit#getOfflinePlayer from being called every time
    private int completions; // How many times the user has completed the level
    private int attempts; // How many times the user has attempted the level
    private long bestTime; // The best time the user has completed the level in
    private long bestTimeAchieved; // The time the user achieved their best time
    private long lastTime; // The time it took the user to complete the level last
    private long lastCompletion; // The last time the user completed the level
    private List<Long> totalTimes; // List of all times the user has completed the level in

    public UserData(@NotNull UUID player, @NotNull String level) {
        this.level = level;
        this.player = player;
        this.name = null;
        this.completions = 0;
        this.attempts = 0;
        this.bestTime = 0L;
        this.bestTimeAchieved = 0L;
        this.lastTime = 0L;
        this.lastCompletion = 0L;
        this.totalTimes = new ArrayList<>();
    }

    /**
     * Check if the user's name is up-to-date.
     *
     * @param player The player to check.
     * @return Whether the name was updated or not.
     */
    public boolean checkUser(@NotNull OfflinePlayer player) {
        if (player.getUniqueId().equals(this.player) && !Objects.equals(player.getName(), this.name)) {
            this.name = player.getName();
            return true;
        }

        return false;
    }

    public @NotNull String getLevel() {
        return level;
    }

    public @NotNull UUID getPlayer() {
        return player;
    }

    public @Nullable String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public int getCompletions() {
        return completions;
    }

    public void setCompletions(int completions) {
        this.completions = completions;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }

    public long getBestTimeAchieved() {
        return bestTimeAchieved;
    }

    public void setBestTimeAchieved(long bestTimeAchieved) {
        this.bestTimeAchieved = bestTimeAchieved;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getLastCompletion() {
        return lastCompletion;
    }

    public void setLastCompletion(long lastCompletion) {
        this.lastCompletion = lastCompletion;
    }

    public List<Long> getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(List<Long> totalTimes) {
        this.totalTimes = totalTimes;
    }


}
