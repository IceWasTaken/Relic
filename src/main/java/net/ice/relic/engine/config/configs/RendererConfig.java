package net.ice.relic.engine.config.configs;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.config.ConfigBase;
import net.ice.relic.engine.config.ConfigEntry;
import java.nio.file.Path;

@Rewrite
public class RendererConfig extends ConfigBase {

    private static final String defaultFileName = "default_renderer_config.properties";
    private static final String fileName = "rendererConfig.properties";

    private static final Path configPath = Path.of("config/" + fileName);
    private static final Path defaultConfigPath = Path.of("net/ice/relic/config/" + defaultFileName);

    private ConfigEntry<Integer> MAX_DRAW_ELEMENTS;
    private ConfigEntry<Integer> MAX_SCENE_OBJECTS;
    private ConfigEntry<Integer> MAX_TEXTURES;
    private ConfigEntry<Integer> MAX_MATERIALS;
    private ConfigEntry<Integer> COMMAND_SIZE;
    private ConfigEntry<Integer> MAX_POINT_LIGHTS;
    private ConfigEntry<Integer> MAX_SPOT_LIGHTS;

    public RendererConfig() {
        super(configPath, defaultConfigPath);
    }

    @Override
    public RendererConfig loadConfig() {
        writeConfigToFileAndSave(configPath);
        MAX_DRAW_ELEMENTS = getInt("max_draw_elements");
        MAX_SCENE_OBJECTS = getInt("max_scene_objects");
        MAX_TEXTURES = getInt("max_textures");
        MAX_MATERIALS = getInt("max_materials");
        COMMAND_SIZE = getInt("command_size");
        MAX_POINT_LIGHTS = getInt("max_point_lights");
        MAX_SPOT_LIGHTS = getInt("max_spot_lights");

        writeConfigToFileAndSave(configPath);
        
        return this;
    }

    public int getMaxDrawElements() {
        return MAX_DRAW_ELEMENTS.getValue();
    }

    public void setMaxDrawElements(ConfigEntry<Integer> maxDrawElements) {
        this.MAX_DRAW_ELEMENTS = maxDrawElements;
    }

    public int getMaxSceneObjects() {
        return MAX_SCENE_OBJECTS.getValue();
    }

    public void setMaxSceneObjects(ConfigEntry<Integer> maxSceneObjects) {
        this.MAX_SCENE_OBJECTS = maxSceneObjects;
    }

    public int getMaxTextures() {
        return MAX_TEXTURES.getValue();
    }

    public void setMaxTextures(ConfigEntry<Integer> maxTextures) {
        this.MAX_TEXTURES = maxTextures;
    }

    public int getMaxMaterials() {
        return MAX_MATERIALS.getValue();
    }

    public void setMaxMaterials(ConfigEntry<Integer> maxMaterials) {
        this.MAX_MATERIALS = maxMaterials;
    }

    public int getCommandSize() {
        return COMMAND_SIZE.getValue() * 4;
    }

    public void setCommandSize(ConfigEntry<Integer> commandSize) {
        this.COMMAND_SIZE = commandSize;
    }

    public int getMaxPointLights() {
        return MAX_POINT_LIGHTS.getValue();
    }

    public void setMaxPointLights(ConfigEntry<Integer> maxPointLights) {
        this.MAX_POINT_LIGHTS = maxPointLights;
    }

    public int getMaxSpotLights() {
        return MAX_SPOT_LIGHTS.getValue();
    }

    public void setMaxSpotLights(ConfigEntry<Integer> maxSpotLights) {
        this.MAX_SPOT_LIGHTS = maxSpotLights;
    }
}