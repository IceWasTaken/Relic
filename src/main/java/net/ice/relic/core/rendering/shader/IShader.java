package net.ice.relic.core.rendering.shader;

import net.ice.relic.core.interfaces.Cleanable;

public interface IShader extends Cleanable {

    IShader load(String fileName, ShaderType type, boolean postShader);
    long getHandle();
}
