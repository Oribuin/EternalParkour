package xyz.oribuin.eternalparkour.parkour;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserData {

    private final @NotNull String level; // Id of the level
    private final @NotNull UUID player; // UUID of the player
    private int completed; // How many times the user has completed the level
    private int attempts; // How many times the user has attempted the level
    private long bestTime; // The best time the user has completed the level in
    private long lastTime; // The last time the user completed the level in
    private long lastCompletion; // The last time the user completed the level
    private boolean hidingPlayers; // Whether to hide other players in the level
    private List<Long> totalTimes; // List of all times the user has completed the level in

    public UserData(@NotNull UUID player, @NotNull String level) {
        this.level = level;
        this.player = player;
        this.completed = 0;
        this.attempts = 0;
        this.bestTime = 0L;
        this.lastTime = 0L;
        this.lastCompletion = 0L;
        this.hidingPlayers = false;
        this.totalTimes = new ArrayList<>();
    }

    public @NotNull String getLevel() {
        return level;
    }

    public @NotNull UUID getPlayer() {
        return player;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
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

    public boolean isHidingPlayers() {
        return hidingPlayers;
    }

    public void setHidingPlayers(boolean hidingPlayers) {
        this.hidingPlayers = hidingPlayers;
    }

    public List<Long> getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(List<Long> totalTimes) {
        this.totalTimes = totalTimes;
    }


}
