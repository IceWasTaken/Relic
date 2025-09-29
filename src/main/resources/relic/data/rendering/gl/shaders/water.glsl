#version 430 core

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

uniform uint64_t DuDvHandle;
uniform uint64_t NormalHandle;
uniform uint64_t ReflectionHandle;
uniform uint64_t RefractionHandle;

void distort() {

}

vec3 fresnelEffect() {

}

void main() {
    sampler2D DuDvMap = sampler2D(DuDvHandle);
    sampler2D NormalMap = sampler2D(NormalHandle);
    sampler2D ReflectionMap = sampler2D(ReflectionHandle);
    sampler2D RefractionMap = sampler2D(RefractionHandle);
}