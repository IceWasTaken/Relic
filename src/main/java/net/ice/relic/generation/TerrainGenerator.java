package net.ice.relic.generation;

import net.ice.relic.noise.FastNoiseLite;

public class TerrainGenerator {

    static class Desert extends Biome {

        public Desert() {
            super(105);
        }

        @Override
        public boolean isNeighborBiomeEligible(Biome biome) {
            return !(biome instanceof Desert);
        }

        @Override
        public FastNoiseLite setupGenerator() {
            FastNoiseLite noiseGenerator = new FastNoiseLite();
            noiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
            //noiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
            //noiseGenerator.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);
            //noiseGenerator.SetFrequency(10);
            noiseGenerator.SetCellularJitter(1.6f);
            noiseGenerator.SetFractalGain(1.5f);
            //noiseGenerator.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Hybrid);

            return noiseGenerator;
        }
    }
}

