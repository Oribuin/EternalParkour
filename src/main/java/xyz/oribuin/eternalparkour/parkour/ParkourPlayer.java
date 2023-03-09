package xyz.oribuin.eternalparkour.parkour;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkourPlayer {

    /**
     * The UUID of the player this object represents.
     */

    @NotNull
    private final UUID uuid;

    /**
     * The cached player object.
     */
    private Player cachedPlayer;

    /**
     * The user data of the player.
     */
    @NotNull
    private Map<String, UserData> userData;

    public ParkourPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.userData = new HashMap<>();
    }

    public ParkourPlayer(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.userData = new HashMap<>();
        this.cachedPlayer = player;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        if (this.cachedPlayer == null) {
            this.cachedPlayer = Bukkit.getPlayer(this.uuid);
        }

        return this.cachedPlayer;
    }

    public void clearCachedPlayer() {
        this.cachedPlayer = null;
    }

    /**
     * @param player Refresh the cached player object.
     */
    public void refresh(Player player) {
        this.cachedPlayer = player;
    }

    @NotNull
    public Map<String, UserData> getUserData() {
        return userData;
    }

    public void setUserData(@NotNull Map<String, UserData> userData) {
        this.userData = userData;
    }

}


