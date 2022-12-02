package xyz.oribuin.eternalparkour.task;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;

/**
 * This timer will update the leaderboard every x seconds, Default is 10 minutes.
 */
public class LeaderboardTimer extends BukkitRunnable {

    private final ParkourManager manager;

    public LeaderboardTimer(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);
    }

    @Override
    public void run() {
        this.manager.getLevelData().values().forEach(manager::calculateLevel);
    }

}
