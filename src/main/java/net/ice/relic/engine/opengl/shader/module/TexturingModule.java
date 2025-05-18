package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;
import net.ice.relic.engine.util.IOUtil;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;

@Deprecated
public class TexturingModule implements ShaderModule {


    @Override
    public String getName() {
        return "texturing";
    }

    @Override
    public String getVertexCode() {
        return readShaderFile("texturing.glsl");
    }

    @Override
    public String getFragmentCode() {
        return readShaderFile("texturing.glsl");
    }
}
