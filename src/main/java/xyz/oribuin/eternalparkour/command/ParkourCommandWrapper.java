package xyz.oribuin.eternalparkour.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class ParkourCommandWrapper extends RoseCommandWrapper {

    public ParkourCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "parkour";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("pk", "epk");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.oribuin.eternalparkour.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
