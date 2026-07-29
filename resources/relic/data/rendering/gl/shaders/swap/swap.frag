#version 460

layout (location = 0) in vec2 outTextureCoord;

uniform sampler2D swap;

out vec4 fragColor;

void main() {
    fragColor = texture(swap, outTextureCoord);
}