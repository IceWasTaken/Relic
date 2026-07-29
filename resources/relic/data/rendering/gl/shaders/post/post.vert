#version 460

layout (location=0) in vec3 inPos;
layout (location=1) in vec2 inCoord;

layout (location = 0) out vec2 outTextureCoord;

void main()
{
    outTextureCoord = inCoord;
    gl_Position = vec4(inPos, 1.0f);
}