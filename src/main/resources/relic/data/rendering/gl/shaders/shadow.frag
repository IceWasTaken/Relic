#version 460

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

layout(location = 0) in vec2 inTextureCoords;
layout(location = 1) in flat uint inMaterialIndex;

out vec2 outFragColor;

const int MAX_MATERIALS = 200;

struct Material
{
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    float roughnessFactor;
    float metallicFactor;
    float _padding;
};

layout(std430, binding = 7) buffer MaterialBuffer {
    Material materials[MAX_MATERIALS];
};

layout(std430, binding = 8) buffer AlbedoMapBuffer {
    uint64_t albedoMaps[MAX_MATERIALS];
};

layout(std430, binding = 9) buffer NormalMapBuffer {
    uint64_t normalMaps[MAX_MATERIALS];
};

layout(std430, binding = 10) buffer PBRMapBuffer {
    uint64_t pbrMaps[MAX_MATERIALS];
};

void main() {
    Material material = materials[inMaterialIndex];
    vec4 albedo;

    if(albedoMaps[inMaterialIndex] != 0) {
        albedo = texture(sampler2D(albedoMaps[inMaterialIndex]), inTextureCoords);
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