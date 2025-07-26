package net.ice.relic.generation;

import net.ice.relic.common.model.MeshData;
import net.ice.relic.common.scene.SceneObject;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private final ChunkPosition position;
    private final List<Biome> biomes;
    private final List<SceneObject> sceneObjects;
    private final MeshData chunkMeshData;

    public Chunk(float x, float y, float z, MeshData meshData) {
        this.position = new ChunkPosition(x, y, z);
        this.biomes = new ArrayList<>();
        this.sceneObjects = new ArrayList<>();
        this.chunkMeshData = meshData;
    }

    public Chunk(ChunkPosition position, MeshData chunkMeshData) {
        this.position = position;
        this.biomes = new ArrayList<>();
        this.sceneObjects = new ArrayList<>();
        this.chunkMeshData = chunkMeshData;
    }

    public ChunkPosition getPosition() {
        return position;
    }

    public List<Biome> getBiomes() {
        return biomes;
    }

    public List<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    public MeshData getChunkMeshData() {
        return chunkMeshData;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Chunk chunk) {
            return chunk.getPosition() == this.getPosition();
        }
        return false;
    }
}
