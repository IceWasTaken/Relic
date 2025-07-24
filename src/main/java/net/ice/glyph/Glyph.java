package net.ice.glyph;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.modding.IMod;
import net.ice.relic.modding.ModData;

public class Glyph implements IMod {

    @Override
    public void init(RelicApplication application) {

    }

    @Override
    public ModData getModData() {
        ModData modData = new ModData();
        modData.setName("Glyph");
        modData.setAuthor("Ice");
        modData.setDescription("Test mod.");
        modData.setModVersion(1, 0, 0);
        modData.setEngineVersion(0, 3, 0);
        modData.setApplicationVersion(0, 1, 0);

        return modData;
    }
}
