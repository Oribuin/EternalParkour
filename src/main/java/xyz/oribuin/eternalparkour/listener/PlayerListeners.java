package xyz.oribuin.eternalparkour.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.DataManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;

public class PlayerListeners implements Listener {

    private final EternalParkour plugin;

    public PlayerListeners(EternalParkour plugin) {
        this.plugin = plugin;
    }

    // We're going to reset the player's current run if they log in while in one.
    // This is to prevent players from logging out in a level and then being stuck in it.
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ParkourManager manager = this.plugin.getManager(ParkourManager.class);
        Player player = event.getPlayer();
        Level level = manager.getLevel(player.getLocation());

        if (level != null && !manager.isPlaying(player.getUniqueId()) && level.getTeleport() != null) {
            manager.teleport(player, level.getTeleport());
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Save the player's data when they leave
        this.plugin.getManager(DataManager.class).saveUser(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        ParkourManager manager = this.plugin.getManager(ParkourManager.class);
        if (manager.isPlaying(player.getUniqueId())) {
            manager.cancelRun(player, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.isOnline()) // Player might not be online.
            return;

        ParkourManager manager = this.plugin.getManager(ParkourManager.class);
        if (manager.isPlaying(player.getUniqueId())) {
            manager.cancelRun(player, true);
        }
    }

}
