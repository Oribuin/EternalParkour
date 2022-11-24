package xyz.oribuin.eternalparkour.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;

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

        // Get the current level the player is in.
        var level = this.manager.getLevel(event.getTo());
        if (level == null)
            return;

        // Get the player's current region.
        var region = level.getRegionAt(event.getTo());
        var fromRegion = level.getRegionAt(event.getFrom());

    }



}
