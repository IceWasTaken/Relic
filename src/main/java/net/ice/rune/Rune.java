package net.ice.rune;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.Version;
import net.ice.relic.config.Config;

public class Rune extends RelicApplication {

    public Rune(Config config) {
        super(config, new Version(0, 1, 0));
        config.getWindowConfig().setFullscreen(false);
    }

    @Override
    protected void init(RelicApplication application) {
        modManager.loadMods();
    }

    @Override
    protected void update(RelicApplication application) {

    }

    @Override
    protected void render(RelicApplication application) {

    }

    @Override
    protected void cleanup(RelicApplication application) {

    }

    public static void main(String[] args) {
        Rune rune = new Rune(new Config());
        rune.run();
    }
}
