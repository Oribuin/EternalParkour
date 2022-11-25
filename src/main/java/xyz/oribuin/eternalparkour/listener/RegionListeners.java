package xyz.oribuin.eternalparkour.listener;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.event.PlayerEnterRegionEvent;
import xyz.oribuin.eternalparkour.event.PlayerExitRegionEvent;
import xyz.oribuin.eternalparkour.event.PlayerFinishLevelEvent;
import xyz.oribuin.eternalparkour.event.PlayerStartLevelEvent;
import xyz.oribuin.eternalparkour.event.PlayerSwitchRegionEvent;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;

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

        //Don't allow players to start a level if they are in creative mode or spectator mode
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Check if the player is editing a level, if the player is viewing the level, who cares
        if (this.manager.getLevelEditors().containsKey(player.getUniqueId()) && this.manager.getLevelEditors().get(player.getUniqueId()).getType() != EditType.VIEWING)
            return;

        // Get the current level the player is in.
        var level = this.manager.getLevel(event.getTo());
        var from = this.manager.getLevel(event.getFrom());
        if (level == null) {

            // If the player is not in a level, but was in one before, then they have exited the level.
            if (from != null) {
                if (!from.isEnabled()) // The level is disabled, so do not run the exit event.
                    return;

                if (this.manager.isPlaying(player.getUniqueId())) {
                    this.manager.failRun(player, true);
                    return;
                }

                var region = from.getRegionAt(event.getFrom());
                // Don't know why this would be null, but just in case.
                if (region == null)
                    return;

                this.manager.cancelRun(player, false);
                this.plugin.getServer().getPluginManager().callEvent(new PlayerExitRegionEvent(player, region, from));
                return;
            }

            return;
        }

        if (!level.isEnabled()) // The level is disabled, so do not run any events.
            return;

        // Get the player's current region.
        var region = level.getRegionAt(event.getTo());
        var fromRegion = level.getRegionAt(event.getFrom());

        // Signify that a player has entered a region.
        if (region != null && fromRegion == null) {
            this.plugin.getServer().getPluginManager().callEvent(new PlayerEnterRegionEvent(player, region, level));
        }

        // Signify that a player has left a region.
        if (region == null && fromRegion != null) {
            if (this.manager.isPlaying(player.getUniqueId())) {
                this.manager.failRun(player, true);
                return;
            }

            this.manager.cancelRun(player, false);
            this.plugin.getServer().getPluginManager().callEvent(new PlayerExitRegionEvent(player, fromRegion, level));
        }

        // Signify that a player has switched regions. (This is not the same as entering a region)
        if (region != null && fromRegion != null && !region.equals(fromRegion)) {

            // Make sure the player is not entering just a different region in the same level,
            // we're trying to pretend like they're the same region
            if (!level.isParkourRegion(region) && !level.isParkourRegion(fromRegion)) {
                this.plugin.getServer().getPluginManager().callEvent(new PlayerSwitchRegionEvent(player, region, fromRegion));
            }
        }

        var session = this.manager.getRunSession(player);
        if (fromRegion == null || region == null) // The player has switched regions.
            return;

        if (level.getCheckpoints().size() > 0) {
            var checkpoint = level.getCheckpoint(player.getLocation());
            if (this.manager.isPlaying(player.getUniqueId()) && checkpoint != null ) {

                // Don't change the checkpoint if it was already hit
                if (session.getCheckpoint() != null && checkpoint.getKey() <= session.getCheckpoint().getKey()) {
                    return;
                }

                player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
                session.setCheckpoint(checkpoint);
                this.manager.saveRunSession(session);
                return;
            }
        }

        // Check if the player is going from parkour region -> finish region.
        if (level.isParkourRegion(fromRegion) && level.isFinishRegion(region) && session != null) {
            this.manager.finishRun(player);
            this.plugin.getServer().getPluginManager().callEvent(new PlayerFinishLevelEvent(player, level, session));
            return;
        }

        // Check if the player went from a start region to a parkour region.
        if (level.isStartRegion(fromRegion) && level.isParkourRegion(region) && session == null) {
            session = this.manager.startRun(player, level);
            if (session != null) {
                this.plugin.getServer().getPluginManager().callEvent(new PlayerStartLevelEvent(player, level, session));
            }
        }

        // Check if a player went from a parkour region to a start region.
        if (level.isParkourRegion(fromRegion) && level.isStartRegion(region) && session != null) {
            this.manager.cancelRun(player, false);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        // We cancel the run if a player teleports.
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            return;
        }

        if (this.manager.isPlaying(event.getPlayer().getUniqueId())) {
            this.manager.cancelRun(event.getPlayer(), false);
        }
    }


}
