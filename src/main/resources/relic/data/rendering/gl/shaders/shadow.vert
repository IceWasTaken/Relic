#version 460

const int MAX_DRAW_ELEMENTS = 200;
const int MAX_ENTITIES = 100;

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=2) in vec3 tangent;
layout (location=3) in vec3 bitangent;
layout (location=4) in vec2 texCoord;

struct DrawElement
{
    mat4 modelMatrix;
    int materialIndex;
};

uniform DrawElement drawElements[MAX_DRAW_ELEMENTS];

layout (location = 0) out vec2 inTextCoords;
layout (location = 1) out flat uint inMaterialIdx;

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;

    inTextCoords = texCoord;
    inMaterialIdx = drawElements[idx].materialIndex;

    gl_Position = drawElements[idx].modelMatrix * vec4(position, 1.0f);
}