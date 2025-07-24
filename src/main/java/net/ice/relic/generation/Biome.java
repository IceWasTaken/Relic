package net.ice.relic.generation;

import net.ice.relic.noise.FastNoiseLite;

public abstract class Biome {

    private FastNoiseLite noiseGenerator;

    private int temperature;

    protected Biome(int temperature) {

    }

    public abstract boolean isNeighborBiomeEligible(Biome biome);
    public abstract FastNoiseLite setupGenerator();



}

