package net.ice.relic.core.rendering.shader;


public interface IShader {

    IShader load(String fileName, ShaderType type, boolean postShader);
    long getHandle();
}
