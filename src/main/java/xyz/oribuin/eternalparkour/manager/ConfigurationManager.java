package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import org.bukkit.Color;
import xyz.oribuin.eternalparkour.EternalParkour;
import xyz.oribuin.eternalparkour.util.PluginUtils;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        // Leaderboard Settings
        LEADERBOARD_MAX_SIZE("leaderboard.max-size", 10, "The amount of players to show on the leaderboard."),
        LEADERBOARD_AUTO_UPDATE("leaderboard.auto-update", true, "Should the leaderboard update automatically?"),
        LEADERBOARD_UPDATE_INTERVAL("leaderboard.update-interval", 12000, "The amount of ticks between leaderboard updates. (20 ticks = 1 second)"),
        LEADERBOARD_PLAYERS_PER_PAGE("leaderboard.players-per-page", 10, "The amount of players to show per page on the leaderboard."),
        LEADERBOARD_DATE_FORMAT("leaderboard.date-format", "dd/MMM/yyyy HH:mm:ss", "The date format to use for the leaderboard & stats."),

        // Active running settings
        RUNNER_TIME_ENABLED("runner-timer.enabled", true, "Should the current time be shown on the runner's HUD?"),
        RUNNER_TIMER_INTERVAL("runner-timer.update-interval", 1, "The amount of ticks between runner timer updates (Actionbar Message)."),
        RUNNER_TIME_MESSAGE("runner-timer.message", "<gradient:#00B4DB:#0083B0>Time <gray>- <white>%time%", "The message to display in the actionbar for the runner timer."),

        // Editor Task Settings
        EDITOR_TASK_ENABLED("editor-task.enabled", true, "Should the editor task be enabled?", "This task will show the player visual indicators for the level."),
        EDITOR_TASK_INTERVAL("editor-task.update-interval", 5, "The amount of ticks between editor task updates."),
        EDITOR_TASK_START_COLOR("editor-task.start-region-color", "#32CD32", "The color of the start region."),
        EDITOR_TASK_FINISH_COLOR("editor-task.finish-region-color", "#FF0000", "The color of the end region."),
        EDITOR_TASK_CHECKPOINT_COLOR("editor-task.checkpoint-region-color", "#FFFF00", "The color of the checkpoint region."),
        EDITOR_TASK_LEVEL_COLOR("editor-task.level-region-color", "#6495ED", "The color of the parkour region."),
        ;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return EternalParkour.getInstance().getManager(ConfigurationManager.class).getConfig();
        }

        public Color getColor() {
            return PluginUtils.getColor(this.getString());
        }

    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }


    @Override
    protected String[] getHeader() {
        return new String[]{
                "___________ __                             .__ __________               __                       ",
                "\\_   _____//  |_  ___________  ____ _____  |  |\\______   \\_____ _______|  | ______  __ _________ ",
                " |    __)_\\   __\\/ __ \\_  __ \\/    \\\\__  \\ |  | |     ___/\\__  \\\\_  __ \\  |/ /  _ \\|  |  \\_  __ \\",
                " |        \\|  | \\  ___/|  | \\/   |  \\/ __ \\|  |_|    |     / __ \\|  | \\/    <  <_> )  |  /|  | \\/",
                "/_______  /|__|  \\___  >__|  |___|  (____  /____/____|    (____  /__|  |__|_ \\____/|____/ |__|   ",
                "        \\/           \\/           \\/     \\/                    \\/           \\/                   "
        };
    }
}
