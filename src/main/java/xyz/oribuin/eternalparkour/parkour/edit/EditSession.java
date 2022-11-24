package xyz.oribuin.eternalparkour.parkour.edit;

import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.parkour.Level;

public class EditSession {

    private @NotNull Level level;
    private @NotNull EditType type;

    public EditSession(@NotNull Level level, @NotNull EditType type) {
        this.level = level;
        this.type = type;
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

}
