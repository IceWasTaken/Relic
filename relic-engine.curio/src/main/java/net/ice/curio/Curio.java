package net.ice.curio;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.window.Window;
import net.ice.heirloom.ApplicationProperties;
import net.ice.heirloom.Lifecycle;

public class Curio implements Lifecycle {

    private final Window window;
    private final GraphicsContext graphicsContext;

    private final ApplicationProperties applicationProperties;

    public Curio(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;

        this.window = Window.getBackend();
        this.graphicsContext = GraphicsContext.getGraphicsContext(this);
    }

    @Override
    public void init() {
        window.init();
        graphicsContext.init();
    }

    public Window getWindow() {
        return window;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }
}
