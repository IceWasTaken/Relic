package net.ice.relic.core.rendering.shader;

import java.util.List;

public interface IShaderProgram {

    IShaderProgram attach(List<IShader> shaders);
}
