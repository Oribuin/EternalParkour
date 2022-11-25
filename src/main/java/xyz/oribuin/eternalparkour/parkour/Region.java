package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class Region {

    private @Nullable Location pos1; // First position of the region
    private @Nullable Location pos2; // Second position of the region

    public Region(@Nullable Location pos1, @Nullable Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Region() {
        this(null, null);
    }

    /**
     * Check if location is in the region
     *
     * @param location The location to check
     * @return Whether the location is in the region
     */
    public boolean isInside(Location location) {
        // Check if the position is null
        if (pos1 == null || pos2 == null)
            return false;

        // Check if the location is inside the world of the region
        if (location.getWorld() != pos1.getWorld() || location.getWorld() != pos2.getWorld())
            return false;

        // Declare location x, y, z
        var x = location.getBlockX();
        var y = location.getBlockY();
        var z = location.getBlockZ();

        // Check if the location is inside the region
        return x >= Math.min(pos1.getX(), pos2.getX()) && x <= Math.max(pos1.getX(), pos2.getX()) &&
                y >= Math.min(pos1.getY(), pos2.getY()) && y <= Math.max(pos1.getY(), pos2.getY()) &&
                z >= Math.min(pos1.getZ(), pos2.getZ()) && z <= Math.max(pos1.getZ(), pos2.getZ());
    }

    public @Nullable Location getPos1() {
        return pos1;
    }

    public void setPos1(@Nullable Location pos1) {
        this.pos1 = pos1;
    }

    public @Nullable Location getPos2() {
        return pos2;
    }

    public void setPos2(@Nullable Location pos2) {
        this.pos2 = pos2;
    }

}
