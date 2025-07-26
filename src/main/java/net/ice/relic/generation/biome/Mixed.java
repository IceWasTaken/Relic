package net.ice.relic.generation.biome;

import net.ice.relic.generation.Biome;
import net.ice.relic.noise.FastNoiseLite;

public class Mixed<T extends Biome, R extends Biome> extends Biome {

    protected Mixed(int temperature) {
        super(temperature);
    }

    @Override
    public boolean isNeighborBiomeEligible(Biome biome) {
        return false;
    }

    @Override
    public FastNoiseLite setupGenerator() {
        return null;
    }
}
