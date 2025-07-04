package net.ice.rune;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.config.Config;
import net.ice.relic.engine.opengl.rendering.Renderer;

public class Rune extends RelicApplication {

    public Rune(Config config) {
        super(config);
        config.getWindowConfig().setFullscreen(false);

    }

    @Override
    protected void init(RelicApplication application, Config config) {
        //application.loadScene(new SceneTest("test"));

    }

    @Override
    protected void update(RelicApplication application, float deltaTime) {
        application.getCurrentScene().getObjects().forEach(((s, sceneObject) -> sceneObject.update()));
    }

    @Override
    protected void render(RelicApplication application, Renderer renderer) {

    }

    @Override
    protected void cleanup(RelicApplication application) {

    }

    public static void main(String[] args) {
        Rune rune = new Rune(new Config());
        rune.run();
    }
}
