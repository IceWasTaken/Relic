in vec2 TexCoord;
in vec3 FragPos;
in vec3 Normal;

out vec4 FragColor;

uniform sampler2D texture0;
uniform sampler2D emissiveMap;
uniform bool hasEmissiveMap;
uniform vec3 emissiveColor;
uniform float emissiveStrength;

uniform vec3 viewPos;
uniform vec3 lightPos;
uniform vec3 lightColor;

#define MAX_EMISSIVE_LIGHTS 8
uniform int emissiveLightCount;
uniform vec3 emissiveLightPos[MAX_EMISSIVE_LIGHTS];
uniform vec3 emissiveLightColor[MAX_EMISSIVE_LIGHTS];
uniform float emissiveLightStrength[MAX_EMISSIVE_LIGHTS];

vec3 sampleTexture(vec2 uv) {
    return texture(texture0, uv).rgb;
}

vec3 applyEmissive(vec2 uv, vec3 baseColor) {
    vec3 emissiveTex = hasEmissiveMap ? texture(emissiveMap, uv).rgb : vec3(1.0);
    vec3 emissive = emissiveColor * emissiveTex * emissiveStrength;
    return baseColor + emissive;
}

vec3 calculateLighting(vec3 normal, vec3 fragPos, vec3 viewDir, vec3 texColor) {
    vec3 ambient = 0.1 * texColor;

    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor * texColor;

    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
    vec3 specular = spec * lightColor;

    return ambient + diffuse + specular;
}

void main() {
    vec3 texColor = sampleTexture(TexCoord);
    vec3 viewDir = normalize(viewPos - FragPos);

    vec3 baseLight = calculateLighting(Normal, FragPos, viewDir, texColor);

    vec3 emissive = vec3(0.0);
    for (int i = 0; i < emissiveLightCount; ++i) {
        float dist = length(emissiveLightPos[i] - FragPos);
        float attenuation = clamp(1.0 - (dist / 5.0), 0.0, 1.0); // Simple falloff
        emissive += emissiveLightColor[i] * emissiveLightStrength[i] * attenuation;
    }

    vec3 result = baseLight + emissive;
    result = applyEmissive(TexCoord, result);

    FragColor = vec4(result, 1.0);
}
