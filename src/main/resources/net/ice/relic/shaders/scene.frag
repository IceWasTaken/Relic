#version 460 core
#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

const int MAX_MATERIALS = 60;

in vec3 outNormal;
in vec3 outTangent;
in vec3 outBitangent;
in vec2 outTextCoord;
in vec4 outViewPosition;
in vec4 outWorldPosition;
flat in uint outMaterialIdx;

layout (location = 0) out vec4 buffAlbedo;
layout (location = 1) out vec4 buffNormal;
layout (location = 2) out vec4 buffSpecular;

struct Material {
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    uint64_t textureHandle;
    uint64_t normalHandle;
    uint64_t ormHandle;
};

uniform Material materials[MAX_MATERIALS];

vec3 calcNormal(Material mat, vec3 normal, vec3 tangent, vec3 bitangent, vec2 texCoords) {
    if (mat.normalHandle == 0u) {
        return normalize(normal);
    }

    mat3 TBN = mat3(tangent, bitangent, normal);
    vec3 newNormal = texture(sampler2D(mat.normalHandle), texCoords).rgb;
    newNormal = normalize(newNormal * 2.0 - 1.0);
    newNormal = normalize(TBN * newNormal);
    return newNormal;
}

void main() {
    Material mat = materials[outMaterialIdx];
    sampler2D albedoSampler = sampler2D(mat.textureHandle);
    sampler2D ormSampler = sampler2D(mat.ormHandle);

    vec4 texColor;
    if (mat.textureHandle != 0u) {
        texColor = texture(sampler2D(mat.textureHandle), outTextCoord);
    } else {
        texColor = vec4(1.0, 0.0, 0.0, 1.0); // Output red for debug
    }

    //DO NOT FUCKING MULTIPLY
    vec4 diffuse = texColor + mat.diffuse;
    vec4 specular = texColor + mat.specular;

    if (diffuse.a < 0.5) {
        discard;
    }

    vec3 normal = outNormal;

    if(mat.normalHandle != 0u) {
        normal = calcNormal(mat, outNormal, outTangent, outBitangent, outTextCoord);
    }

    buffAlbedo   = vec4(diffuse.xyz, mat.reflectance);
    buffNormal   = vec4(0.5 * normal + 0.5, 1.0);
    buffSpecular = specular;
}


