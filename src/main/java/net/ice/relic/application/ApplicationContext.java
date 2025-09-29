package net.ice.relic.application;

import net.ice.relic.core.Clock;
import net.ice.relic.core.Input;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.window.Window;

public interface ApplicationContext {

    Window getWindow();

    Renderer getRenderer();

    Clock getClock();

    Input getInput();


    //Stats getStats();

}
