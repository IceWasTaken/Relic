package net.ice.relic.core.rendering.shader;

import net.ice.curio.graphics.object.pipeline.shader.ShaderType;
import net.ice.heirloom.Lifecycle;

public interface IShader extends Lifecycle {

    IShader load(String fileName, ShaderType type);
    long getHandle();
}
