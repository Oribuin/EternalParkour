package xyz.oribuin.eternalparkour;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import xyz.oribuin.eternalparkour.manager.CommandManager;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;

import xyz.oribuin.eternalparkour.manager.DataManager;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.task.RunnerTimer;

import java.util.Collections;
import java.util.List;

public class EternalParkour extends RosePlugin {

    private static EternalParkour instance;

    public EternalParkour() {
        super(-1, -1, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    protected void enable() {

        // Make sure the server is using 1.16+
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("You cannot use EternalParkour on 1." + NMSUtil.getVersionNumber() + ", We are limited to 1.16.5+");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register Listeners

    }

    @Override
    public void reload() {
        super.reload();

        Bukkit.getScheduler().cancelTasks(this);
        new RunnerTimer(this).runTaskTimerAsynchronously(this, 0, Setting.RUNNER_TIMER_INTERVAL.getInt());

    }

    @Override
    protected void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Collections.emptyList();
    }

    public static EternalParkour getInstance() {
        return instance;
    }

}
