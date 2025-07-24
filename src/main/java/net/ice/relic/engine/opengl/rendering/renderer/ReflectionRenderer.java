package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.Shader;

public class ReflectionRenderer extends AbstractRenderer {

    public ReflectionRenderer(RelicApplication application) {
        super(application);
    }

    @Override
    protected void initShaders() {
        loadShader("", Shader.ShaderType.VERTEX);
        loadShader("", Shader.ShaderType.FRAGMENT);
    }

    @Override
    protected void initUniforms() {

    }

    @Override
    public void render() {
        reflectionBuffer.getReflectionFBO().bindFrameBuffer();
        // Clear and set up viewport if needed
        // Render your reflection scene here

        reflectionBuffer.getReflectionFBO().unbindFrameBuffer();
    }

    @Override
    protected void setupData() {

    }
}
