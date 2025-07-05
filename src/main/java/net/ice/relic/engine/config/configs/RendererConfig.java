package net.ice.relic.engine.config.configs;

import net.ice.relic.engine.config.ConfigBase;

import java.nio.file.Path;

public class RendererConfig extends ConfigBase {

    private static final String defaultFileName = "default_renderer_config.properties";
    private static final String fileName = "rendererConfig.properties";

    private static final Path configPath = Path.of("config/" + fileName);
    private static final Path defaultConfigPath = Path.of("net/ice/relic/config/" + defaultFileName);

    private Integer MAX_DRAW_ELEMENTS;
    private Integer MAX_SCENE_OBJECTS;
    private Integer MAX_TEXTURES;
    private Integer MAX_MATERIALS;
    private Integer COMMAND_SIZE;
    private Integer MAX_POINT_LIGHTS;
    private Integer MAX_SPOT_LIGHTS;

    public RendererConfig() {
        super(configPath, defaultConfigPath);
    }

    @Override
    public RendererConfig loadConfig() {
        MAX_DRAW_ELEMENTS = getInt("max_draw_elements");
        MAX_SCENE_OBJECTS = getInt("max_scene_objects");
        MAX_TEXTURES = getInt("max_textures");
        MAX_MATERIALS = getInt("max_materials");
        COMMAND_SIZE = getInt("command_size");
        MAX_POINT_LIGHTS = getInt("max_point_lights");
        MAX_SPOT_LIGHTS = getInt("max_spot_lights");

        return this;
    }

    public int getMaxDrawElements() {
        return MAX_DRAW_ELEMENTS;
    }

    public void setMaxDrawElements(Integer maxDrawElements) {
        this.MAX_DRAW_ELEMENTS = maxDrawElements;
    }

    public int getMaxSceneObjects() {
        return MAX_SCENE_OBJECTS;
    }

    public void setMaxSceneObjects(Integer maxSceneObjects) {
        this.MAX_SCENE_OBJECTS = maxSceneObjects;
    }

    public int getMaxTextures() {
        return MAX_TEXTURES;
    }

    public void setMaxTextures(Integer maxTextures) {
        this.MAX_TEXTURES = maxTextures;
    }

    public int getMaxMaterials() {
        return MAX_MATERIALS;
    }

    public void setMaxMaterials(Integer maxMaterials) {
        this.MAX_MATERIALS = maxMaterials;
    }

    public int getCommandSize() {
        return COMMAND_SIZE * 4;
    }

    public void setCommandSize(Integer commandSize) {
        this.COMMAND_SIZE = commandSize;
    }

    public int getMaxPointLights() {
        return MAX_POINT_LIGHTS;
    }

    public void setMaxPointLights(Integer maxPointLights) {
        this.MAX_POINT_LIGHTS = maxPointLights;
    }

    public int getMaxSpotLights() {
        return MAX_SPOT_LIGHTS;
    }

    public void setMaxSpotLights(Integer maxSpotLights) {
        this.MAX_SPOT_LIGHTS = maxSpotLights;
    }
}