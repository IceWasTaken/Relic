package net.ice.relic.core.cache;

import net.ice.relic.core.rendering.backend.opengl.model.texture.GLTexture;
import net.ice.relic.core.resource.Resource;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    public static final Resource DEFAULT_TEXTURE = Resource.getResourceWithDefaultNamespace("assets/textures/default.png");
    private final Map<Resource, GLTexture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
    }

    public void init() {
//        textureMap.put(Resource.getResourceWithDefaultNamespace(DEFAULT_TEXTURE.getPath()), new GLTexture(new Image(DEFAULT_TEXTURE)));
//        textureMap.put(Resource.getResourceWithDefaultNamespace("grass"), new GLTexture(new Image(Resource.getResourceWithDefaultNamespace("assets/textures/terrain/grass/grass.png"))));
//        textureMap.put(Resource.getResourceWithDefaultNamespace("grass_normal"), new GLTexture(new Image(Resource.getResourceWithDefaultNamespace("assets/textures/terrain/grass/grass_normal.png"))));
    }

    public void cleanup() {
        textureMap.values().forEach(GLTexture::cleanup);
    }

    public GLTexture createTexture(Resource texturePath) {
        return textureMap.computeIfAbsent(texturePath, GLTexture::new);
    }

    public GLTexture createTexture(ByteBuffer data) {
        return new GLTexture(data);
    }



    public GLTexture getTexture(String texturePath) {
        GLTexture texture = null;
        if(texturePath != null) {
            texture = textureMap.get(Resource.getResourceWithDefaultNamespace(texturePath));
        }
        if(texture == null) {
            Logger.error("Texture not found in cache: [{}] - [{}]", texturePath);
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }

    public Collection<GLTexture> getTextures() {
        return textureMap.values();
    }

    public Map<Resource, GLTexture> getTextureMap() {
        return textureMap;
    }
}
