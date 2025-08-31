package net.ice.talisman;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Version;
import net.ice.relic.core.config.Config;
import net.ice.talisman.scene.EditorScene;

public class Talisman extends RelicApplication {

    protected Talisman(Config config) {
        super(config, new Version(0, 1, 0));
    }

    @Override
    protected void init(RelicApplication application) {
        application.loadScene(new EditorScene("Editor", application));
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
        Talisman talisman = new Talisman(new Config());
        talisman.run();
    }
}
