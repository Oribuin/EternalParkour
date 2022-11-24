package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;

public class EditCommand extends RoseCommand {

    public EditCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, @Optional Level level, @Optional EditType type) {
        var player = (Player) context.getSender();
        var manager = this.rosePlugin.getManager(ParkourManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        if (manager.getEditSession(player) == null || level == null || type == null) {
            manager.stopEditing(player);
            locale.sendMessage(player, "command-edit-disabled");
            return;
        }

        var placeholders = StringPlaceholders.builder()
                .addPlaceholder("type", type.name().toLowerCase().replace("_", " "))
                .addPlaceholder("name", level.getId());


        if (type == EditType.REMOVE_FINISH ||
                type == EditType.REMOVE_REGION ||
                type == EditType.REMOVE_START ||
                type == EditType.SET_SPAWN) {

            switch (type) {
                case REMOVE_FINISH -> level.setFinishRegion(null);
                case SET_SPAWN -> level.setSpawn(player.getLocation());
                case REMOVE_START -> level.setStartRegion(null);

                case REMOVE_REGION -> {
                    // Check if there is a region to remove
                    if (level.getLevelRegions().isEmpty()) {
                        locale.sendMessage(player, "command-edit-no-regions", placeholders.build());
                        return;
                    }

                    // check if the player is inside a region
                    var region = level.getRegionAt(player.getLocation());
                    if (region == null) {
                        locale.sendMessage(player, "command-edit-not-in-region", placeholders.build());
                        return;
                    }

                    // Remove the region
                    level.getLevelRegions().remove(region);
                }
            }

            locale.sendMessage(player, "command-edit-success", placeholders.build());
            manager.saveLevel(level);
            return;
        }

        manager.startEditing(player, level, type);
        locale.sendMessage(player, "command-edit-start", placeholders.build());
    }

    @Override
    protected String getDefaultName() {
        return "edit";
    }

    @Override
    public String getDescriptionKey() {
        return "command-edit-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.edit";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
