#version 330

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 frgTextCoords;
in vec4 frgColor;

uniform uint64_t textureHandle;

out vec4 outColor;

void main()
{
    outColor = frgColor  * texture(sampler2D(textureHandle), frgTextCoords);
}