package net.ice.relic.generation;

import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.model.MeshData;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.noise.FastNoiseLite;
import net.ice.relic.util.NoiseUtil;

import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private final int chunkSize = 128;

    private Texture texture;
    private Texture normalTexture;
    private Material material;
    private Model model;

    private MeshData meshData;

    public Terrain(int seed, MaterialCache materialCache, TextureCache textureCache, ModelCache modelCache) {
        FastNoiseLite noise = new FastNoiseLite();

        MeshData meshData = NoiseUtil.createMeshData(2048, noise, 8);

        List<MeshData> meshDataList = new ArrayList<>();
        meshDataList.add(meshData);

        Material material = new Material();
        material.setMaterialIndex(3);


        Texture texture1 = textureCache.createTexture("resources/textures/terrain/grass/" + "grass.png");
        Texture texture2 = textureCache.createTexture("resources/textures/terrain/grass/" + "grass_normal.png");

        material.setTexturePath(texture1.getTexturePath());
        material.setNormalMapPath(texture2.getTexturePath());

        material.setDiffuseColor(Material.DEFAULT_COLOR);


        material.setTexture(texture1);
        material.setNormalMap(texture2);


        this.material = material;
        this.model = new Model("terrain", meshDataList, null);

        materialCache.addMaterial(material);
        modelCache.addModel(model);
    }



    public Model getModel() {
        return model;
    }

    public void loadChunk() {

    }

    public void getChunk(ChunkPosition position) {


    }
}
