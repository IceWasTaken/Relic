package net.ice.relic.engine.opengl.shader;

@Deprecated
public interface ShaderModule {
    String getName();
    String getVertexCode();
    String getFragmentCode();
}