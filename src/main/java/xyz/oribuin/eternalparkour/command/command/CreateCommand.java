package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;

public class CreateCommand extends RoseCommand {

    public CreateCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name) {
        Player player = (Player) context.getSender();
        ParkourManager manager = this.rosePlugin.getManager(ParkourManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (manager.getLevel(name) != null) {
            locale.sendMessage(player, "command-create-already-exists");
            return;
        }

        Level level = new Level(name.toLowerCase());
        level.setTeleport(player.getLocation()); // Set the player's location as the level's spawn
        manager.saveLevel(level); // Save the level to the database

        locale.sendMessage(player, "command-create-success", StringPlaceholders.single("name", name));
    }

    @Override
    protected String getDefaultName() {
        return "create";
    }

    @Override
    public String getDescriptionKey() {
        return "command-create-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.create";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
