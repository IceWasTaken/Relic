package net.ice.relic.core.rendering;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.GLShader;

public class ReflectionRenderer extends AbstractRenderer {

    public ReflectionRenderer(RelicApplication application) {
        super(application);
    }

    @Override
    protected void initShaders() {
        loadShader("", GLShader.ShaderType.VERTEX);
        loadShader("", GLShader.ShaderType.FRAGMENT);
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
