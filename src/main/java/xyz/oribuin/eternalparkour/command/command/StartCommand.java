package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;

public class StartCommand extends RoseCommand {

    public StartCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Level level) {
        var player = (Player) context.getSender();
        var manager = this.rosePlugin.getManager(ParkourManager.class);

        if (manager.startRun(player, level) != null) {
            this.rosePlugin.getManager(LocaleManager.class).sendCustomMessage(player, String.format("&fStarted run of #00B4DB%s&f.", level.getId()));
        };
    }

    @Override
    protected String getDefaultName() {
        return "start";
    }

    @Override
    public String getDescriptionKey() {
        return "command-start-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.start";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
