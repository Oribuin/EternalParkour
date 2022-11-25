package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.command.command.edit.AddFinishCommand;
import xyz.oribuin.eternalparkour.command.command.edit.AddRegionCommand;
import xyz.oribuin.eternalparkour.command.command.edit.AddStartCommand;
import xyz.oribuin.eternalparkour.command.command.edit.CheckpointsCommand;
import xyz.oribuin.eternalparkour.command.command.edit.DelFinishCommand;
import xyz.oribuin.eternalparkour.command.command.edit.DelRegionCommand;
import xyz.oribuin.eternalparkour.command.command.edit.DelStartCommand;
import xyz.oribuin.eternalparkour.command.command.edit.TeleportCommand;
import xyz.oribuin.eternalparkour.command.command.edit.ViewCommand;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;

public class EditCommand extends RoseCommand {

    public EditCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent,
                AddFinishCommand.class,
                AddRegionCommand.class,
                AddStartCommand.class,
                CheckpointsCommand.class,
                DelFinishCommand.class,
                DelRegionCommand.class,
                DelStartCommand.class,
                TeleportCommand.class,
                ViewCommand.class
        );
    }

    @RoseExecutable
    public void execute(CommandContext context, @Optional RoseSubCommand command) {
        if (command == null && context.getSender() instanceof Player player) {
            this.rosePlugin.getManager(ParkourManager.class).stopEditing(player);
            this.rosePlugin.getManager(LocaleManager.class).sendMessage(player, "command-edit-success");
            return;
        }

        // This is a empty command, it's only used to register the sub commands
    }

    @Override
    protected String getDefaultName() {
        return "edit";
    }

    @Override
    public String getDescriptionKey() {
        return "command-edit-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.edit";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
