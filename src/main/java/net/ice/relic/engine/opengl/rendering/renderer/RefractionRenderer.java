package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.Shader;

public class RefractionRenderer extends AbstractRenderer {

    public RefractionRenderer(RelicApplication application) {
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
        refractionBuffer.getRefractionFBO().bindFrameBuffer();
        refractionBuffer.getRefractionFBO().unbindFrameBuffer();
    }

    @Override
    protected void setupData() {

    }

}
