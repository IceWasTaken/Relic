package net.ice.relic.modding;

import net.ice.relic.engine.RelicApplication;

import java.util.ArrayList;
import java.util.List;

public class ModManager {

    private final List<Mod> mods;
    private final RelicApplication application;

    public ModManager(RelicApplication application) {
        this.application = application;

        mods = new ArrayList<>();
    }
}
