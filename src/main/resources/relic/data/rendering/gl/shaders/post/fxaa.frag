#version 460 core
#extension GL_ARB_bindless_texture : require
#extension GL_ARB_gpu_shader_int64 : require

in vec2 textureCoord;
out vec4 FragColor;

uniform uint64_t screenHandle;
uniform vec2 resolution;

const float FXAA_SPAN_MAX = 8.0;
const float FXAA_REDUCE_MUL = 1.0/8.0;
const float FXAA_REDUCE_MIN = 1.0/128.0;

vec3 sampleTex(sampler2D tex, vec2 uv) {
    return texture(tex, uv).rgb;
}

void main() {
    sampler2D tex = sampler2D(screenHandle);
    vec2 rcpFrame = 1.0 / resolution;

    vec3 rgbNW = sampleTex(tex, textureCoord + vec2(-1.0, -1.0) * rcpFrame);
    vec3 rgbNE = sampleTex(tex, textureCoord + vec2( 1.0, -1.0) * rcpFrame);
    vec3 rgbSW = sampleTex(tex, textureCoord + vec2(-1.0,  1.0) * rcpFrame);
    vec3 rgbSE = sampleTex(tex, textureCoord + vec2( 1.0,  1.0) * rcpFrame);
    vec3 rgbM  = sampleTex(tex, textureCoord);

    float lumaNW = dot(rgbNW, vec3(0.299, 0.587, 0.114));
    float lumaNE = dot(rgbNE, vec3(0.299, 0.587, 0.114));
    float lumaSW = dot(rgbSW, vec3(0.299, 0.587, 0.114));
    float lumaSE = dot(rgbSE, vec3(0.299, 0.587, 0.114));
    float lumaM  = dot(rgbM,  vec3(0.299, 0.587, 0.114));

    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

    vec2 dir;
    dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));

    float dirReduce = max(
    (lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * FXAA_REDUCE_MUL),
    FXAA_REDUCE_MIN);

    float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);

    dir = clamp(dir * rcpDirMin, -FXAA_SPAN_MAX, FXAA_SPAN_MAX) * rcpFrame;

    vec3 rgbA = 0.5 * (
    sampleTex(tex, textureCoord + dir * (1.0/3.0 - 0.5)) +
    sampleTex(tex, textureCoord + dir * (2.0/3.0 - 0.5)));

    vec3 rgbB = 0.5 * rgbA + 0.25 * (
    sampleTex(tex, textureCoord + dir * (0.0/3.0 - 0.5)) +
    sampleTex(tex, textureCoord + dir * (3.0/3.0 - 0.5)));

    float lumaB = dot(rgbB, vec3(0.299, 0.587, 0.114));

    vec3 result = (lumaB < lumaMin || lumaB > lumaMax) ? rgbA : rgbB;

    FragColor = vec4(result, 1.0);
}
