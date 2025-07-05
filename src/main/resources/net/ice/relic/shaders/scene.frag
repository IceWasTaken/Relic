#version 400

const int MAX_MATERIALS  = 32;
const int MAX_TEXTURES = 32;

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
layout (location = 3) out vec4 buffORM; // AO, Roughness, Metallic, Unused

struct Material {
    vec4 diffuse;
    vec4 specular;

    float reflectance;
    int textureIndex;
    int normalMapIndex;
    int ormMapIndex;
};

uniform sampler2D textureSampler[MAX_TEXTURES];
uniform Material materials[MAX_MATERIALS];

vec3 calcNormal(int idx, vec3 normal, vec3 tangent, vec3 bitangent, vec2 textCoords) {
    mat3 TBN = mat3(tangent, bitangent, normal);
    vec3 newNormal = texture(textureSampler[idx], textCoords).rgb;
    newNormal = normalize(vec3(newNormal.r, 1.0 - newNormal.g, newNormal.b) * 2.0 - 1.0);
    newNormal = normalize(TBN * newNormal);
    return newNormal;
}

void main() {
    Material material = materials[outMaterialIdx];

    vec4 textColor = texture(textureSampler[material.textureIndex], outTextCoord);
    vec4 diffuse = textColor + material.diffuse;
    if (diffuse.a < 0.5) {
        discard;
    }

    vec3 normal = outNormal;
    if (material.normalMapIndex > 0) {
        normal = calcNormal(material.normalMapIndex, outNormal, outTangent, outBitangent, outTextCoord);
    }

    vec3 orm = vec3(1.0, 1.0, 0.0); // Default AO=1, rough=1, metal=0
    if (material.ormMapIndex > 0) {
        orm = texture(textureSampler[material.ormMapIndex], outTextCoord).rgb;
    }

    buffAlbedo   = vec4(diffuse.rgb, material.reflectance);
    buffNormal   = vec4(normalize(0.5 * normal + 0.5), 1.0);
    buffSpecular = vec4(material.specular.rgb, 1.0);
    buffORM      = vec4(orm, 1.0); // AO, Roughness, Metallic
}
