package xyz.oribuin.eternalparkour.parkour;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RunSession {

    private final @NotNull UUID player;
    private final @NotNull Level level;
    private final long startTime;
    private long endTime;
    private int checkpoint;

    public RunSession(@NotNull UUID player, @NotNull Level level) {
        this.player = player;
        this.level = level;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0L;
        this.checkpoint = 0;
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

    public int getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
    }

}
