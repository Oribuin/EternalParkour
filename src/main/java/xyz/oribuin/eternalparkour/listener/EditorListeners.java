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
import org.bukkit.inventory.EquipmentSlot;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Region;
import xyz.oribuin.eternalparkour.parkour.edit.EditSession;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;
import xyz.oribuin.eternalparkour.particle.ParticleData;
import xyz.oribuin.eternalparkour.util.PluginUtils;

import java.util.HashMap;
import java.util.Map;

public class EditorListeners implements Listener {

    private final ParkourManager manager;

    public EditorListeners(EternalParkour plugin) {
        this.manager = plugin.getManager(ParkourManager.class);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var block = event.getClickedBlock();
        var action = event.getAction();

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (block == null)
            return;

        var editSession = this.manager.getLevelEditors().get(player.getUniqueId());
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
        var current = session.getRegion() == null ? new Region() : session.getRegion();
        var particle = new ParticleData(Particle.REDSTONE);
        var location = PluginUtils.asBlockLoc(block.getLocation());
        var particleLocations = PluginUtils.getCube(location, location.clone().add(1, 1, 1), 0.5);

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

            System.out.println("Saving region for " + session.getLevel().getId());

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

        System.out.println("Editing checkpoints");

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
