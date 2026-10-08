#version 330 core

layout(location = 0) in vec3 position;

out vec2 textureCoords;
out vec4 particleColor;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 cameraRight;
uniform vec3 cameraUp;
uniform vec3 billboardPos;
uniform vec2 billboardSize;
uniform vec4 color;

void main() {
    vec3 particleCenter = billboardPos;

    vec3 vertexPos = particleCenter +
        cameraRight * position.x * billboardSize.x +
        cameraUp * position.y * billboardSize.y;

    gl_Position = (projectionMatrix * viewMatrix) * vec4(vertexPos, 1.0f);

    textureCoords = position.xy + vec2(0.5, 0.5);
    particleColor = color;
}