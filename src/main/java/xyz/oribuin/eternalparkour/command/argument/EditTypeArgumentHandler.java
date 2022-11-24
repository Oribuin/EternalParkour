package xyz.oribuin.eternalparkour.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalparkour.parkour.edit.EditType;

import java.util.Arrays;
import java.util.List;

public class EditTypeArgumentHandler extends RoseCommandArgumentHandler<EditType> {

    public EditTypeArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, EditType.class);
    }

    @Override
    protected EditType handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        var input = argumentParser.next();

        EditType type = EditType.fromString(input);
        if (type == null)
            throw new HandledArgumentException("argument-handler-edit-type", StringPlaceholders.single("type", input));

        return type;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Arrays.stream(EditType.values()).map(Enum::name).map(String::toLowerCase).toList();
    }

}
