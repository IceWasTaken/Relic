#version 330

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 outTextCoord;
out vec4 fragColor;

uniform vec4 diffuse;
uniform uint64_t textureHandle;
uniform int hasTexture;

void main()
{
    if (hasTexture == 1) {
        fragColor = texture(sampler2D(textureHandle), outTextCoord);
    } else {
        fragColor = diffuse * vec4(500, 1, 1, 1);
    }
}