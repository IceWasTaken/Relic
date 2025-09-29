#version 330 core

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 TexCoord;
out vec4 FragColor;

uniform uint64_t screenTexture;
uniform vec2 resolution;

uniform vec2 convergeOffsetR = vec2(-4.0,  0.0);
uniform vec2 convergeOffsetG = vec2( 0.0, -4.0);
uniform vec2 convergeOffsetB = vec2( 2.0,  2.0);

void main() {
    sampler2D screen = sampler2D(screenTexture);
    vec2 oneTexel = 1.0 / resolution;

    vec2 offsetR = TexCoord + convergeOffsetR * oneTexel;
    vec2 offsetG = TexCoord + convergeOffsetG * oneTexel;
    vec2 offsetB = TexCoord + convergeOffsetB * oneTexel;

    float r = texture(screen, offsetR).r;
    float g = texture(screen, offsetG).g;
    float b = texture(screen, offsetB).b;
    float a = texture(screen, TexCoord).a;

    FragColor = vec4(r, g, b, a);
}
