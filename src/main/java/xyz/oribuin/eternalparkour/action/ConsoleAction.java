package xyz.oribuin.eternalparkour.action;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.hook.PAPI;

public class ConsoleAction extends Action {

    public ConsoleAction() {
        super("console");
    }

    @Override
    public void execute(@NotNull Player player, @NotNull StringPlaceholders placeholders) {
        if (this.getMessage().length() == 0)
            return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HexUtils.colorify(PAPI.apply(player, placeholders.apply(this.getMessage()))));
    }

}
