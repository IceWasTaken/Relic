package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;
import net.ice.relic.engine.util.IOUtil;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;

public class TexturingModule implements ShaderModule {
    @Override
    public String getCode() {
        return readShaderFile("texturing.glsl");
    }
}
