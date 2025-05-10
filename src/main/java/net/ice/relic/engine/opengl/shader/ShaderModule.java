package net.ice.relic.engine.opengl.shader;

public interface ShaderModule {
    String getCode();
    default String getName() { return this.getClass().getSimpleName(); }
}