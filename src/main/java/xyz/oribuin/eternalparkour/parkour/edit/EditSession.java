package xyz.oribuin.eternalparkour.parkour.edit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.Region;

public class EditSession {

    private @NotNull Level level;
    private @NotNull EditType type;
    private @Nullable Region region;

    public EditSession(@NotNull Level level, @NotNull EditType type) {
        this.level = level;
        this.type = type;
        this.region = null;
    }

    public @NotNull Level getLevel() {
        return level;
    }

    public void setLevel(@NotNull Level level) {
        this.level = level;
    }

    public @NotNull EditType getType() {
        return type;
    }

    public void setType(@NotNull EditType type) {
        this.type = type;
    }

    public @Nullable Region getRegion() {
        return region;
    }

    public void setRegion(@Nullable Region region) {
        this.region = region;
    }
}
