package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;
import net.ice.relic.engine.util.IOUtil;

public class LightingModule implements ShaderModule {

    @Override
    public String getCode() {
        return IOUtil.readShaderFile("lighting.glsl");
    }
}
