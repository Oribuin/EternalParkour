package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RunSession {

    private final @NotNull UUID player;
    private final @NotNull Level level;
    private final long startTime;
    private long endTime;
    private @Nullable Map.Entry<Integer, Location> checkpoint;

    public RunSession(@NotNull UUID player, @NotNull Level level) {
        this.player = player;
        this.level = level;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0L;
        this.checkpoint = null;
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

    public Map.@Nullable Entry<Integer, Location> getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Map.@Nullable Entry<Integer, Location> checkpoint) {
        this.checkpoint = checkpoint;
    }

}
