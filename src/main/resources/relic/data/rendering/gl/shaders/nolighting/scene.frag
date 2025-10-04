#version 430

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

const int MAX_MATERIALS  = 200;
const int MAX_TEXTURES = 160;

in vec3 outNormal;
in vec3 outTangent;
in vec3 outBitangent;
in vec2 outTextCoord;
in vec4 outViewPosition;
in vec4 outWorldPosition;
flat in uint outMaterialIdx;

out vec4 FragColor;

struct Material
{
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    float _padding;
    float _padding1;
    float _padding2;
    uint64_t textureHandle;
    uint64_t normalHandle;
    //uint64_t emissiveHandle;
    //uint64_t specularHandle;
    //uint64_t AOHandle;
};

layout(std430, binding = 5) buffer MaterialBuffer {
    Material materials[MAX_MATERIALS];
};

vec3 calcNormal(Material mat, vec3 normal, vec3 tangent, vec3 bitangent, vec2 textCoords) {
    mat3 TBN = mat3(tangent, bitangent, normal);
    vec3 newNormal = texture(sampler2D(mat.normalHandle), textCoords).rgb;
    newNormal = normalize(newNormal * 2.0 - 1.0);
    newNormal = normalize(TBN * newNormal);
    return newNormal;
}

void main() {
    Material material = materials[outMaterialIdx];

    sampler2D albedo = sampler2D(material.textureHandle);

    vec4 text_color = texture(albedo, outTextCoord);
    vec4 diffuse = text_color + material.diffuse;
    if (diffuse.a < 0.5) {
        discard;
    }
    vec4 specular = text_color + material.specular;
    vec3 normal = outNormal;
    if (material.normalHandle != 0u) {
        normal = calcNormal(material, outNormal, outTangent, outBitangent, outTextCoord);
    }

    FragColor = vec4(diffuse.xyz, material.reflectance);
    //buffNormal = vec4(0.5 * normal + 0.5, 1.0);
    //buffSpecular = specular;

}