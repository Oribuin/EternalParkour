package xyz.oribuin.eternalparkour.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.RunSession;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// TODO Clean up this class
public class PAPI extends PlaceholderExpansion {

    private final EternalParkour plugin;
    private final ParkourManager manager;
    private static boolean enabled;
    // okay this is gonna be hella cringe
    private final Map<Level, Map<String, Function<OfflinePlayer, String>>> levelPlaceholders = new HashMap<>();


    public PAPI(EternalParkour plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(ParkourManager.class);
        enabled = this.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (!enabled || !this.register())
            this.plugin.getLogger().warning("Failed to register PlaceholderAPI expansion.");
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        // Adding the placeholders that require no arguments
        boolean isPlaying = this.manager.isPlaying(player.getUniqueId());
        if (params.equalsIgnoreCase("level")) {
            return isPlaying ? manager.getRunSession(player.getUniqueId()).getLevel().getId() : "N/A";
        }

        if (params.equalsIgnoreCase("checkpoint")) {
            return isPlaying ? String.valueOf(manager.getRunSession(player.getUniqueId()).getCheckpoint()) : "N/A";
        }

        if (params.equalsIgnoreCase("max_checkpoints")) {
            return isPlaying ? String.valueOf(manager.getRunSession(player.getUniqueId()).getLevel().getCheckpoints().size()) : "N/A";
        }

        // All of these placeholders require a level argument
        String[] args = params.split("_");
        if (args.length < 2)
            return null;

        // Get the level from the second arg
        Level level = this.manager.getLevel(args[1]);
        if (level == null)
            return null;

        if (this.levelPlaceholders.get(level) == null)
            this.levelPlaceholders.put(level, this.getPlaceholders(level));

        // Alternative placeholders that require multiple args
        if (args[0].equalsIgnoreCase("get")) {
            if (args.length < 3)
                return "Invalid Parameters";

            // get number from args 2
            Integer number = PluginUtils.asInt(args[2]);
            if (number == null)
                return "N/A";

            UserData scoreData = level.getLeaderboardData(number);
            if (scoreData == null)
                return "N/A";

            // get the text from args 3
            String text = args[3];
            return switch (text.toLowerCase()) {
                case "name" -> scoreData.getName() == null ? "N/A" : scoreData.getName();
                case "time" -> PluginUtils.parseToScore(scoreData.getBestTime());
                case "time-raw" -> String.valueOf(scoreData.getBestTime());
                case "attempts" -> String.valueOf(scoreData.getAttempts());
                default -> "N/A";
            };
        }

        Function<OfflinePlayer, String> function = this.levelPlaceholders.get(level).get(args[0]);
        if (function == null)
            return null;

        return function.apply(player);
    }

    private Map<String, Function<OfflinePlayer, String>> getPlaceholders(@NotNull Level level) {
        return new HashMap<>() {{

            // The general user data
            // Best time formatted
            this.put("best_time", player -> PluginUtils.parseToScore(getData(player, level).getBestTime()));
            // Best time in milliseconds
            this.put("best_time_raw", player -> String.valueOf(getData(player, level).getBestTime()));
            // The amount of times the user has attempted the level
            this.put("attempts", player -> String.valueOf(getData(player, level).getAttempts()));
            // How many times the user has completed the level
            this.put("completions", player -> String.valueOf(getData(player, level).getCompletions()));
            // The time it took the user to complete the level last
            this.put("last_time", player -> PluginUtils.parseToScore(getData(player, level).getLastTime()));
            this.put("last_time_raw", player -> String.valueOf(getData(player, level).getLastTime()));
            // Reward Cooldown
            this.put("cooldown", player -> PluginUtils.parseFromTime(getData(player, level).getLastCompletion() - System.currentTimeMillis()));
            this.put("cooldown_raw", player -> String.valueOf(getData(player, level).getLastCompletion() - System.currentTimeMillis()));
            // best time achieved date
            this.put("best_achieved", player -> PluginUtils.parseToDate(getData(player, level).getBestTimeAchieved()));
            this.put("best_achieved-raw", player -> String.valueOf(getData(player, level).getBestTimeAchieved()));

            // Global level data
            this.put("average_time", player -> PluginUtils.parseFromTime(level.getAverageTime()));
            this.put("average_time_raw", player -> String.valueOf(level.getAverageTime()));
            this.put("max_checkpoints", player -> String.valueOf(level.getCheckpoints().size()));

            // Leaderboard data
            this.put("position", player -> String.valueOf(level.getLeaderboardPosition(player.getUniqueId())));

        }};
    }

    /**
     * Get the level data for the player.
     *
     * @param player The player
     * @param level  The level
     * @return The level data
     */
    @NotNull
    public UserData getData(OfflinePlayer player, Level level) {
        return this.manager.getUser(player.getUniqueId(), level.getId());
    }

    /**
     * Get the run session for the player.
     *
     * @param player The player
     * @return The run session
     */
    public RunSession getSession(OfflinePlayer player) {
        return this.manager.getRunSession(player.getUniqueId());
    }

    /**
     * Apply PlaceholderAPI Placeholders to a string
     *
     * @param player The player to apply the placeholders to
     * @param text   The text to apply the placeholders to
     * @return The text with the placeholders applied
     */
    public static String apply(@Nullable Player player, String text) {
        if (!enabled)
            return text;

        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }
}
