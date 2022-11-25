package xyz.oribuin.eternalparkour.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.DataManager;

public class PlayerListeners implements Listener {

    private final EternalParkour plugin;

    public PlayerListeners(EternalParkour plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Save the player's data when they leave
        this.plugin.getManager(DataManager.class).saveUser(event.getPlayer());
    }
}
