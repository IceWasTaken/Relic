#version 460

layout(location = 0) in vec2 inTextureCoord;

uniform sampler2D inputSampler;

uniform bool horizontal;
uniform float weight[5] = float[] (0.227027, 0.194594, 0.121621, 0.054054, 0.016216);

out vec4 outFragColor;

void main() {
    vec2 tex_offset = 1.0 / textureSize(inputSampler, 0);
    vec3 result = texture(inputSampler, inTextureCoord).rgb * weight[0];

    if(horizontal)
    {
        for(int i = 1; i < 5; ++i)
        {
            result += texture(inputSampler, inTextureCoord + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            result += texture(inputSampler, inTextureCoord - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
        }
    }
    else
    {
        for(int i = 1; i < 5; ++i)
        {
            result += texture(inputSampler, inTextureCoord + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            result += texture(inputSampler, inTextureCoord - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        }
    }
    outFragColor = vec4(result, 1.0);
}