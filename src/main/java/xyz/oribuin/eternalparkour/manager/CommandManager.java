package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import xyz.oribuin.eternalparkour.command.ParkourCommandWrapper;

import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return List.of(ParkourCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return List.of("xyz.oribuin.eternalparkour.command.argument");
    }


}
