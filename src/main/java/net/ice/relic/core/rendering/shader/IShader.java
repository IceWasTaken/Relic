package net.ice.relic.core.rendering.shader;

import net.ice.relic.core.rendering.backend.opengl.GLShader;

public interface IShader {

    IShader load(String fileName, GLShader.ShaderType type, boolean postShader);
    long getHandle();
}
