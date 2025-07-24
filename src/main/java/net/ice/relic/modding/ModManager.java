package net.ice.relic.modding;

import net.ice.glyph.Glyph;
import net.ice.relic.application.RelicApplication;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class ModManager {

    private final List<IMod> mods;
    private final ModLoader modLoader;
    private final RelicApplication application;

    public ModManager(RelicApplication application) {
        this.application = application;
        this.modLoader = new ModLoader(application);

        mods = new ArrayList<>();
        mods.add(new Glyph());
    }

    public void loadMods() {
        for(IMod mod : mods) {
            Logger.info("Loading mod: {}", mod.getModData().getName());
            modLoader.loadMod(mod);
        }
    }
}
