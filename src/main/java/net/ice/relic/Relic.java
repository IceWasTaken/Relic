package net.ice.relic;

import net.ice.relic.engine.Window;
import net.ice.relic.engine.common.EngineConfig;
import net.ice.relic.engine.opengl.RelicGL;

import java.io.IOException;

public class Relic {

    public static void main(String[] args) throws IOException {
        EngineConfig config = EngineConfig.getInstance();
        new Window(720,720,"", new RelicGL());
    }
}
