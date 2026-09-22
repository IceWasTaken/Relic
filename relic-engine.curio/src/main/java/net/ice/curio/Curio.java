package net.ice.curio;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.window.Window;
import net.ice.heirloom.application.Application;
import net.ice.heirloom.application.ApplicationProperties;
import net.ice.heirloom.Lifecycle;

public class Curio implements Lifecycle {

    private final Window window;
    private final GraphicsContext graphicsContext;

    private final Application application;

    public Curio(Application application) {
        this.application = application;

        this.window = Window.getWindowContext(this);
        this.graphicsContext = GraphicsContext.getGraphicsContext(this);
    }

    @Override
    public void init() {
        graphicsContext.init();
    }

    public Window getWindow() {
        return window;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public ApplicationProperties getApplicationProperties() {
        return application.getProperties();
    }
}
