package xyz.oribuin.eternalparkour.command.command.admin;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.OfflinePlayerArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.OfflinePlayer;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;

import java.util.concurrent.CompletableFuture;

public class ResetUserCommand extends RoseSubCommand {

    public ResetUserCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, OfflinePlayer player, @Optional Level level) {
        ParkourManager manager = this.rosePlugin.getManager(ParkourManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (level == null) {
            manager.deleteUser(player.getUniqueId());
            locale.sendMessage(context.getSender(), "command-admin-resetuser-all", StringPlaceholders.single("player", player.getName()));
            return;
        }

        manager.deleteUser(player.getUniqueId(), level);
        locale.sendMessage(context.getSender(), "command-admin-resetuser-level", StringPlaceholders.builder("player", player.getName())
                .addPlaceholder("level", level.getId())
                .build());

        CompletableFuture.runAsync(() -> manager.calculateLevel(level));
    }

    @Override
    protected String getDefaultName() {
        return "resetuser";
    }

    @Override
    public String getDescriptionKey() {
        return "command-admin-resetuser-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.admin";
    }
}
