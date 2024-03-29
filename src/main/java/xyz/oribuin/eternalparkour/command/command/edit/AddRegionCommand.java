package xyz.oribuin.eternalparkour.command.command.edit;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;

public class AddRegionCommand extends RoseSubCommand {

    public AddRegionCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Optional Level level) {
        Player player = (Player) context.getSender();

        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        ParkourManager manager = this.rosePlugin.getManager(ParkourManager.class);

        Level newLevel = manager.startEditing(player, level, EditType.ADD_REGION);

        if (newLevel != null)
            locale.sendMessage(player, "command-edit-add-region-start", StringPlaceholders.single("name", newLevel.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "addregion";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
