package xyz.oribuin.eternalparkour;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import xyz.oribuin.eternalparkour.hook.PAPI;
import xyz.oribuin.eternalparkour.listener.EditorListeners;
import xyz.oribuin.eternalparkour.listener.PlayerListeners;
import xyz.oribuin.eternalparkour.listener.RegionListeners;
import xyz.oribuin.eternalparkour.manager.CommandManager;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.manager.DataManager;
import xyz.oribuin.eternalparkour.manager.LocaleManager;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.task.EditorTimer;
import xyz.oribuin.eternalparkour.task.LeaderboardTimer;
import xyz.oribuin.eternalparkour.task.RunnerTimer;
import xyz.oribuin.eternalparkour.util.EventWaiter;

import java.util.Collections;
import java.util.List;

public class EternalParkour extends RosePlugin {

    private static EternalParkour instance;
    private static EventWaiter eventWaiter;

    public EternalParkour() {
        super(-1, 16982, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
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
        this.getServer().getPluginManager().registerEvents(new EditorListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new RegionListeners(this), this);

        // Register PlaceholderAPI
        new PAPI(this);

        // Register Event Waiter
        eventWaiter = new EventWaiter();
    }

    @Override
    public void reload() {
        super.reload();

        Bukkit.getScheduler().cancelTasks(this);

        if (Setting.RUNNER_TIME_ENABLED.getBoolean())
            new RunnerTimer(this).runTaskTimerAsynchronously(this, 0, Setting.RUNNER_TIMER_INTERVAL.getInt());

        if (Setting.EDITOR_TASK_ENABLED.getBoolean())
            new EditorTimer(this).runTaskTimerAsynchronously(this, 0, Setting.EDITOR_TASK_INTERVAL.getInt());

        if (Setting.LEADERBOARD_AUTO_UPDATE.getBoolean())
            new LeaderboardTimer(this).runTaskTimerAsynchronously(this, 0, Setting.LEADERBOARD_UPDATE_INTERVAL.getInt());
    }

    @Override
    protected void disable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(ParkourManager.class);
    }

    public static EternalParkour getInstance() {
        return instance;
    }

    public static EventWaiter getEventWaiter() {
        return eventWaiter;
    }

}
