#version 430

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

layout (location = 0) in vec4 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 tangent;
layout (location = 3) in vec3 bitangent;
layout (location = 4) in vec2 textureCoords;
layout (location = 5) flat in uint materialIndex;

layout (location = 0) out vec4 outPos;
layout (location = 1) out vec4 outAlbedo;
layout (location = 2) out vec4 outNormal;
layout (location = 3) out vec4 outPBR;

struct Material {
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    float roughnessFactor;
    float metallicFactor;
};

struct Map {
    uint64_t albedoMap;
    uint64_t normalMap;
    uint64_t pbrMap;
};

layout(std430, binding = 7) buffer MaterialBuffer {
    Material materials[];
};

layout(std430, binding = 8) buffer MapBuffer {
    Map maps[];
};


vec3 calculateNormals(Material material, vec3 normal, vec2 textCoords, mat3 TBN) {
    vec3 newNormal = normal;

    if(maps[materialIndex].normalMap != 0) {
        newNormal = texture(sampler2D(maps[materialIndex].normalMap), textCoords).rgb;
        newNormal = normalize(newNormal * 2.0 - 1.0);
        newNormal = normalize(TBN * newNormal);
    }

    return newNormal;
}

void main() {
    outPos = pos;

    Material material = materials[materialIndex];

    uint64_t albedoMap = maps[materialIndex].albedoMap;
    uint64_t normalMap = maps[materialIndex].normalMap;
    uint64_t pbrMap = maps[materialIndex].pbrMap;

    if(albedoMap != 0u) {
        outAlbedo = texture(sampler2D(albedoMap), textureCoords);
    } else {
        outAlbedo = material.diffuse;
    }

    if(outAlbedo.a < 0.5) {
        discard;
    }

    mat3 TBN = mat3(tangent, bitangent, normal);
    vec3 newNormal = calculateNormals(material, normal, textureCoords, TBN);
    outNormal = vec4(newNormal, 1.0);

    float ao = 0.5f;
    float roughnessFactor = 0.0f;
    float metallicFactor = 0.0f;
    if(pbrMap != 0) {
        vec4 pbrMapValue = texture(sampler2D(pbrMap), textureCoords);
        roughnessFactor = pbrMapValue.g;
        metallicFactor = pbrMapValue.b;
    } else {
        roughnessFactor = material.roughnessFactor;
        metallicFactor = material.metallicFactor;
    }

    outPBR = vec4(ao, roughnessFactor, metallicFactor, 1.0f);
}