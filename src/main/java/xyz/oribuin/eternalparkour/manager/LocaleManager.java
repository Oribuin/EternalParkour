package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.locale.Locale;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalparkour.locale.EnglishLocale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocaleManager extends AbstractLocaleManager {

    public LocaleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Locale> getLocales() {
        return List.of(
                new EnglishLocale()
        );
    }

    /**
     * Gets a list or single locale message with the given placeholders applied, will return an empty list for no messages
     *
     * @param messageKey The key of the message to get
     * @param stringPlaceholders The placeholders to apply
     * @return The locale messages with the given placeholders applied
     */
    public List<String> getLocaleMessages(String messageKey, StringPlaceholders stringPlaceholders) {
        if (this.locale.isList(messageKey)) {
            List<String> message = this.locale.getStringList(messageKey);
            message.replaceAll(x -> HexUtils.colorify(stringPlaceholders.apply(x)));
            return message;
        } else {
            return new ArrayList<>(Collections.singletonList(this.getLocaleMessage(messageKey, stringPlaceholders)));
        }
    }

}
