package xyz.oribuin.eternalparkour.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalparkour.EternalParkour;

public class PAPI extends PlaceholderExpansion {

    private final EternalParkour plugin;
    private static boolean enabled;

    public PAPI(EternalParkour plugin) {
        this.plugin = plugin;
        enabled = this.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (!enabled || !this.register())
            this.plugin.getLogger().warning("Failed to register PlaceholderAPI expansion.");
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

}
