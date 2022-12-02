package xyz.oribuin.eternalparkour.command.command.admin;

import dev.rosewood.rosegarden.RosePlugin;
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

public class ResetLevelCommand extends RoseSubCommand {

    public ResetLevelCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, Level level) {
        var manager = this.rosePlugin.getManager(ParkourManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        manager.deleteLevelData(level);
        locale.sendMessage(context.getSender(), "command-admin-resetlevel-success", StringPlaceholders.single("level", level.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "resetlevel";
    }

    @Override
    public String getDescriptionKey() {
        return "command-admin-resetlevel-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.admin";
    }
}
