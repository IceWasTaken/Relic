package net.ice.relic.engine.opengl.shader.module;

import net.ice.relic.engine.opengl.shader.ShaderModule;
import net.ice.relic.engine.util.IOUtil;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;

public class LightingModule implements ShaderModule {


    @Override
    public String getName() {
        return "lighting";
    }

    @Override
    public String getVertexCode() {
        return "";
    }

    @Override
    public String getFragmentCode() {
        return readShaderFile("lighting.glsl");
    }
}
