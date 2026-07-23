#version 460 core

layout (location = 0) in vec2 inTextureCoords;

layout(binding = 0) uniform sampler2D inputSampler;

out vec4 outFragColor;


void main() {
    outFragColor = texture(inputSampler, inTextureCoords);
}