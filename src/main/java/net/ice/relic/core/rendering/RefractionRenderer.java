package net.ice.relic.core.rendering;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.GLShader;

public class RefractionRenderer extends AbstractRenderer {

    public RefractionRenderer(RelicApplication application) {
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
        refractionBuffer.getRefractionFBO().bindFrameBuffer();
        refractionBuffer.getRefractionFBO().unbindFrameBuffer();
    }

    @Override
    protected void setupData() {

    }

}
