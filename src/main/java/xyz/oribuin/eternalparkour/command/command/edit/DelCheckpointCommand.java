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
import xyz.oribuin.eternalparkour.parkour.Checkpoint;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;

public class DelCheckpointCommand extends RoseSubCommand {

    public DelCheckpointCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
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

        Checkpoint checkpoint = level.getCheckpoint(player.getLocation());
        if (checkpoint == null) {
            locale.sendMessage(player, "command-edit-del-checkpoint-fail");
            return;
        }

        level.getCheckpoints().remove(checkpoint.getId());
        level.reorganizeCheckpoints();
        locale.sendMessage(player, "command-edit-del-checkpoint-success", StringPlaceholders.single("name", level.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "delcheckpoint";
    }

}
