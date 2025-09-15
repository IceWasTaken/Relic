package net.ice.relic.common.generation.biome;

import net.ice.relic.common.generation.Biome;
import net.ice.relic.common.noise.FastNoiseLite;

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
