package xyz.oribuin.eternalparkour.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.parkour.Region;

public class PlayerSwitchRegionEvent extends PlayerEvent {

    private static final HandlerList list = new HandlerList();
    private final @NotNull Region region;
    private final @NotNull Region previousRegion;

    public PlayerSwitchRegionEvent(Player player, @NotNull Region region, @NotNull Region previousRegion) {
        super(player);
        this.region = region;
        this.previousRegion = previousRegion;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return list;
    }

    public static @NotNull HandlerList getHandlerList() {
        return list;
    }

    public @NotNull Region getRegion() {
        return region;
    }

    public @NotNull Region getPreviousRegion() {
        return previousRegion;
    }

}
