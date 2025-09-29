#version 330 core

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 TexCoord;
out vec4 FragColor;

uniform uint64_t screenTexture;
uniform vec2 resolution;

void main() {
    vec2 oneTexel = 1.0 / resolution;

    vec4 center = texture(sampler2D(screenTexture), TexCoord);
    vec4 left   = texture(sampler2D(screenTexture), TexCoord - vec2(oneTexel.x, 0.0));
    vec4 right  = texture(sampler2D(screenTexture), TexCoord + vec2(oneTexel.x, 0.0));
    vec4 up     = texture(sampler2D(screenTexture), TexCoord - vec2(0.0, oneTexel.y));
    vec4 down   = texture(sampler2D(screenTexture), TexCoord + vec2(0.0, oneTexel.y));

    vec4 edge = abs(center - left)
    + abs(center - right)
    + abs(center - up)
    + abs(center - down);

    FragColor = vec4(edge.rgb, 1.0);
}
