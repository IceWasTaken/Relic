#version 460

layout(triangles, invocations = 6) in; // e.g., 6 layers for a point light cube
layout(triangle_strip, max_vertices = 3) out;

layout(location = 0) in vec3 inPosition[]; // from vertex shader
layout(location = 0) out vec4 fragPos;     // optional, for debugging depth in fragment

uniform mat4 projViewMatrices[6]; // one per layer

void main()
{
    for (int layer = 0; layer < 6; layer++)
    {
        for (int i = 0; i < 3; i++)
        {
            gl_Layer = layer;
            gl_Position = projViewMatrices[layer] * gl_in[i].gl_Position;
            fragPos = gl_in[i].gl_Position; // optional
            EmitVertex();
        }
        EndPrimitive();
    }
}