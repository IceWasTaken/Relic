#version 460

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec3 inTangent;
layout (location = 3) in vec3 inBitangent;
layout (location = 4) in vec2 inTexCoord;

layout (location = 0) out vec4 outPos;
layout (location = 1) out vec3 outNormal;
layout (location = 2) out vec3 outTangent;
layout (location = 3) out vec3 outBitangent;
layout (location = 4) out vec2 outTextureCoords;
layout (location = 5) out uint outMaterialIndex;

struct Instance {
    mat4 modelMatrix;
    int materialIndex;
};

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

layout(std430, binding = 7) buffer InstanceBuffer {
    Instance instances[];
};

void main() {
    Instance instance = instances[gl_BaseInstance + gl_InstanceID];

    vec4 worldPos = instance.modelMatrix * vec4(inPosition, 1);
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    mat3 normal = transpose(inverse(mat3(instance.modelMatrix)));

    outPos = worldPos;
    outNormal = normal * normalize(inNormal);
    outTangent = normal * normalize(inTangent);
    outBitangent = normal * normalize(inBitangent);
    outTextureCoords = inTexCoord;
    outMaterialIndex = instance.materialIndex;
}