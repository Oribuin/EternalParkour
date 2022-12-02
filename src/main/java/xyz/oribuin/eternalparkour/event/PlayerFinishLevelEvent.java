package xyz.oribuin.eternalparkour.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.RunSession;

public class PlayerFinishLevelEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList list = new HandlerList();

    private final @NotNull Level level;
    private final @NotNull RunSession session;
    private boolean cancelled;

    public PlayerFinishLevelEvent(Player player, @NotNull Level level, @NotNull RunSession session) {
        super(player);
        this.level = level;
        this.session = session;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return list;
    }

    public static @NotNull HandlerList getHandlerList() {
        return list;
    }

    public @NotNull Level getLevel() {
        return level;
    }

    public @NotNull RunSession getSession() {
        return session;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}