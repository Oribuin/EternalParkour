package xyz.oribuin.eternalparkour.command.command.edit;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.util.PluginUtils;

public class TeleportCommand extends RoseSubCommand {

    public TeleportCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, Level level) {
        var player = (Player) context.getSender();
        var manager = this.rosePlugin.getManager(ParkourManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        var region = level.getRegionAt(player.getLocation());
        if (region == null || !level.isStartRegion(region)) {
            locale.sendMessage(player, "region-not-found");
            return;
        }

        level.setTeleport(player.getLocation());
        manager.saveLevel(level);
        locale.sendMessage(player, "command-edit-teleport-success", StringPlaceholders.builder("name", level.getId())
                .addPlaceholder("location", PluginUtils.formatLocation(player.getLocation()))
                .build());
    }

    @Override
    protected String getDefaultName() {
        return "teleport";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
