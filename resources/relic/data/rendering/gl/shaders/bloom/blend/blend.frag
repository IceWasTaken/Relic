#version 460

layout(location = 0) in vec2 inTextureCoord;

uniform sampler2D inputSampler;
uniform sampler2D bloomSampler;

uniform float exposure;
uniform float bloomStrength = 0.04f;

out vec4 outFragColor;

void main() {
    vec3 color = texture(inputSampler, inTextureCoord).rgb;
    vec3 bloomColor = texture(bloomSampler, inTextureCoord).rgb;

    vec3 result = mix(color, bloomColor, bloomStrength);

    result = vec3(1.0) - exp(-color * exposure);

    const float gamma = 2.2;
    result = pow(result, vec3(1.0 / gamma));

    outFragColor = vec4(result, 1.0);
}
