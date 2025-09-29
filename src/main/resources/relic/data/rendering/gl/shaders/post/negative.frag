#version 330 core

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

const float INVERSE_AMOUNT = 0.8f;

in vec2 TexCoord;
out vec4 FragColor;

uniform uint64_t screenTexture;

void main() {
    vec4 diffuseColor = texture2D(sampler2D(screenTexture), TexCoord);
    vec4 invertColor = 1.0 - diffuseColor;
    vec4 outColor = mix(diffuseColor, invertColor, INVERSE_AMOUNT);
    FragColor = vec4(outColor.rgb, 1.0);
}
