package net.ice.curio.graphics.object.pipeline.shader;

import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.vulkan.VK10.*;

public enum ShaderType {

    VERTEX(GL_VERTEX_SHADER, VK_SHADER_STAGE_VERTEX_BIT),
    FRAGMENT(GL_FRAGMENT_SHADER, VK_SHADER_STAGE_FRAGMENT_BIT),
    GEOMETRY(GL_GEOMETRY_SHADER, VK_SHADER_STAGE_GEOMETRY_BIT),
    COMPUTE(GL_COMPUTE_SHADER, VK_SHADER_STAGE_COMPUTE_BIT),;

    private final int glType;
    private final int vulkanType;

    ShaderType(int glType, int vulkanType) {
        this.glType = glType;
        this.vulkanType = vulkanType;
    }

    public int getGlType() {
        return glType;
    }

    public int getVulkanType() {
        return vulkanType;
    }
}
