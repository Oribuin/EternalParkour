package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.eternalparkour.command.command.admin.ResetLevelCommand;
import xyz.oribuin.eternalparkour.command.command.admin.ResetUserCommand;
import xyz.oribuin.eternalparkour.manager.LocaleManager;

public class AdminCommand extends RoseCommand {

    public AdminCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent,
                ResetLevelCommand.class,
                ResetUserCommand.class
        );
    }


    @RoseExecutable
    public void execute(CommandContext context, RoseSubCommand command) {
        this.rosePlugin.getManager(LocaleManager.class).sendMessage(context.getSender(), "command-admin-usage");
    }
    @Override
    protected String getDefaultName() {
        return "admin";
    }

    @Override
    public String getDescriptionKey() {
        return "command-admin-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.admin";
    }

}
