package net.ice.relic.core.cache;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.stb.Bitmap;
import org.tinylog.Logger;
import net.ice.heirloom.io.resource.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    public static final Resource DEFAULT_TEXTURE = Resource.getResource("relic", "assets/textures/default.png");

    private final Map<Resource, Texture> textureMap;

    private final GraphicsContext graphicsContext;

    public TextureCache(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        textureMap = new HashMap<>();
    }

    public void init() {
//        textureMap.put(Resource.getResourceDefaultNamespace(DEFAULT_TEXTURE.getPath()), new GLTexture(new Image(DEFAULT_TEXTURE)));
//        textureMap.put(Resource.getResourceDefaultNamespace("grass"), new GLTexture(new Image(Resource.getResourceDefaultNamespace("assets/textures/terrain/grass/grass.png"))));
//        textureMap.put(Resource.getResourceDefaultNamespace("grass_normal"), new GLTexture(new Image(Resource.getResourceDefaultNamespace("assets/textures/terrain/grass/grass_normal.png"))));
    }

    public void cleanup() {
        textureMap.values().forEach(Texture::cleanup);
    }

    public Texture createTexture(Resource texturePath) {
        return textureMap.computeIfAbsent(texturePath, (res) -> graphicsContext.createTexture(new Bitmap(res)));
    }

    public Texture getTexture(Resource texturePath) {
        Texture texture = null;
        if(texturePath != null) {
            texture = textureMap.get(texturePath);
        }
        if(texture == null) {
            Logger.error("Texture not found in cache: [{}] - [{}]", texturePath);
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }

    public Texture getTexture(String texturePath) {
        return getTexture(Resource.getResource("relic", texturePath));
    }


    public Collection<Texture> getTextureMaps() {
        return textureMap.values();
    }

    public Map<Resource, Texture> getTextureMap() {
        return textureMap;
    }

}
