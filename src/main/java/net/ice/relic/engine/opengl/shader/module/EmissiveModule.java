package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;

@Deprecated
public class EmissiveModule implements ShaderModule {

    @Override
    public String getName() {
        return "emissive";
    }

    @Override
    public String getVertexCode() {
        return "";
    }

    @Override
    public String getFragmentCode() {
        return readShaderFile("emissive.glsl");
    }
}

