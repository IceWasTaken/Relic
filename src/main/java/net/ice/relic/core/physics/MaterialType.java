package net.ice.relic.core.physics;

public enum MaterialType {

    // All densities in kg/m^3
    // my american brain hurts
    PLASTIC_PET(1_350),         // standard plastic, water bottles
    PLASTIC_HDPE(950),          // milk jugs, thicker bottles
    PLASTIC_PVC(1_400),         // piping, cards
    PLASTIC_POLYCARBONATE(1_200), // durable plastic lenses, casings

    METAL_STEEL_STAINLESS(8_000), // pots, pans, etc
    METAL_STEEL_MILD(7_850),    // structural steel
    METAL_ALUMINUM(2_700),      // cans, lightweight parts
    METAL_COPPER(8_960),        // wiring, pipes
    METAL_BRASS(8_500),         // fittings, ornaments
    METAL_TITANIUM(4_500),      // strong
    METAL_LEAD(11_340),         // yum

    WOOD_BASSWOOD(415),         // light craft wood
    WOOD_PINE(500),             // strong, used for 2x4s
    WOOD_OAK(750),              // strong
    WOOD_BIRCH(670),            // strong

    PAPER(800),                 // paper (no shit)
    CARDBOARD(690),             // corrugated boxboard

    CLOTH_COTTON(1_500),        // clothing
    CLOTH_POLYESTER(1_380),     // my skin is itchy
    CLOTH_WOOL(1_300),          // my skin is slightly less itchy

    RUBBER_NATURAL(1_100),      // rubber
    RUBBER_FOAMED(500),         // also rubber
    RUBBER_SYNTHETIC(1_200),    // fake rubber

    GLASS_STANDARD(2_500),      // window glass
    GLASS_TEMPERED(2_480),      // safety glass
    GLASS_PYREX(2_230),         // borosilicate

    STONE_GRANITE(2_750),       //
    STONE_MARBLE(2_710),        // grandma
    STONE_CONCRETE(2_400),      // sidewalk

    ICE(917),                   // fresh ice
    WATER(1_000);               // pure water, 4C


    private final float density;

    MaterialType(float density) {
        this.density = density;
    }

    public float getDensity() {
        return density;
    }
}
