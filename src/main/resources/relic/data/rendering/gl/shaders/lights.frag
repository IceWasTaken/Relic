#version 460

const int SHADOW_MAP_CASCADE_COUNT = 3;
const int DEBUG_SHADOWS = 0;
const float PI = 3.1459265359;

layout(location = 0) in vec2 inTextureCoord;
out vec4 outFragColor;

struct Light {
    vec3 position;
    float intensity;

    vec3 color;
    int directional;
};

struct Fog {
    vec3 color;
    int activeFog;
    float density;
};
struct CascadeShadow {
    mat4 shadowProjectionMatrix;
    vec4 splitDistance;
};

uniform sampler2D posSampler;
uniform sampler2D albedoSampler;
uniform sampler2D normalSampler;
uniform sampler2D pbrSampler;
uniform sampler2DArray shadowSampler;

uniform vec3 cameraPos;
uniform mat4 viewMatrix;

uniform float ambientLightIntensity;
uniform vec3 ambientLightColor;

uniform int lightCount;

uniform Fog fog;
uniform Light lights[200];
uniform CascadeShadow shadows[3];

float chebyshevUpperBound(vec2 moments, float t) {
    //fully lit if current fragment is close to light
    if (t <= moments.x) {
        return 1.0;
    }

    float variance = moments.y - (moments.x * moments.x);
    variance = max(variance, 0.00002);

    //probablistic upper bound
    float d = t - moments.x;
    float p_max = variance / (variance + d * d);

    //reduce light bleeding
    p_max = smoothstep(0.2, 1.0, p_max);

    return p_max;
}

float calculateVisibility(vec4 worldPosition, uint cascadeIndex) {
    vec4 shadowMapPosition = shadows[cascadeIndex].shadowProjectionMatrix * worldPosition;
    shadowMapPosition /= shadowMapPosition.w;

    vec2 uv = shadowMapPosition.xy * 0.5 + 0.5;
    float depth = shadowMapPosition.z;
    vec2 moments = texture(shadowSampler, vec3(uv, cascadeIndex)).rg;

    float visibility = chebyshevUpperBound(moments, depth);
    return visibility;
}

float distributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;

    float nom = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}

float geometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;

    float nom = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}

float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = geometrySchlickGGX(NdotV, roughness);
    float ggx1 = geometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

vec3 calculatePointLight(Light light, vec3 worldPos, vec3 V, vec3 N, vec3 F0, vec3 albedo, float metallic, float roughness) {
    vec3 tmpSub = light.position - worldPos;
    vec3 L = normalize(tmpSub - worldPos);
    vec3 H = normalize(V + L);

    // Calculate distance and attenuation
    float distance = length(tmpSub);
    float attenuation = 1.0 / (distance * distance);
    float intensity = 10.0f;
    vec3 radiance = light.color * light.intensity * attenuation;

    // Cook-Torrance BRDF
    float NDF = distributionGGX(N, H, roughness);
    float G = geometrySmith(N, V, L, roughness);
    vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    vec3 numerator = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    vec3 specular = numerator / denominator;

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;

    float NdotL = max(dot(N, L), 0.0);
    return (kD * albedo / PI + specular) * radiance * NdotL;
}

vec3 calculateDirectionalLight(Light light, vec3 V, vec3 N, vec3 F0, vec3 albedo, float metallic, float roughness) {
    vec3 L = normalize(-light.position);
    vec3 H = normalize(V + L);

    vec3 radiance = light.color * light.intensity;

    // Cook-Torrance BRDF
    float NDF = distributionGGX(N, H, roughness);
    float G = geometrySmith(N, V, L, roughness);
    vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    vec3 numerator = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    vec3 specular = numerator / denominator;

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;

    float NdotL = max(dot(N, L), 0.0);
    return (kD * albedo / PI + specular) * radiance * NdotL;
}


void main()
{
    vec3 albedo = texture(albedoSampler, inTextureCoord).rgb;
    vec3 normal = texture(normalSampler, inTextureCoord).rgb;
    vec4 worldPosW = texture(posSampler, inTextureCoord);
    vec3 worldPos = worldPosW.xyz;
    vec3 pbr = texture(pbrSampler, inTextureCoord).rgb;

    float roughness = pbr.g;
    float metallic = pbr.b;

    vec3 N = normalize(normal);
    vec3 V = normalize(cameraPos - worldPos);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metallic);

    uint cascadeIndex = 0;
    vec4 viewPos = viewMatrix * worldPosW;
    for(uint i = 0; i < SHADOW_MAP_CASCADE_COUNT - 1; ++i) {
        if(viewPos.z < shadows[i].splitDistance.x) {
            cascadeIndex = i + 1;
        }
    }

    float shadow = calculateVisibility(vec4(worldPos, 1), cascadeIndex);

    vec3 Lo = vec3(0.0);
    for (uint i = 0; i < lightCount; i++) {
        Light light = lights[i];
        if (light.directional == 1) {
            Lo += calculateDirectionalLight(light, V, N, F0, albedo, metallic, roughness);
        } else {
            Lo += calculatePointLight(light, worldPos, V, N, F0, albedo, metallic, roughness);
        }
    }

    vec3 ambient = ambientLightColor * albedo * ambientLightIntensity;
    outFragColor = vec4(Lo * shadow + ambient, 1.0f);

    if (DEBUG_SHADOWS == 1) {
        switch (cascadeIndex) {
            case 0:
                outFragColor.rgb *= vec3(1.0f, 0.25f, 0.25f);
                break;
            case 1:
                outFragColor.rgb *= vec3(0.25f, 1.0f, 0.25f);
                break;
            case 2:
                outFragColor.rgb *= vec3(0.25f, 0.25f, 1.0f);
                break;
            default:
                outFragColor.rgb *= vec3(1.0f, 1.0f, 0.25f);
                break;
        }
    }

    //if (fog.activeFog == 1) {
    //fragColor = calcFog(view_pos, fragColor, fog, ambientLight.color, directionalLight);
    //}
}