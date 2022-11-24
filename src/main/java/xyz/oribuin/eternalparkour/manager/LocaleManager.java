package xyz.oribuin.eternalparkour.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.locale.Locale;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import xyz.oribuin.eternalparkour.locale.EnglishLocale;

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

}
