package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeadCommand extends RoseCommand {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss.SSS");

    public LeadCommand(RosePlugin rosePlugin, RoseCommandWrapper wrapper) {
        super(rosePlugin, wrapper);
    }

    @RoseExecutable
    public void execute(CommandContext context, Level level) {
        final var manager = this.rosePlugin.getManager(ParkourManager.class);
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        // We get the top 10 players for the level, this is done asynchronously.
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
            var newLevel = manager.calculateLevel(level);

            final var placeholders = StringPlaceholders.builder()
                    .addPlaceholder("id", newLevel.getId())
                    .addPlaceholder("average", newLevel.getAverageTime());

            locale.sendSimpleMessage(context.getSender(), "command-lead-header", placeholders.build());
            newLevel.getTopUsers().forEach((integer, data) -> {
                var player = Bukkit.getOfflinePlayer(data.getPlayer());

                placeholders.addPlaceholder("rank", integer)
                        .addPlaceholder("player", player.getName())
                        .addPlaceholder("time", timeFormat.format(new Date(data.getBestTime())))
                        .addPlaceholder("attempts", data.getAttempts())
                        .addPlaceholder("completed", dateFormat.format(new Date(data.getLastCompletion())));

                locale.sendSimpleMessage(context.getSender(), "command-lead-entry", placeholders.build());
            });
        });


    }

    @Override
    protected String getDefaultName() {
        return "lead";
    }

    @Override
    public String getDescriptionKey() {
        return "command-lead-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.lead";
    }

}
