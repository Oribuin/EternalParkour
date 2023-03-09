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
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;

public class DelStartCommand extends RoseSubCommand {

    public DelStartCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Optional Level level) {
        Player player = (Player) context.getSender();

        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        ParkourManager manager = this.rosePlugin.getManager(ParkourManager.class);
        EditSession session = manager.getLevelEditors().get(player.getUniqueId());

        if (level == null) {
            if (session == null) {
                locale.sendMessage(player, "argument-handler-level");
                return;
            }

            level = session.getLevel();
        }

        level.setStartRegion(null);
        locale.sendMessage(context.getSender(), "command-edit-del-start-success", StringPlaceholders.single("name", level.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "delstart";
    }

}
