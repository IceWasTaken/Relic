#version 460

layout(location = 0) in vec2 inTextureCoord;

uniform sampler2D inputSampler;
uniform float threshold;

out vec4 outFragColor;

void main() {
    vec3 color = texture(inputSampler, inTextureCoord).rgb;

    // Calculate brightness (Luminance)
    float brightness = dot(color, vec3(0.2126, 0.7152, 0.0722));

    // If brightness is above threshold, output the color, else black
    if(brightness > threshold) {
        outFragColor = vec4(color, 1.0);
    }
    else {
        outFragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}