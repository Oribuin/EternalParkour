package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class Checkpoint {

    private final int id; // The ID of the checkpoint
    private @Nullable Location teleport; // The fail teleport location
    private @Nullable Region region; // The region of the checkpoint

    public Checkpoint(int id) {
        this.id = id;
        this.teleport = null;
        this.region = null;
    }

    /**
     * Get the ID of the checkpoint.
     *
     * @return The ID of the checkpoint.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the fail teleport location of the checkpoint.
     *
     * @return The fail teleport location of the checkpoint.
     */
    public @Nullable Location getTeleport() {
        return this.teleport;
    }

    /**
     * Set the fail teleport location of the checkpoint.
     *
     * @param teleport The fail teleport location of the checkpoint.
     */
    public void setTeleport(@Nullable Location teleport) {
        this.teleport = teleport;
    }

    /**
     * Get the region of the checkpoint.
     *
     * @return The region of the checkpoint.
     */
    public @Nullable Region getRegion() {
        return this.region;
    }

    /**
     * Set the region of the checkpoint.
     *
     * @param region The region of the checkpoint.
     */
    public void setRegion(@Nullable Region region) {
        this.region = region;
    }

}
