package xyz.oribuin.eternalparkour.task;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.particle.ParticleData;
import xyz.oribuin.eternalparkour.util.PluginUtils;

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
    private final ParticleData greenParticle;
    private final ParticleData redParticle;
    private final ParticleData blueParticle;
    private final ParticleData yellowParticle;

    public EditorTimer(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);

        this.greenParticle = new ParticleData(Particle.REDSTONE).setDustColor(Color.LIME);
        this.redParticle = new ParticleData(Particle.REDSTONE).setDustColor(Color.RED);
        this.blueParticle = new ParticleData(Particle.REDSTONE).setDustColor(Color.BLUE);
        this.yellowParticle = new ParticleData(Particle.REDSTONE).setDustColor(Color.YELLOW);

    }

    @Override
    public void run() {

        // TODO: Fix all boxes being displayed one block smaller than they should be if pos1 is smaller than pos2
        for (var entry : this.manager.getLevelEditors().entrySet()) {
            var player = Bukkit.getPlayer(entry.getKey());

            var spawnRegion = entry.getValue().getLevel().getStartRegion();
            if (spawnRegion != null) {
                this.getCube(spawnRegion).forEach(location -> greenParticle.spawn(player, location, 1));
            }

            var endRegion = entry.getValue().getLevel().getFinishRegion();
            if (endRegion != null) {
                this.getCube(endRegion).forEach(location -> redParticle.spawn(player, location, 1));
            }

            // all other regions
            entry.getValue().getLevel().getLevelRegions().forEach(region -> this.getCube(region).forEach(location -> blueParticle.spawn(player, location, 1)));

            // All checkpoints
            entry.getValue().getLevel().getCheckpoints().forEach((id, loc) -> {
                var corner1 = loc.clone().add(1, 2, 1);
                var corner2 = loc.clone();

                this.getCube(corner1, corner2, 0.5, 0).forEach(location -> yellowParticle.spawn(player, location, 1));
            });
        }
    }

    private List<Location> getCube(Region region) {
        // Get the first & second corner of the region
        Location pos1 = region.getPos1();
        Location pos2 = region.getPos2();

        if (pos1 == null || pos2 == null)
            return new ArrayList<>();

        return this.getCube(pos1, pos2, 0.5, 1.0);
    }

    /**
     * Get all the particle locations to spawn a hollow cube in between point A & Point B
     *
     * @param corner1          The first corner.
     * @param corner2          The second corner
     * @param particleDistance The distance between particles
     * @return The list of particle locations
     * @author Esophose
     * @ <a href="https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleOutline.java#L86">...</a>
     */
    private List<Location> getCube(Location corner1, Location corner2, double particleDistance, double outerAdjustment) {
        List<Location> result = new ArrayList<>();
        var world = corner1.getWorld();
        var minX = Math.min(corner1.getX(), corner2.getX());
        var minY = Math.min(corner1.getY(), corner2.getY());
        var minZ = Math.min(corner1.getZ(), corner2.getZ());
        var maxX = Math.max(corner1.getX(), corner2.getX()) + outerAdjustment;
        var maxY = Math.max(corner1.getY(), corner2.getY()) + outerAdjustment;
        var maxZ = Math.max(corner1.getZ(), corner2.getZ()) + outerAdjustment;

        for (double x = minX; x <= maxX; x += particleDistance) {
            result.add(new Location(world, x, minY, minZ));
            result.add(new Location(world, x, maxY, minZ));
            result.add(new Location(world, x, minY, maxZ));
            result.add(new Location(world, x, maxY, maxZ));
        }

        for (double y = minY; y <= maxY; y += particleDistance) {
            result.add(new Location(world, minX, y, minZ));
            result.add(new Location(world, maxX, y, minZ));
            result.add(new Location(world, minX, y, maxZ));
            result.add(new Location(world, maxX, y, maxZ));
        }

        for (double z = minZ; z <= maxZ; z += particleDistance) {
            result.add(new Location(world, minX, minY, z));
            result.add(new Location(world, maxX, minY, z));
            result.add(new Location(world, minX, maxY, z));
            result.add(new Location(world, maxX, maxY, z));
        }

        return result;
    }


}
