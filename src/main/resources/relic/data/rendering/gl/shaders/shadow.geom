#version 460

#define SHADOW_MAP_CASCADE_COUNT 3

layout(triangles, invocations = SHADOW_MAP_CASCADE_COUNT) in;
layout(triangle_strip, max_vertices = 3) out;

layout(location = 0) in vec2 inTextCoords[];
layout(location = 1) in flat uint inMaterialIndex[];

layout(location = 0) out vec2 outTextCoords;
layout(location = 1) out flat uint outMaterialIndex;

uniform mat4 projViewMatrices[3];

void main() {

    for (int i = 0; i < 3; i++) {
        outTextCoords = inTextCoords[i];
        outMaterialIndex = inMaterialIndex[i];
        gl_Layer = gl_InvocationID;
        gl_Position = projViewMatrices[gl_InvocationID] * gl_in[i].gl_Position;
        EmitVertex();
    }
    EndPrimitive();
}