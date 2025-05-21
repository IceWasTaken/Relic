#version 330 core
in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord;

out vec4 FragColor;

uniform vec3 ambientColor;
uniform sampler2D texture_diffuse;

void main() {
    vec3 ambient = ambientColor;
    vec3 texColor = texture(texture_diffuse, TexCoord).rgb;
    FragColor = vec4(texColor * ambient, 1.0);
}