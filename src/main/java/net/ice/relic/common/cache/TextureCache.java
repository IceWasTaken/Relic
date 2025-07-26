package net.ice.relic.common.cache;

import net.ice.relic.engine.opengl.model.texture.Texture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    public static final String DEFAULT_TEXTURE = "resources/textures/default.png";
    private Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
        textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
        textureMap.put("grass", new Texture("resources/textures/terrain/grass/grass.png"));
        textureMap.put("grass_normal", new Texture("resources/textures/terrain/grass/grass_normal.png"));
    }

    public void cleanup() {
        textureMap.values().forEach(Texture::cleanup);
    }

    public Texture createTexture(String texturePath) {
        return textureMap.computeIfAbsent(texturePath, Texture::new);
    }

    public Texture getTexture(String texturePath) {
        Texture texture = null;
        if(texturePath != null) {
            texture = textureMap.get(texturePath);
        }
        if(texture == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }

    public Collection<Texture> getTextures() {
        return textureMap.values();
    }

    public Map<String, Texture> getTextureMap() {
        return textureMap;
    }
}
