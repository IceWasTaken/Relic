package net.ice.relic.engine;

import net.ice.relic.engine.common.Clock;
import net.ice.relic.engine.opengl.rendering.Renderer;

public interface ApplicationContext {

    Window getWindow();

    Renderer getRenderer();

    Clock getClock();

    //Stats getStats();




}
