package net.ice.relic.generation;

import net.ice.relic.common.scene.SceneObject;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private final ChunkPosition position;
    private final List<Biome> biomes;
    private final List<SceneObject> sceneObjects;

    public Chunk(float x, float y, float z) {
        this.position = new ChunkPosition(x, y, z);
        this.biomes = new ArrayList<>();
        this.sceneObjects = new ArrayList<>();
    }

    public Chunk(ChunkPosition position) {
        this.position = position;
        this.biomes = new ArrayList<>();
        this.sceneObjects = new ArrayList<>();
    }
}
