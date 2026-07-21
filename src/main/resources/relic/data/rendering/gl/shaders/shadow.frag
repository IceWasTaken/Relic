#version 460

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

layout(location = 0) in vec2 textureCoords;
layout(location = 1) in flat uint materialIndex;

out vec2 outFragColor;

const int MAX_MATERIALS = 200;

struct Material {
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    float roughnessFactor;
    float metallicFactor;
    float std430Padding;
};

struct Map {
    uint64_t albedoMap;
    uint64_t normalMap;
    uint64_t pbrMap;
};

layout(std430, binding = 7) buffer MaterialBuffer {
    Material materials[MAX_MATERIALS];
};

layout(std430, binding = 8) buffer MapBuffer {
    Map maps[MAX_MATERIALS];
};

void main() {
    Material material = materials[materialIndex];
    vec4 albedo;

    uint64_t albedoMap = maps[materialIndex].albedoMap;
    uint64_t normalMap = maps[materialIndex].normalMap;
    uint64_t pbrMap = maps[materialIndex].pbrMap;

    if(albedoMap != 0) {
        albedo = texture(sampler2D(albedoMap), textureCoords);
    } else {
        albedo = material.diffuse;
    }

    if(albedo.a < 0.5) {
        discard;
    }

    float depth = (gl_FragCoord.z * 2.0) - 1;
    float moment1 = depth;
    float moment2 = depth * depth;

    float dx = dFdx(depth);
    float dy = dFdy(depth);
    moment2 += 0.25 * (dx * dx + dy * dy);

    outFragColor = vec2(moment1, moment2);
}