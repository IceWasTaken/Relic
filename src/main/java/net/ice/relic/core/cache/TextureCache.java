package net.ice.relic.core.cache;

import net.ice.relic.common.asset.image.Image;
import net.ice.relic.core.rendering.backend.opengl.model.texture.GLTexture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    public static final String DEFAULT_TEXTURE = "resources/textures/default.png";
    private final Map<String, GLTexture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
    }

    public void init() {
        textureMap.put(DEFAULT_TEXTURE, new GLTexture(new Image(DEFAULT_TEXTURE)));
        textureMap.put("grass", new GLTexture(new Image("resources/textures/terrain/grass/grass.png")));
        textureMap.put("grass_normal", new GLTexture(new Image("resources/textures/terrain/grass/grass_normal.png")));
    }

    public void cleanup() {
        textureMap.values().forEach(GLTexture::cleanup);
    }

    public GLTexture createTexture(String texturePath) {
        return textureMap.computeIfAbsent(texturePath, GLTexture::new);
    }

    public GLTexture getTexture(String texturePath) {
        GLTexture texture = null;
        if(texturePath != null) {
            texture = textureMap.get(texturePath);
        }
        if(texture == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }

    public Collection<GLTexture> getTextures() {
        return textureMap.values();
    }

    public Map<String, GLTexture> getTextureMap() {
        return textureMap;
    }
}
