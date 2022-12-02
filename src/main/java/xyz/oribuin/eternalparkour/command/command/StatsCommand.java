package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.util.PluginUtils;

public class StatsCommand extends RoseCommand {

    public StatsCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Level level, @Optional OfflinePlayer player) {
        var manager = this.rosePlugin.getManager(ParkourManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        if (!(context.getSender() instanceof Player) && player == null) {
            locale.sendMessage(context.getSender(), "command-argument-player");
            return;
        }

        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
            var offlinePlayer = player == null ? (OfflinePlayer) context.getSender() : player;
            var data = manager.getUser(offlinePlayer.getUniqueId(), level.getId());

            var placeholders = StringPlaceholders.builder("player", offlinePlayer.getName())
                    .addPlaceholder("level", level.getId())
                    .addPlaceholder("time", PluginUtils.parseToScore(data.getBestTime()))
                    .addPlaceholder("attempts", data.getAttempts())
                    .addPlaceholder("completed", data.getCompletions())
                    .addPlaceholder("rank", level.getLeaderboardPosition(offlinePlayer.getUniqueId()))
                    .build();

            locale.sendMessage(context.getSender(), "command-stats-header", placeholders);
            locale.getLocaleMessages("command-stats-body", placeholders).forEach(context.getSender()::sendMessage);
        });
    }

    @Override
    protected String getDefaultName() {
        return "stats";
    }

    @Override
    public String getDescriptionKey() {
        return "command-stats-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.stats";
    }

}
