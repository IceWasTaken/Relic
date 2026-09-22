package net.ice.curio.config;

import net.ice.curio.config.enums.BackendType;
import net.ice.curio.config.enums.GraphicsQuality;
import net.ice.curio.config.enums.WindowBackend;
import net.ice.heirloom.config.ConfigBase;

public class RendererConfig extends ConfigBase {

    private static RendererConfig instance;

    private static final String fileName = "rendererConfig.properties";

    private static Integer maxDrawElements = 500;
    private static Integer maxSceneObjects = 100;
    private static Integer maxTextures = 200;
    private static Integer maxMaterials = 200;
    private static Integer commandSize = 5;
    private static Integer maxPointLights = 5;
    private static Integer maxSpotLights = 5;
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
        maxDrawElements = getInt("max_draw_elements");
        maxSceneObjects = getInt("max_scene_objects");
        maxTextures = getInt("max_textures");
        maxMaterials = getInt("max_materials");
        commandSize = getInt("command_size");
        maxPointLights = getInt("max_point_lights");
        maxSpotLights = getInt("max_spot_lights");
        shadowQuality = getEnum("shadowQuality", GraphicsQuality.class);
        lightingQuality = getEnum("lightingQuality", GraphicsQuality.class);
        backendType = getEnum("backendType", BackendType.class);
    }

    public static int getMaxDrawElements() {
        return maxDrawElements;
    }
    public static void setMaxDrawElements(Integer value) {
        maxDrawElements = value;
    }

    public static int getMaxSceneObjects() {
        return maxSceneObjects;
    }
    public static void setMaxSceneObjects(Integer value) {
        maxSceneObjects = value;
    }

    public static int getMaxTextures() {
        return maxTextures;
    }
    public static void setMaxTextures(Integer value) {
        maxTextures = value;
    }

    public static int getMaxMaterials() {
        return maxMaterials;
    }
    public static void setMaxMaterials(Integer value) {
        maxMaterials = value;
    }

    public static int getCommandSize() {
        return commandSize * 4;
    }
    public static void setCommandSize(Integer value) {
        commandSize = value;
    }

    public static int getMaxPointLights() {
        return maxPointLights;
    }
    public static void setMaxPointLights(Integer value) {
        maxPointLights = value;
    }

    public static int getMaxSpotLights() {
        return maxSpotLights;
    }
    public static void setMaxSpotLights(Integer value) {
        maxSpotLights = value;
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