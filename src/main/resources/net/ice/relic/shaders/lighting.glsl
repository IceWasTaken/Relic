vec3 calculateLighting(vec3 normal, vec3 fragPos, vec3 lightPos, vec3 lightColor, vec3 viewPos) {
    vec3 ambient = 0.1 * lightColor;
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
    vec3 specular = spec * lightColor;
    return ambient + diffuse + specular;
}