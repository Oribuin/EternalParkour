package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.eternalparkour.EternalParkour;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        // Leaderboard Settings
        LEADERBOARD_MAX_SIZE("leaderboard-max-size", 10, "The amount of players to show on the leaderboard."),
        LEADERBOARD_UPDATE_INTERVAL("leaderboard-update-interval", 12000, "The amount of ticks between leaderboard updates. (20 ticks = 1 second)"),

        // Active running settings
        RUNNER_TIME_ENABLED("runner-time.enabled", true, "Should the current time be shown on the runner's HUD?"),
        RUNNER_TIMER_INTERVAL("runner-timer.interval", 3, "The amount of ticks between runner timer updates (Actionbar Message)."),
        RUNNER_TIMER_USE_MINIMESSAGE("runner-timer.use-minimessage", true, "Use MiniMessage for the runner timer actionbar message. (Requires PaperMC)", "https://docs.adventure.kyori.net/minimessage/index.html"),
        RUNNER_TIME_MESSAGE("runner-time.message", "<gradient:#00B4DB:#0083B0>%time%</gradient>", "The message to display in the actionbar for the runner timer."),

        // Editor Task Settings
        EDITOR_TASK_ENABLED("editor-task.enabled", true, "Should the editor task be enabled?", "This task will show the player visual indicators for the level."),
        EDITOR_TASK_INTERVAL("editor-task.interval", 5, "The amount of ticks between editor task updates."),
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
