package xyz.oribuin.eternalparkour.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalparkour.manager.ParkourManager;
import xyz.oribuin.eternalparkour.parkour.Level;

import java.util.List;

public class LevelArgumentHandler extends RoseCommandArgumentHandler<Level> {

    public LevelArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Level.class);
    }

    @Override
    protected Level handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        var input = argumentParser.next();

        var level = this.rosePlugin.getManager(ParkourManager.class).getLevel(input);
        if (level == null)
            throw new HandledArgumentException("argument-handler-level", StringPlaceholders.single("level", input));

        return level;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        List<String> levels = this.rosePlugin.getManager(ParkourManager.class).getLevels().stream()
                .map(Level::getId)
                .toList();

        if (levels.isEmpty())
            return List.of("<no levels>");

        return levels;
    }

}
