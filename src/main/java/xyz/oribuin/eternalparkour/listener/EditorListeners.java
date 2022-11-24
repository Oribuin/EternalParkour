package xyz.oribuin.eternalparkour.listener;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;
import xyz.oribuin.eternalparkour.particle.ParticleData;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorListeners implements Listener {

    private final ParkourManager manager;
    private final Map<UUID, Region> currentRegion = new HashMap<>();

    public EditorListeners(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var block = event.getClickedBlock();
        var action = event.getAction();

        var editSession = this.manager.getLevelEditors().get(player.getUniqueId());
        if (editSession == null)
            return;

        if (block == null)
            return;

        // If the player is creating a region
        if (editSession.getType() == EditType.ADD_REGION || editSession.getType() == EditType.SET_FINISH) {
            this.editRegion(player, editSession, block, action);
            return;
        }

        if (editSession.getType() == EditType.CHANGE_CHECKPOINTS) {
            this.editCheckpoint(player, editSession, block);
            return;
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
        var current = this.currentRegion.getOrDefault(player.getUniqueId(), new Region());
        var particle = new ParticleData(Particle.REDSTONE);

        var particleLocations = PluginUtils.getCube(block.getLocation(), block.getLocation().clone().add(1, 1, 1), 0.5);

        if (action == Action.LEFT_CLICK_BLOCK) {
            current.setPos1(block.getLocation());

            particle.setDustColor(Color.LIME)
                    .cacheParticleData();

            particleLocations.forEach(loc -> particle.spawn(player, loc, 1));
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            current.setPos2(block.getLocation());

            particle.setDustColor(Color.RED)
                    .cacheParticleData();

            particleLocations.forEach(loc -> particle.spawn(player, loc, 1));

        }

        if (current.getPos1() != null && current.getPos2() != null) {
            this.currentRegion.remove(player.getUniqueId());

            if (session.getType() == EditType.ADD_REGION) {
                session.getLevel().getLevelRegions().add(current);
            } else if (session.getType() == EditType.SET_FINISH) {
                session.getLevel().setFinishRegion(current);
            }

            this.manager.saveEditSession(player);
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

        var level = session.getLevel();
        var checkpoints = level.getCheckpoints();

        // Get checkpoint from the block
        var checkpoint = checkpoints.entrySet().stream()
                .filter(entry -> entry.getValue().equals(block.getLocation()))
                .findFirst()
                .orElse(null);

        var particleLocations = PluginUtils.getCube(block.getLocation(), block.getLocation().clone().add(1, 1, 1), 0.5);
        var particle = new ParticleData(Particle.REDSTONE);


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
        var newCheckpoints = new HashMap<Integer, Location>();
        var i = 1;
        for (Map.Entry<Integer, Location> entry : checkpoints.entrySet()) {
            newCheckpoints.put(i++, entry.getValue());
        }

        particle.setDustColor(Color.RED)
                .cacheParticleData();

        particleLocations.forEach(location -> particle.spawn(player, location, 1));
        level.setCheckpoints(newCheckpoints);
        this.manager.saveEditSession(player);
    }


}
