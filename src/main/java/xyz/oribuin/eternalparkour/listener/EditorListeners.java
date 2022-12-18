package xyz.oribuin.eternalparkour.listener;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;
import xyz.oribuin.eternalparkour.particle.ParticleData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorListeners implements Listener {

    private final ParkourManager manager;

    public EditorListeners(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (block == null)
            return;

        EditSession editSession = this.manager.getLevelEditors().get(player.getUniqueId());
        if (editSession == null || editSession.getType() == EditType.VIEWING)
            return;

        event.setCancelled(true);

        // If the player is creating a region
        if (editSession.getType() == EditType.ADD_REGION || editSession.getType() == EditType.ADD_FINISH || editSession.getType() == EditType.ADD_START) {
            this.editRegion(player, editSession, block, action);
            return;
        }

        if (editSession.getType() == EditType.CHANGE_CHECKPOINTS) {
            this.editCheckpoint(player, editSession, block);
        }
    }

    /**
     * Edit a region of the level once both points are selected
     *
     * @param player  The player editing the region
     * @param session The edit session
     * @param block   The block the player clicked
     * @param action  The action the player performed
     */
    private void editRegion(Player player, EditSession session, Block block, Action action) {
        Region  current = session.getRegion() == null ? new Region() : session.getRegion();
        ParticleData particle = new ParticleData(Particle.REDSTONE);
        Location location = block.getLocation().clone();
        List<Location> particleLocations = this.getCube(location, location.clone().add(1, 1, 1), 0.5);

        if (action == Action.LEFT_CLICK_BLOCK) {
            current.setPos1(location);
            particle.setDustColor(Color.LIME).cacheParticleData();
            particleLocations.forEach(loc -> particle.spawn(player, loc, 1));
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            current.setPos2(location);
            particle.setDustColor(Color.RED).cacheParticleData();
            particleLocations.forEach(loc -> particle.spawn(player, loc, 1));
        }

        session.setRegion(current);

        if (current.getPos1() != null && current.getPos2() != null) {

            // We're not adding a region that already exists
            if (session.getType() == EditType.ADD_REGION && session.getLevel().isParkourRegion(current)) {
                return;
            }

            switch (session.getType()) {
                case ADD_REGION -> session.getLevel().getLevelRegions().add(current);
                case ADD_START -> session.getLevel().setStartRegion(current);
                case ADD_FINISH -> session.getLevel().setFinishRegion(current);
            }

            this.manager.saveLevel(session.getLevel());
            this.manager.startEditing(player, session.getLevel(), session.getType());
        }

    }

    /**
     * Edit all the checkpoints of the level
     *
     * @param player  The player editing the checkpoints
     * @param session The edit session
     * @param block   The block the player clicked
     */
    private void editCheckpoint(Player player, EditSession session, Block block) {
        Level level = session.getLevel();
        Map<Integer, Location> checkpoints = level.getCheckpoints();

        // Get checkpoint from the block
        Map.Entry<Integer, Location> checkpoint = checkpoints.entrySet().stream()
                .filter(entry -> entry.getValue().equals(block.getLocation()))
                .findFirst()
                .orElse(null);

        List<Location> particleLocations = this.getCube(block.getLocation(), block.getLocation().clone().add(1, 1, 1), 0.5);
        ParticleData particle = new ParticleData(Particle.REDSTONE);

        // Add a new checkpoint
        if (checkpoint == null) {
            checkpoints.put(checkpoints.size() + 1, block.getLocation());
            particle.setDustColor(Color.LIME)
                    .cacheParticleData();

            particleLocations.forEach(location -> particle.spawn(player, location, 1));
            level.setCheckpoints(checkpoints);
            this.manager.saveEditSession(player);
            return;
        }

        // Remove a checkpoint
        checkpoints.remove(checkpoint.getKey());

        // Reorder the checkpoints
        Map<Integer, Location> newCheckpoints = new HashMap<>();
        int i = 1;
        for (Map.Entry<Integer, Location> entry : checkpoints.entrySet()) {
            newCheckpoints.put(i++, entry.getValue());
        }

        particle.setDustColor(Color.RED).cacheParticleData();

        particleLocations.forEach(location -> particle.spawn(player, location, 1));
        level.setCheckpoints(newCheckpoints);
        this.manager.saveEditSession(player);
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
    private List<Location> getCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

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
