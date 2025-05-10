vec3 transformNormal(mat4 model, vec3 normal) {
    return mat3(transpose(inverse(model))) * normal;
}