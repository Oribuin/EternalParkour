package xyz.oribuin.eternalparkour.util;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class PluginUtils {

    public PluginUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean usingPaper() {
        try {
            Class.forName("com.destroystokyo.paper.util.VersionFetcher");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Parse a string such as 1h 30m 10s into a long
     *
     * @param time The time to parse
     * @return The parsed time
     */
    public static long parseToTime(String time) {
        String[] parts = time.split(" ");
        long totalSeconds = 0;

        for (String part : parts) {

            // get the last character
            char lastChar = part.charAt(part.length() - 1);
            String num = part.substring(0, part.length() - 1);
            if (num.isEmpty())
                continue;

            int amount;
            try {
                amount = Integer.parseInt(num);
            } catch (NumberFormatException e) {
                continue;
            }

            switch (lastChar) {
                case 'w' -> totalSeconds += amount * 604800L;
                case 'd' -> totalSeconds += amount * 86400L;
                case 'h' -> totalSeconds += amount * 3600L;
                case 'm' -> totalSeconds += amount * 60L;
                case 's' -> totalSeconds += amount;
            }
        }

        return totalSeconds * 1000;
    }

    /**
     * Parse a time in milliseconds into a string such as 1h 30m 10s
     *
     * @param time The time to parse
     * @return The parsed time
     */
    public static String parseFromTime(long time) {
        // Do the reverse of the above method
        long totalSeconds = time / 1000;

        if (totalSeconds <= 0)
            return "";

        long weeks = totalSeconds / 604800;
        totalSeconds %= 604800;
        long days = totalSeconds / 86400;
        totalSeconds %= 86400;
        long hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        final StringBuilder builder = new StringBuilder();

        if (weeks > 0)
            builder.append(weeks).append("w ");

        if (days > 0)
            builder.append(days).append("d ");

        if (hours > 0)
            builder.append(hours).append("h ");

        if (minutes > 0)
            builder.append(minutes).append("m ");

        if (seconds > 0)
            builder.append(seconds).append("s");

        return builder.toString();
    }

    /**
     * Format a location into a string
     *
     * @param location The location to format
     * @return The formatted location
     */
    public static String formatLocation(Location location) {
        if (location == null)
            return "null";

        return String.format("%s, %s, %s", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get all the particle locations to spawn a hollow cube in between point A & Point B
     *
     * @param corner1          The first corner.
     * @param corner2          The second corner
     * @param particleDistance The distance between particles
     * @return The list of particle locations
     * @author Esophose
     * @ <a href="https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleOutline.java#L86">...</a>
     */
    public static List<Location> getCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        var world = corner1.getWorld();
        var minX = Math.min(corner1.getX(), corner2.getX());
        var minY = Math.min(corner1.getY(), corner2.getY());
        var minZ = Math.min(corner1.getZ(), corner2.getZ());
        var maxX = Math.max(corner1.getX(), corner2.getX());
        var maxY = Math.max(corner1.getY(), corner2.getY());
        var maxZ = Math.max(corner1.getZ(), corner2.getZ());

        for (double x = minX; x <= maxX; x += particleDistance) {
            result.add(new Location(world, x, minY, minZ));
            result.add(new Location(world, x, maxY, minZ));
            result.add(new Location(world, x, minY, maxZ));
            result.add(new Location(world, x, maxY, maxZ));
        }

        for (double y = minY; y <= maxY; y += particleDistance) {
            result.add(new Location(world, minX, y, minZ));
            result.add(new Location(world, maxX, y, minZ));
            result.add(new Location(world, minX, y, maxZ));
            result.add(new Location(world, maxX, y, maxZ));
        }

        for (double z = minZ; z <= maxZ; z += particleDistance) {
            result.add(new Location(world, minX, minY, z));
            result.add(new Location(world, maxX, minY, z));
            result.add(new Location(world, minX, maxY, z));
            result.add(new Location(world, maxX, maxY, z));
        }

        return result;
    }

    public static @Nullable Location asBlockLoc(@Nullable Location location) {
        if (location == null)
            return null;

        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }



}
