uniform sampler2D texture0;

vec3 sampleTexture(vec2 texCoord) {
    return texture(texture0, texCoord).rgb;
}