#version 430 core

in vec3 fragNormal;
in vec2 fragTexCoord;

out vec4 fragColor;

void main() {
    vec3 color = vec3(0.8) + 0.2 * normalize(fragNormal);
    fragColor = vec4(color, 1.0);
}
