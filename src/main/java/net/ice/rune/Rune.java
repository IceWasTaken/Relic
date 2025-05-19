package net.ice.rune;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.Window;
import net.ice.relic.engine.opengl.Relic;
import net.ice.relic.engine.opengl.Renderer;
import net.ice.relic.engine.opengl.scene.Scene;

public class Rune implements RelicApplication {

    @Override
    public void init(Window window, Scene scene, Renderer renderer) {

    }

    @Override
    public void update(Window window, Scene scene, Renderer renderer) {

    }

    @Override
    public void cleanup() {

    }

    public static void main(String[] args) {
        new Relic(new Rune());
    }
}
