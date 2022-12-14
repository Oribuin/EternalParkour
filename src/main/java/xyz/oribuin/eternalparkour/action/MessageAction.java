package xyz.oribuin.eternalparkour.action;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalparkour.hook.PAPI;

public class MessageAction extends Action {

    public MessageAction() {
        super("message");
    }

    @Override
    public void execute(@NotNull Player player, @NotNull StringPlaceholders placeholders) {
        if (this.getMessage().length() == 0)
            return;

        player.sendMessage(HexUtils.colorify(PAPI.apply(player, placeholders.apply(this.getMessage()))));
    }
}
