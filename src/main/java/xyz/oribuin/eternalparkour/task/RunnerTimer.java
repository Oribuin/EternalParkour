package xyz.oribuin.eternalparkour.task;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.hook.PAPI;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.RunSession;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.util.Map;
import java.util.UUID;

/**
 * This is the task that will display the time for the player in the action bar.
 *
 * @author oribuin
 */
public class RunnerTimer extends BukkitRunnable {

    private final ParkourManager manager;
    private final String timerMessage;

    public RunnerTimer(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);
        this.timerMessage = Setting.RUNNER_TIME_MESSAGE.getString();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        long current = System.currentTimeMillis();

        this.manager.getActiveRunners().forEach((parkourPlayer, runSession) -> {
            Player cachedPlayer = parkourPlayer.getPlayer();

            if (cachedPlayer == null) {
                return;
            }

            StringPlaceholders placeholders = StringPlaceholders.single("time", PluginUtils.parseToScore(current - runSession.getStartTime()));
            if (PluginUtils.usingPaper()) {
                cachedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(PAPI.apply(cachedPlayer, placeholders.apply(timerMessage))));
                return;
            }

            // Player#spigot is so very cringe, but this is how it has to be done if we want to the plugin on spigot's website.
            // but realistically, who doesn't use paper?
            cachedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(HexUtils.colorify(
                            PAPI.apply(cachedPlayer, placeholders.apply(timerMessage)))));
        });
    }

}
