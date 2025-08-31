package net.ice.relic.core.config.configs;

import net.ice.relic.core.config.ConfigBase;
import net.ice.relic.core.config.enums.GraphicsQuality;

public class RendererConfig extends ConfigBase {

    private static final String defaultFileName = "default_renderer_config.properties";
    private static final String fileName = "rendererConfig.properties";

    private static final String configPath = "config/" + fileName;
    private static final String defaultConfigPath = "net/ice/relic/config/" + defaultFileName;

    private Integer maxDrawElements;
    private Integer maxSceneObjects;
    private Integer maxTextures;
    private Integer maxMaterials;
    private Integer commandSize;
    private Integer maxPointLights;
    private Integer maxSpotLights;
    private GraphicsQuality shadowQuality;
    private GraphicsQuality lightingQuality;

    public RendererConfig() {
        super(configPath, defaultConfigPath);
    }

    @Override
    public RendererConfig loadConfig() {
        maxDrawElements = getInt("max_draw_elements");
        maxSceneObjects = getInt("max_scene_objects");
        maxTextures = getInt("max_textures");
        maxMaterials = getInt("max_materials");
        commandSize = getInt("command_size");
        maxPointLights = getInt("max_point_lights");
        maxSpotLights = getInt("max_spot_lights");
        shadowQuality = getEnum("shadowQuality", GraphicsQuality.class);
        lightingQuality = getEnum("lightingQuality", GraphicsQuality.class);

        return this;
    }

    public int getMaxDrawElements() {
        return maxDrawElements;
    }
    public void setMaxDrawElements(Integer maxDrawElements) {
        this.maxDrawElements = maxDrawElements;
    }

    public int getMaxSceneObjects() {
        return maxSceneObjects;
    }
    public void setMaxSceneObjects(Integer maxSceneObjects) {
        this.maxSceneObjects = maxSceneObjects;
    }

    public int getMaxTextures() {
        return maxTextures;
    }
    public void setMaxTextures(Integer maxTextures) {
        this.maxTextures = maxTextures;
    }

    public int getMaxMaterials() {
        return maxMaterials;
    }
    public void setMaxMaterials(Integer maxMaterials) {
        this.maxMaterials = maxMaterials;
    }

    public int getCommandSize() {
        return commandSize * 4;
    }
    public void setCommandSize(Integer commandSize) {
        this.commandSize = commandSize;
    }

    public int getMaxPointLights() {
        return maxPointLights;
    }
    public void setMaxPointLights(Integer maxPointLights) {
        this.maxPointLights = maxPointLights;
    }

    public int getMaxSpotLights() {
        return maxSpotLights;
    }
    public void setMaxSpotLights(Integer maxSpotLights) {
        this.maxSpotLights = maxSpotLights;
    }

    public GraphicsQuality getLightingQuality() {
        return lightingQuality;
    }
    public void setLightingQuality(GraphicsQuality lightingQuality) {
        this.lightingQuality = lightingQuality;
    }

    public GraphicsQuality getShadowQuality() {
        return shadowQuality;
    }
    public void setShadowQuality(GraphicsQuality shadowQuality) {
        this.shadowQuality = shadowQuality;
    }
}