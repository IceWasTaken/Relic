#version 330 core

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 textureCoords;
in vec4 particleColor;

out vec4 fragColor;

uniform sampler2D sprite;
uniform float life = 1;

void main() {
    fragColor = (texture(sprite, textureCoords) * particleColor) * life;
}