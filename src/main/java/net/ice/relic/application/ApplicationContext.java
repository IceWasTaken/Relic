package net.ice.relic.application;

import net.ice.curio.window.Window;
import net.ice.relic.core.Timer;
import net.ice.relic.core.rendering.backend.Renderer;

public interface ApplicationContext {

    Window getWindow();

    Renderer getRenderer();

    Timer getClock();


    //Stats getStats();
}
