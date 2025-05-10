package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;

public class EmissiveModule implements ShaderModule {

    @Override
    public String getCode() {
        return readShaderFile("emissive.glsl");
    }
}

