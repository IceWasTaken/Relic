package net.ice.relic;

import net.ice.relic.common.Clock;
import net.ice.relic.common.Input;
import net.ice.relic.engine.opengl.rendering.renderer.Renderer;

public interface ApplicationContext {

    Window getWindow();

    Renderer getRenderer();

    Clock getClock();

    Input getInput();


    //Stats getStats();

}
