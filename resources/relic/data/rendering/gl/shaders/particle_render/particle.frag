#version 430 core

#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 textureCoords;

out vec4 fragColor;

struct Particle {
    vec4 color;
    vec3 position;
    float life;
    vec3 velocity;
    float collisionSize;
    vec2 scale;
    int dead;
};

layout(std430, binding = 9) buffer ParticleBuffer {
    Particle particles[];
};

uniform sampler2D sprite;
uniform int index;

void main() {
    if(particles[index].dead == 1) {
        discard;
    }

    fragColor = (texture(sprite, textureCoords) * particles[index].color) * particles[index].life;
}