package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;

public class DeleteCommand extends RoseCommand {

    public DeleteCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name) {
        var manager = this.rosePlugin.getManager(ParkourManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        var level = manager.getLevel(name);
        if (level == null) {
            locale.sendMessage(context.getSender(), "command-delete-doesnt-exists");
            return;
        }

        manager.deleteLevel(level);
        locale.sendMessage(context.getSender(), "command-delete-success", StringPlaceholders.single("name", level.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "delete";
    }

    @Override
    public String getDescriptionKey() {
        return "command-delete-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.delete";
    }

}
