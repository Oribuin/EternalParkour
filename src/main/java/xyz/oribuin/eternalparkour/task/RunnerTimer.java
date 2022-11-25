package xyz.oribuin.eternalparkour.task;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.hook.PAPI;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the task that will display the time for the player in the action bar.
 *
 * @author oribuin
 */
public class RunnerTimer extends BukkitRunnable {

    private final ParkourManager manager;
    private final String timerMessage;
    private final boolean useMiniMessage;

    private final SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SSS");

    public RunnerTimer(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);
        this.timerMessage = Setting.RUNNER_TIME_MESSAGE.getString();
        this.useMiniMessage = Setting.RUNNER_TIMER_USE_MINIMESSAGE.getBoolean();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        var current = System.currentTimeMillis();

        for (var entry : this.manager.getActiveRunners().entrySet()) {
            var player = Bukkit.getPlayer(entry.getKey());
            var runner = entry.getValue();

            if (player == null || !player.isOnline()) {
                this.manager.getActiveRunners().remove(entry.getKey());
                continue;
            }

            var placeholders = StringPlaceholders.single("time", formatter.format(new Date((current - runner.getStartTime()))));
            if (PluginUtils.usingPaper() && useMiniMessage) {
                var miniMessage = MiniMessage.miniMessage()
                        .deserialize(PAPI.apply(player, placeholders.apply(timerMessage)));

                player.sendActionBar(miniMessage);
                return;
            }

            if (PluginUtils.usingPaper()) {
                player.sendActionBar(HexUtils.colorify(PAPI.apply(player, placeholders.apply(timerMessage))));
                return;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(HexUtils.colorify(
                            PAPI.apply(player, placeholders.apply(timerMessage))
                    )));
        }
    }

}
