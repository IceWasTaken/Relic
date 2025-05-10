package net.ice.relic.engine.opengl.shader;

public interface ShaderModule {
    String getName();
    String getVertexCode();
    String getFragmentCode();
}