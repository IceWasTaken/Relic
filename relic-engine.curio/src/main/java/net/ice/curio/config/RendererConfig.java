package net.ice.curio.config;

import net.ice.curio.config.enums.BackendType;
import net.ice.curio.config.enums.GraphicsQuality;
import net.ice.curio.config.enums.WindowBackend;
import net.ice.heirloom.config.ConfigBase;

public class RendererConfig extends ConfigBase {

    private static RendererConfig instance;

    private static final String fileName = "rendererConfig.properties";

    private static GraphicsQuality shadowQuality = GraphicsQuality.LOW;
    private static GraphicsQuality lightingQuality = GraphicsQuality.LOW;
    private static BackendType backendType = BackendType.OPENGL;
    private static WindowBackend windowBackend = WindowBackend.SDL;

    public RendererConfig() {
        super(fileName);
    }

    public static RendererConfig getInstance() {
        return instance == null ? instance = new RendererConfig() : instance;
    }

    @Override
    public void loadConfig() {
        shadowQuality = getEnum("shadowQuality", GraphicsQuality.class);
        lightingQuality = getEnum("lightingQuality", GraphicsQuality.class);
        backendType = getEnum("backendType", BackendType.class);
        windowBackend = getEnum("windowBackend", WindowBackend.class);
    }


    public static GraphicsQuality getLightingQuality() {
        return lightingQuality;
    }
    public static void setLightingQuality(GraphicsQuality value) {
        lightingQuality = value;
    }

    public static GraphicsQuality getShadowQuality() {
        return shadowQuality;
    }
    public static void setShadowQuality(GraphicsQuality value) {
        shadowQuality = value;
    }

    public static BackendType getBackendType() {
        return backendType;
    }
    public static void setBackendType(BackendType value) {
        backendType = value;
    }

    public static WindowBackend getWindowBackend() {
        return windowBackend;
    }
    public static void setWindowBackend(WindowBackend windowBackend) {
        RendererConfig.windowBackend = windowBackend;
    }
}