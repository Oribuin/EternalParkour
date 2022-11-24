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
            this.put("command-edit-not-found", "A parkour level with the name [#00B4DB%name%&f] does not exist.");
            this.put("command-edit-success", "You have entered edit mode for the parkour level [#00B4DB%name%&f].");

        }};
    }

}
