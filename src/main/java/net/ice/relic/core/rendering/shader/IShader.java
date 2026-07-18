package net.ice.relic.core.rendering.shader;

import net.ice.heirloom.Lifecycle;

public interface IShader extends Lifecycle {

    IShader load(String fileName, ShaderType type, boolean postShader);
    long getHandle();
}
