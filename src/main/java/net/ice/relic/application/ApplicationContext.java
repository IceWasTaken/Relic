package net.ice.relic.application;

import net.ice.relic.Window;
import net.ice.relic.core.Clock;
import net.ice.relic.core.Input;
import net.ice.relic.engine.opengl.rendering.renderer.Renderer;

public interface ApplicationContext {

    Window getWindow();

    Renderer getRenderer();

    Clock getClock();

    Input getInput();


    //Stats getStats();

}
