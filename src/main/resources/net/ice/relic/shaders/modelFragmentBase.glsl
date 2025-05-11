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

vec3 calculateEmissiveLighting(vec3 fragPos, vec3 normal) {
    vec3 result = vec3(0.0);
    for (int i = 0; i < emissiveLightCount; i++) {
        vec3 lightPos = emissiveLightPos[i];
        vec3 lightColor = emissiveLightColor[i];
        float lightStrength = emissiveLightStrength[i];

        vec3 toLight = lightPos - fragPos;
        float distance = length(toLight);
        vec3 lightDir = normalize(toLight);

        // Inverse square attenuation with a small epsilon
        float attenuation = 1.0 / (distance * distance + 0.01);

        // Optional soft falloff: fade to zero at max range
        float maxRange = 5.0; // configurable max range
        float fade = clamp(1.0 - (distance / maxRange), 0.0, 1.0);

        // Lambertian diffuse (emissive lights aren't directional, but this gives nice rim shading)
        float NdotL = max(dot(normal, lightDir), 0.0);

        // Shadow check (stub – implement actual shadow logic as needed)
        float shadow = 1.0; // Set to 0.0 if in shadow

        // Final contribution
        vec3 lightContribution = lightColor * lightStrength * attenuation * fade * NdotL * shadow;
        result += lightContribution;
    }
    return result;
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
    if (hasEmissiveMap == 1) {
        emissive = texture(emissiveMap, TexCoords).rgb;
    }
    emissive *= emissiveColor * emissiveStrength;

    // Dynamic lighting from nearby emissive lights
    emissive += calculateEmissiveLighting(FragPos, Normal);

    vec3 result = baseLight + emissive;
    result = applyEmissive(TexCoord, result);

    FragColor = vec4(result, 1.0);
}
