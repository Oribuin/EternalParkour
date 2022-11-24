package xyz.oribuin.eternalparkour.task;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.hook.PAPI;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.util.PluginUtils;

/**
 * This is the task that will display the time for the player in the action bar.
 *
 * @author oribuin
 */
public class RunnerTimer extends BukkitRunnable {

    private final ParkourManager manager;
    private final String timerMessage;
    private final boolean useMiniMessage;


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

            var placeholders = StringPlaceholders.single("time", PluginUtils.parseFromTime(current - runner.getStartTime()));
            if (PluginUtils.usingPaper() && useMiniMessage) {
                var miniMessage = MiniMessage.miniMessage()
                        .deserialize(PAPI.apply(player, placeholders.apply(timerMessage)));

                player.sendActionBar(miniMessage);
                return;
            }

            player.sendActionBar(HexUtils.colorify(PAPI.apply(player, placeholders.apply(timerMessage))));
        }
    }

}
