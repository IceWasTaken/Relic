#version 430 core

layout(location = 0) in vec3 position;

out vec2 textureCoords;

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

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 cameraRight;
uniform vec3 cameraUp;
uniform vec2 billboardSize;
uniform int index;

void main() {
    vec3 particleCenter = particles[index].position;

    vec3 vertexPos = particleCenter +
        cameraRight * position.x * billboardSize.x +
        cameraUp * position.y * billboardSize.y;

    gl_Position = (projectionMatrix * viewMatrix) * vec4(vertexPos, 1.0f);

    textureCoords = position.xy + vec2(0.5, 0.5);
}