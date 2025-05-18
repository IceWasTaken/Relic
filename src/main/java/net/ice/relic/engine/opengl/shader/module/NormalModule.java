package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;

@Deprecated
public class NormalModule implements ShaderModule {

    @Override
    public String getName() {
        return "normal";
    }

    @Override
    public String getVertexCode() {
        return readShaderFile("normal.glsl");
    }

    @Override
    public String getFragmentCode() {
        return "";
    }
}
