#version 460

in vec4 Position;

uniform vec2 resolution;
const float SubPixelShift = 0.25;

uniform mat4 projectionMatrix;

out vec2 textureCoord;
out vec4 posPos;

void main() {
    vec4 outPos = projectionMatrix * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    textureCoord = Position.xy / resolution;
    textureCoord.y = 1.0 - textureCoord.y;
    posPos.xy = textureCoord.xy;
    posPos.zw = textureCoord.xy - (1.0/resolution * vec2(0.5 + SubPixelShift));
}