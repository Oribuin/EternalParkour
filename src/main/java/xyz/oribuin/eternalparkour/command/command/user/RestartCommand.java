package xyz.oribuin.eternalparkour.command.command.user;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.RunSession;

public class RestartCommand extends RoseCommand {

    public RestartCommand(RosePlugin plugin, RoseCommandWrapper parent) {
        super(plugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        ParkourManager manager = this.rosePlugin.getManager(ParkourManager.class);
        Player player = (Player) context.getSender();

        RunSession session = manager.getRunSession(player.getUniqueId());
        if (session == null) {
            locale.sendMessage(player, "command-restart-not-playing");
            return;
        }

        manager.failRun(player);
        locale.sendMessage(player, "command-restart-success", StringPlaceholders.single("level", session.getLevel().getId()));
    }

    @Override
    protected String getDefaultName() {
        return "restart";
    }

    @Override
    public String getDescriptionKey() {
        return "command-restart-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.restart";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
