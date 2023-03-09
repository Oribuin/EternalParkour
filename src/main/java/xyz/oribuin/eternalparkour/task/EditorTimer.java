package xyz.oribuin.eternalparkour.task;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.particle.ParticleData;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the task that will display the time for the player in the action bar.
 *
 * @author oribuin
 */
public class EditorTimer extends BukkitRunnable {

    private final ParkourManager manager;
    // mmm, this is terrible to look at
    private final ParticleData startParticle;
    private final ParticleData finishParticle;
    private final ParticleData levelParticle;
    private final ParticleData checkpointParticle;

    public EditorTimer(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);

        this.startParticle = new ParticleData(Particle.REDSTONE).setDustColor(Setting.EDITOR_TASK_START_COLOR.getColor());
        this.finishParticle = new ParticleData(Particle.REDSTONE).setDustColor(Setting.EDITOR_TASK_FINISH_COLOR.getColor());
        this.levelParticle = new ParticleData(Particle.REDSTONE).setDustColor(Setting.EDITOR_TASK_LEVEL_COLOR.getColor());
        this.checkpointParticle = new ParticleData(Particle.REDSTONE).setDustColor(Setting.EDITOR_TASK_CHECKPOINT_COLOR.getColor());
    }

    @Override
    public void run() {
        this.manager.getLevelEditors().forEach((uuid, session) -> {
            Player cachedPlayer = this.manager.getPPlayer(uuid).getPlayer();
            if (cachedPlayer == null)
                return;

            Region spawnRegion = session.getLevel().getStartRegion();

            if (spawnRegion != null) {
                this.getCube(spawnRegion).forEach(location -> startParticle.spawn(cachedPlayer, location, 1));
            }

            Region endRegion = session.getLevel().getFinishRegion();
            if (endRegion != null) {
                this.getCube(endRegion).forEach(location -> finishParticle.spawn(cachedPlayer, location, 1));
            }

            // Checkpoint regions
            session.getLevel().getCheckpoints().values().forEach(checkpoint -> {
                Region region = checkpoint.getRegion();
                if (region != null)
                    this.getCube(region).forEach(location -> checkpointParticle.spawn(cachedPlayer, location, 1));
            });

            // all other regions
            session.getLevel().getLevelRegions().forEach(region -> this.getCube(region).forEach(location -> levelParticle.spawn(cachedPlayer, location, 1)));
        });
    }

    private List<Location> getCube(Region region) {
        // Get the first & second corner of the region
        Location pos1 = region.getPos1();
        Location pos2 = region.getPos2();

        if (pos1 == null || pos2 == null)
            return new ArrayList<>();


        return this.getCube(pos1, pos2, 1.0);
    }

    /**
     * Get all the particle locations to spawn a hollow cube in between point A & Point B
     *
     * @param corner1 The first corner.
     * @param corner2 The second corner
     * @return The list of particle locations
     * @author Esophose
     * @ <a href="https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleOutline.java#L86">...</a>
     */
    private List<Location> getCube(Location corner1, Location corner2, double outerAdjustment) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX()) + outerAdjustment;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + outerAdjustment;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + outerAdjustment;

        for (double x = minX; x <= maxX; x += 0.5) {
            result.add(new Location(world, x, minY, minZ));
            result.add(new Location(world, x, maxY, minZ));
            result.add(new Location(world, x, minY, maxZ));
            result.add(new Location(world, x, maxY, maxZ));
        }

        for (double y = minY; y <= maxY; y += 0.5) {
            result.add(new Location(world, minX, y, minZ));
            result.add(new Location(world, maxX, y, minZ));
            result.add(new Location(world, minX, y, maxZ));
            result.add(new Location(world, maxX, y, maxZ));
        }

        for (double z = minZ; z <= maxZ; z += 0.5) {
            result.add(new Location(world, minX, minY, z));
            result.add(new Location(world, maxX, minY, z));
            result.add(new Location(world, minX, maxY, z));
            result.add(new Location(world, maxX, maxY, z));
        }

        return result;
    }


}
