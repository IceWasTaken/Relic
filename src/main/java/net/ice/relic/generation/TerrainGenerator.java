package net.ice.relic.generation;

import net.ice.relic.noise.FastNoiseLite;

public class TerrainGenerator {

    static class Desert extends Biome {

        public Desert() {
            super(105);
        }

        @Override
        public boolean isNeighborBiomeEligible(Biome biome) {
            return biome.getTemperature() > 60;
        }

        @Override
        public FastNoiseLite setupGenerator() {
            FastNoiseLite noiseGenerator = new FastNoiseLite(5673);
            noiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            noiseGenerator.SetFrequency(-0.1f);

//            noiseGenerator.SetFractalType(FastNoiseLite.FractalType.PingPong);
//            noiseGenerator.SetFractalOctaves(3);
//            noiseGenerator.SetFractalLacunarity(1.2f);
//            noiseGenerator.SetFractalGain(0.5f);
//            noiseGenerator.SetFractalWeightedStrength(0.5f);
//            noiseGenerator.SetFractalPingPongStrength(2);

            return noiseGenerator;
        }
    }
}

