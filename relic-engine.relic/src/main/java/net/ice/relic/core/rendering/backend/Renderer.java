package net.ice.relic.core.rendering.backend;

import net.ice.curio.config.RendererConfig;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.vulkan.VulkanRenderer;

public abstract class Renderer implements Lifecycle {

    private static Renderer instance;

    public abstract void resize(int width, int height);

    protected final RelicApplication application;

    protected Renderer(RelicApplication relicApplication) {
        this.application = relicApplication;
    }

    public static Renderer getRendererType(RelicApplication application) {
        if(instance != null) {
            return instance;
        } else {
            instance = switch(RendererConfig.getBackendType()) {
                case VULKAN -> new VulkanRenderer(application);
                case OPENGL -> new GLRenderer(application);
            };
        }
        return getRendererType(application);
    }

    public static Renderer getInstance() {
        return instance;
    }

}
