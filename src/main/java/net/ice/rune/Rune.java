package net.ice.rune;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Version;
import net.ice.relic.core.config.Config;
import net.ice.rune.scenes.SceneTest;

public class Rune extends RelicApplication {

    public Rune(Config config) {
        super(config, new Version(0, 1, 0));
        config.getWindowConfig().setFullscreen(false);
    }

    @Override
    protected void init(RelicApplication application) {
        modManager.loadMods();

        application.loadScene(new SceneTest("test", application));
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
