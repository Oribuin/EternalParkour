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

public class DelRegionCommand extends RoseSubCommand {

    public DelRegionCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, Level level) {
        var player = (Player) context.getSender();
        var manager = this.rosePlugin.getManager(ParkourManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        var region = level.getRegionAt(player.getLocation());
        if (region == null || !level.isParkourRegion(region)) {
            locale.sendMessage(player, "region-not-found");
            return;
        }

        level.getLevelRegions().remove(region);
        locale.sendMessage(player, "command-edit-del-region-success", StringPlaceholders.single("name", level.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "delregion";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
