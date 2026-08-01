#version 460

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec3 tangent;
layout(location=3) in vec3 bitangent;
layout(location=4) in vec2 texCoord;

layout(location = 0) out vec2 outTextCoord;
layout(location = 1) out flat uint outMaterialIndex;

struct Instance {
    mat4 modelMatrix;
    int materialIndex;
};


layout(std430, binding = 9) buffer InstanceBuffer {
    Instance instances[];
};

void main()
{
    uint index = gl_BaseInstance + gl_InstanceID;

    Instance instance = instances[index];

    outTextCoord = texCoord;
    outMaterialIndex = instance.materialIndex;

    gl_Position = instance.modelMatrix * vec4(position, 1.0f);
}