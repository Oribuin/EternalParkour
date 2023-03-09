package xyz.oribuin.eternalparkour.parkour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RunSession {

    private final @NotNull UUID player; // The user who is running the level
    private final @NotNull Level level; // The level the user is running
    private final long startTime; // The time the user started the level
    private long endTime; // The time the user finished the level
    private @Nullable Checkpoint checkpoint; // The last checkpoint the user reached
    private int attempts; // The number of attempts the user has made

    public RunSession(@NotNull UUID player, @NotNull Level level) {
        this.player = player;
        this.level = level;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0L;
        this.checkpoint = null;
        this.attempts = 0;
    }

    public @NotNull UUID getPlayer() {
        return player;
    }

    public @NotNull Level getLevel() {
        return level;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

}
