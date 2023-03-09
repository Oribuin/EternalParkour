package xyz.oribuin.eternalparkour.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.LinkedHashMap;
import java.util.List;
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
            this.put("command-edit-checkpoints-start", "Now editing the checkpoint region of [#00B4DB%name%&f].");

            this.put("#13", "Edit Command - Delete Finish Command");
            this.put("command-edit-del-finish-success", "You have deleted the finish region of [#00B4DB%name%&f].");

            this.put("#14", "Edit Command - Delete Level Region Command");
            this.put("command-edit-del-region-success", "You have deleted the level region of [#00B4DB%name%&f].");

            this.put("#15", "Edit Command - Delete Start Command");
            this.put("command-edit-del-start-success", "You have deleted the start region of [#00B4DB%name%&f].");

            this.put("#16", "Edit Command - Teleport Location Command");
            this.put("command-edit-teleport-success", "You have successfully changed teleport location of [#00B4DB%name%&f] to [#00B4DB%location%&f].");

            this.put("#17", "Edit Command - View Command");
            this.put("command-edit-view-success", "Now viewing the level [#00B4DB%name%&f].");

            this.put("#18", "Leaderboard Command");
            this.put("command-leaderboard-description", "Show the leaderboard for a level");
            this.put("command-leaderboard-header", "&f&lTop 10 Players for #00B4DB%level%");
            this.put("command-leaderboard-entry", List.of(
                    "#00B4DB&l[Rank] &7- &f#%rank% &7| #00B4DB&l[Player] &7- &f%player%",
                    "#00B4DB&l[Time] &7- &f%time% &7| #00B4DB&l[Date] &7- &f%completed%",
                    " "
            ));

            this.put("19", "Stats Command");
            this.put("command-stats-description", "Show your stats for a level");
            this.put("command-stats-header", "&f&lStats for #00B4DB%level%");
            this.put("command-stats-body", List.of(
                    "#00B4DB&l[Rank] &7- &f#%rank% &7| #00B4DB&l[Time] &7- &f%time%",
                    "#00B4DB&l[Completions] &7- &f%completed% &7| #00B4DB&l[Attempts] &7- &f%attempts%",
                    " "
            ));

            this.put("#20", "Admin Command");
            this.put("command-admin-description", "Moderate the user data.");
            this.put("command-admin-usage", "&cUsage: /pk admin <resetuser|resetlevel> <level> [player]");

            this.put("#21", "Admin Command - Reset Level");
            this.put("command-admin-resetlevel-description", "Reset all player data for a level.");
            this.put("command-admin-resetlevel-success", "You have reset all player data for [#00B4DB%level%&f].");

            this.put("#22", "Admin Command - Reset User");
            this.put("command-admin-resetuser-description", "Reset a player's data for a level.");
            this.put("command-admin-resetuser-all", "You have reset all player data for [#00B4DB%player%&f].");
            this.put("command-admin-resetuser-level", "You have reset [#00B4DB%level%&f] data for [#00B4DB%player%&f].");

            this.put("#23", "Restart Command");
            this.put("command-restart-description", "Restart a level.");
            this.put("command-restart-not-playing", "You are not currently playing a level.");
            this.put("command-restart-success", "You have restarted the level [#00B4DB%level%&f].");


            this.put("#", "General Parkour Messages");
            this.put("region-not-found", "You are not in the correct region to do this.");
            this.put("parkour-finish", "You have finished the parkour level [#00B4DB%level%&f] in #00B4DB%time%&f.");
            this.put("parkour-finish-new-best", "You have beat your person best for [#00B4DB%level%&f] in #00B4DB%time%&f! Your previous best was #00B4DB%best%&f.");
            this.put("parkour-max-attempts", "You have reached the maximum number of attempts for [#00B4DB%level%&f] (%attempts%/%max_attempts%).");
            this.put("parkour-max-completions", "You have reached the maximum number of completions for [#00B4DB%level%&f] (%completions%/%max_completions%).");

            this.put("#", "Argument Handler Messages");
            this.put("argument-handler-level", "Parkour Level [%input%] doesn't exist.");
            this.put("argument-handler-player", "Player doesn't exist.");
        }};
    }

}
