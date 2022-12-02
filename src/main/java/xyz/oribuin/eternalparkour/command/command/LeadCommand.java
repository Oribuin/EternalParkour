package xyz.oribuin.eternalparkour.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.UserData;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeadCommand extends RoseCommand {

    public LeadCommand(RosePlugin rosePlugin, RoseCommandWrapper wrapper) {
        super(rosePlugin, wrapper);
    }

    @RoseExecutable
    public void execute(CommandContext context, Level level, @Optional Integer page) {
        final var manager = this.rosePlugin.getManager(ParkourManager.class);
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        // We get the top players for the level, this is done asynchronously.
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, () -> {
            var newLevel = manager.calculateLevel(level);

            final var placeholders = StringPlaceholders.builder()
                    .addPlaceholder("level", newLevel.getId())
                    .addPlaceholder("average", newLevel.getAverageTime());

            locale.sendSimpleMessage(context.getSender(), "command-leaderboard-header", placeholders.build());
            // Split into pages of 10
            Map<Integer, UserData> topPlayers = this.splitMap(newLevel.getTopUsers(), page == null ? 1 : page, Setting.LEADERBOARD_PLAYERS_PER_PAGE.getInt());

            topPlayers.forEach((integer, data) -> {
                var player = Bukkit.getOfflinePlayer(data.getPlayer());

                placeholders.addPlaceholder("rank", integer)
                        .addPlaceholder("player", player.getName())
                        .addPlaceholder("time", PluginUtils.parseToScore(data.getBestTime()))
                        .addPlaceholder("attempts", data.getAttempts())
                        .addPlaceholder("completed", PluginUtils.parseToDate(data.getBestTimeAchieved()));

                List<String> messages = locale.getLocaleMessages("command-leaderboard-entry", placeholders.build());
                messages.forEach(context.getSender()::sendMessage);
            });
        });

    }

    @Override
    protected String getDefaultName() {
        return "leaderboard";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return  List.of("lead", "top");
    }

    @Override
    public String getDescriptionKey() {
        return "command-leaderboard-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalparkour.command.leaderboard";
    }

    private Map<Integer, UserData> splitMap(Map<Integer, UserData> map, int page, int maxPerPage) {
        Map<Integer, UserData> newMap = new HashMap<>();
        int start = (page - 1) * maxPerPage;
        int end = start + maxPerPage;
        int i = 0;
        for (Map.Entry<Integer, UserData> entry : map.entrySet()) {
            if (i >= start && i < end) {
                newMap.put(entry.getKey(), entry.getValue());
            }
            i++;
        }

        return newMap;
    }

}
