package net.ice.relic.common.generation;

import net.ice.relic.common.noise.FastNoiseLite;

public abstract class Biome {

    private int temperature;

    public abstract boolean isNeighborBiomeEligible(Biome biome);
    public abstract FastNoiseLite setupGenerator();

    protected Biome(int temperature) {

    }

    public int getTemperature() {
        return temperature;
    }

}

