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
import xyz.oribuin.eternalparkour.parkour.edit.EditType;

public class CheckpointsCommand extends RoseSubCommand {

    public CheckpointsCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, Level level) {
        Player player = (Player) context.getSender();
        ParkourManager manager = this.rosePlugin.getManager(ParkourManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        manager.startEditing(player, level, EditType.CHANGE_CHECKPOINTS);
        locale.sendMessage(player, "command-edit-checkpoints-start", StringPlaceholders.single("name", level.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "checkpoints";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
