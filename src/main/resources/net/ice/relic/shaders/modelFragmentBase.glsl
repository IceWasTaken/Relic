in vec2 TexCoord;
in vec3 FragPos;
in vec3 Normal;

out vec4 FragColor;

uniform sampler2D texture1;
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColor;


void main() {
    vec3 texColor = sampleTexture(TexCoord);
    vec3 lighting = calculateLighting(Normal, FragPos, lightPos, lightColor, viewPos);
    vec3 result = lighting * texColor;

    result = applyEmissive(TexCoord, result);
    FragColor = vec4(result, 1.0);
}