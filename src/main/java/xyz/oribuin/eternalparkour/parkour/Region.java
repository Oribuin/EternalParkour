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
        var x = location.getX();
        var y = location.getY();
        var z = location.getZ();

        // Check if the location is inside the region
        return pos1.getX() <= x && pos1.getZ() <= z && pos2.getX() >= x && pos2.getZ() >= z && pos1.getY() <= y && pos2.getY() >= y;
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
