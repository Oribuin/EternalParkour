package xyz.oribuin.eternalparkour.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.event.PlayerEnterRegionEvent;
import xyz.oribuin.eternalparkour.event.PlayerFinishLevelEvent;
import xyz.oribuin.eternalparkour.event.PlayerStartLevelEvent;
import xyz.oribuin.eternalparkour.event.PlayerSwitchRegionEvent;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.util.PluginUtils;

/**
 * This is where we handle all the events for deciding when a player is entering or exiting a region.
 */
public class RegionListeners implements Listener {

    private final EternalParkour plugin;
    private final ParkourManager manager;

    public RegionListeners(EternalParkour plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager(ParkourManager.class);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Do not run if a player only moved their head.
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        var player = event.getPlayer();
//        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
//            return;

        // Check if the player is editing the level.
        if (this.manager.getLevelEditors().containsKey(player.getUniqueId()))
            return;

        // Get the current level the player is in.
        var level = this.manager.getLevel(event.getTo());
        if (level == null)
            return;


        // Get the player's current region.
        var region = level.getRegionAt(event.getTo());
        var fromRegion = level.getRegionAt(event.getFrom());

        // Signify that a player has entered a region.
        if (region != null && fromRegion == null) {
            this.plugin.getLogger().info(String.format("%s entered region %s", player.getName(), level.getId()));
            this.plugin.getServer().getPluginManager().callEvent(new PlayerEnterRegionEvent(player, region, level));
        }

        // Signify that a player has switched regions. (This is not the same as entering a region)
        if (region != null && fromRegion != null && !region.equals(fromRegion)) {

            // Make sure the player is not entering just a different region in the same level,
            // we're trying to pretend like they're the same region
            if (!level.isParkourRegion(region) && !level.isParkourRegion(fromRegion)) {
                this.plugin.getLogger().info(String.format("%s switched regions", player.getName()));
                this.plugin.getServer().getPluginManager().callEvent(new PlayerSwitchRegionEvent(player, region, fromRegion));
            }
        }

        var session = this.manager.getRun(player);
        if (fromRegion == null || region == null) // The player has switched regions.
            return;

        // Check if the player is going from parkour region -> finish region.
        if (level.isParkourRegion(fromRegion) && level.isFinishRegion(region) && session != null) {
            this.plugin.getLogger().info(String.format("%s finished the level", event.getPlayer().getName()));
            this.plugin.getServer().getPluginManager().callEvent(new PlayerFinishLevelEvent(player, level, session));
            this.manager.finishRun(player);
        }

        // Check if the player went from a start region to a parkour region.
        if (level.isStartRegion(fromRegion) && level.isParkourRegion(region) && session != null) {
            this.plugin.getLogger().info(String.format("%s started the level", player.getName()));
            session = this.manager.startRun(player, level);
            if (session != null) {
                this.plugin.getServer().getPluginManager().callEvent(new PlayerStartLevelEvent(player, level, session));
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        // We cancel the run if a player teleports.
        if (this.manager.isPlaying(event.getPlayer().getUniqueId())) {
            this.plugin.getLogger().info(String.format("%s teleported, cancelling run", event.getPlayer().getName()));
            this.manager.cancelRun(event.getPlayer(), false);
        }
    }




}
