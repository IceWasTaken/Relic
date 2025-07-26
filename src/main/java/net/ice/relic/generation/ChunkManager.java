package net.ice.relic.generation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkManager {
    private final Map<ChunkPosition, Chunk> loadedChunks;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ChunkManager() {
        loadedChunks = new ConcurrentHashMap<>();
    }

//    public void enqueueLoad(ChunkPosition chunkPosition) {
//        executorService.submit(() -> {
//            Chunk chunk = TerrainGenerator;
//            synchronized (loadedChunks) {
//                loadedChunks.put(chunkPosition, chunk);
//            }
//        });
//    }
}
