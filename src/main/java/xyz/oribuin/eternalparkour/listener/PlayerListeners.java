package xyz.oribuin.eternalparkour.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.DataManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;

public class PlayerListeners implements Listener {

    private final EternalParkour plugin;

    public PlayerListeners(EternalParkour plugin) {
        this.plugin = plugin;
    }

    // We're going to reset the player's current run if they log in while in one.
    // This is to prevent players from logging out in a level and then being stuck in it.
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var manager = this.plugin.getManager(ParkourManager.class);
        var player = event.getPlayer();
        var level = manager.getLevel(player.getLocation());

        if (level != null && !manager.isPlaying(player.getUniqueId()) && level.getTeleport() != null) {
            manager.teleport(player, level.getTeleport());
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Save the player's data when they leave
        this.plugin.getManager(DataManager.class).saveUser(event.getPlayer());
    }
}
