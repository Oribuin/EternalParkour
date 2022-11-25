package xyz.oribuin.eternalparkour.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Oribuin";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<>() {{
            this.put("#1", "Message Prefix");
            this.put("prefix", "<g:#00B4DB:#0083B0>&lEternalParkour &8| &f");

            this.put("#2", "Generic Command Messages");
            this.put("no-permission", "You don't have permission to execute this.");
            this.put("only-player", "This command can only be executed by a player.");
            this.put("unknown-command", "Unknown command, use #00B4DB/%cmd%&f help for more info");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/%cmd% help &efor command information.");

            this.put("#4", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin.");
            this.put("command-reload-reloaded", "Configuration and locale files were reloaded");

            this.put("#5", "Help Command");
            this.put("command-help-title", "&fAvailable Commands:");
            this.put("command-help-description", "Displays the help menu.");
            this.put("command-help-list-description", "&8 - #00B4DB/%cmd% %subcmd% %args% &7- %desc%");
            this.put("command-help-list-description-no-args", "&8 - #00B4DB/%cmd% %subcmd% &7- %desc%");

            this.put("#6", "Create Command");
            this.put("command-create-description", "Create a new parkour level.");
            this.put("command-create-success", "You have created a new parkour level called #00B4DB&l%name%&f.");
            this.put("command-create-already-exists", "A parkour level with the name [#00B4DB%name%&f] already exists.");

            this.put("#7", "Delete Command");
            this.put("command-delete-description", "Delete a parkour level.");
            this.put("command-delete-success", "You have deleted the parkour level [#00B4DB%name%&f].");
            this.put("command-delete-not-found", "A parkour level with the name [#00B4DB%name%&f] does not exist.");

            this.put("#8", "Edit Command");
            this.put("command-edit-description", "Edit a parkour level.");
            this.put("command-edit-success", "You have left editing level editing mode.");

            this.put("#9", "Edit Command - Add Finish Command");
            this.put("command-edit-add-finish-start", "Now editing the finish region of [#00B4DB%name%&f].");

            this.put("#10", "Edit Command - Add Level Region Command");
            this.put("command-edit-add-region-start", "Now editing the level region of [#00B4DB%name%&f].");

            this.put("#11", "Edit Command - Add Start Command");
            this.put("command-edit-add-start-start", "Now editing the start region of [#00B4DB%name%&f].");

            this.put("#12", "Edit Command - Checkpoint Command");
            this.put("command-edit-checkpoint-start", "Now editing the checkpoint region of [#00B4DB%name%&f].");

            this.put("#13", "Edit Command - Delete Finish Command");
            this.put("command-edit-del-finish-success", "You have deleted the finish region of [#00B4DB%name%&f].");

            this.put("#14", "Edit Command - Delete Level Region Command");
            this.put("command-edit-del-region-success", "You have deleted the level region of [#00B4DB%name%&f].");

            this.put("#15", "Edit Command - Delete Start Command");
            this.put("command-edit-del-start-success", "You have deleted the start region of [#00B4DB%name%&f].");

            this.put("#16", "Edit Command - Teleport Location Command");
            this.put("command-edit-teleport-success", "You have successfully changed teleport location of [#00B4DB%name%&f] to [#00B4DB%location%&f].");

            this.put("#17", "Edit Command - View Command");
            this.put("command-edit-view-start", "Now viewing the level [#00B4DB%name%&f].");

            this.put("#18", "List Command");
            this.put("command-list-description", "List all parkour levels.");
            this.put("command-list-title", "&fAvailable Parkour Levels:");
            this.put("command-list-format", "&8 - #00B4DB%name% &7- %desc%");

            this.put("#19", "General Parkour Messages");
            this.put("region-not-found", "You are not in the correct region to do this.");

        }};
    }

}
