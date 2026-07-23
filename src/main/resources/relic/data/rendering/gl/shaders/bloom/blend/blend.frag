#version 460

layout(location = 0) in vec2 inTextureCoord;

uniform sampler2D inputSampler;
uniform sampler2D blurSampler;

uniform float exposure;

out vec4 outFragColor;

void main() {
    const float gamma = 2.2;
    vec3 color = texture(inputSampler, inTextureCoord).rgb;
    vec3 bloomColor = texture(blurSampler, inTextureCoord).rgb;

    color += bloomColor;

    vec3 result = vec3(1.0) - exp(-color * exposure);

    result = pow(result, vec3(1.0 / gamma));

    outFragColor = vec4(result, 1.0);
}
