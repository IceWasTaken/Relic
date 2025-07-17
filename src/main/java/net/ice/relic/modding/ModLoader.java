package net.ice.relic.modding;

import net.ice.relic.RelicApplication;
import org.tinylog.Logger;

public class ModLoader {

    private final RelicApplication application;

    public ModLoader(RelicApplication application) {
        this.application = application;
    }

    public void loadMod(IMod mod) {
        checkVersion(mod.getModData());
        mod.init(application);
        Logger.info("Loaded mod: {}", mod.getModData().getName());
    }

    private void checkVersion(ModData modData) {
        if(!application.getEngineVersion().equals(modData.getEngineVersion()))  {
            Logger.warn("Mod '{}' is not compatible with this version of the engine. This may cause unexpected errors.", modData.getName());
        }
        if(!application.getApplicationVersion().equals(modData.getApplicationVersion()))  {
            Logger.warn("Mod '{}' is not compatible with this version of the game/app. This may cause unexpected errors.", modData.getName());
        }
    }

}
