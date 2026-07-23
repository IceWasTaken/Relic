#version 460

layout(location = 0) in vec3 inPos;

uniform mat4 centerPos;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 worldPos = centerPos * vec4(inPos, 1);
    gl_Position = projectionMatrix * viewMatrix * worldPos;
}