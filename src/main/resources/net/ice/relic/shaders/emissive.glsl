uniform float emissiveStrength;
uniform sampler2D emissiveMap;
uniform bool hasEmissiveMap;
uniform vec3 emissiveColor;

vec3 applyEmissive(vec2 uv, vec3 baseColor) {
    vec3 emissiveTex = hasEmissiveMap ? texture(emissiveMap, uv).rgb : vec3(1.0);
    vec3 emissive = emissiveColor * emissiveTex * emissiveStrength;
    return baseColor + emissive;
}