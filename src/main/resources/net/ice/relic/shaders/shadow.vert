#version 460

const int MAX_DRAW_ELEMENTS = 100;
const int MAX_ENTITIES = 50;

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=2) in vec3 tangent;
layout (location=3) in vec3 bitangent;
layout (location=4) in vec2 texCoord;

struct DrawElement
{
    int modelMatrixIndex;
};

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform DrawElement drawElements[MAX_DRAW_ELEMENTS];
uniform mat4 modelMatrices[MAX_ENTITIES];

void main()
{
    vec4 initPos = vec4(position, 1.0);
    uint idx = gl_BaseInstance + gl_InstanceID;
    int modelMatrixIdx = drawElements[idx].modelMatrixIndex;
    mat4 modelMatrix = modelMatrices[modelMatrixIdx];
    gl_Position = projectionMatrix * modelMatrix * initPos;
}