package xyz.oribuin.eternalparkour.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public final class PluginUtils {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(Setting.LEADERBOARD_DATE_FORMAT.getString());
    private static final SimpleDateFormat SCORE_FORMATTER = new SimpleDateFormat("mm:ss.SSS");

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
        return Duration.parse(time).toMillis();
    }

    /**
     * Parse a time in milliseconds into a string such as 1h 30m 10s
     *
     * @param time The time to parse
     * @return The parsed time
     */
    public static String parseFromTime(long time) {
        return Duration.ofMillis(time).toString();
    }


    /**
     * Parse a time in milliseconds into a string such as 1:20:30
     *
     * @param time The time to parse
     * @return The parsed time
     */
    public static String parseToScore(long time) {
        return SCORE_FORMATTER.format(new Date(time));
    }

    /**
     * Parse a time in milliseconds into a date string
     *
     * @param time The time to parse
     * @return The parsed time
     */
    public static String parseToDate(long time) {
        return DATE_FORMATTER.format(new Date(time));
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

    public static @Nullable Location asBlockLoc(@Nullable Location location) {
        if (location == null)
            return null;

        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static @Nullable Location asCenterLoc(@Nullable Location location) {
        if (location == null)
            return null;

        return new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
    }

    public static @Nullable Integer asInt(@Nullable String string) {
        if (string == null)
            return null;

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static @NotNull Color getColor(String hex) {
        try {
            java.awt.Color javaColor = java.awt.Color.decode(hex);
            return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
        } catch (IllegalArgumentException e) {
            return Color.BLACK;
        }
    }

}
