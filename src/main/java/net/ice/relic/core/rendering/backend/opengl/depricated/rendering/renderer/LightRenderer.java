package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.renderer;

import net.ice.relic.core.ShadowData;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.depricated.AbstractGLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.rendering.backend.opengl.depricated.rendering.QuadMesh;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.*;

import static org.lwjgl.opengl.GL11.*;

@Deprecated
public class LightRenderer extends AbstractGLRenderer {

    private QuadMesh quadMesh;
    private ShadowRenderer shadowRenderer;

    public LightRenderer(GLManager glManager) {
        super(glManager);
    }

    @Override
    public void init() {
        super.init();

        this.quadMesh = new QuadMesh();
    }

    @Override
    protected void initShaders() {
        loadShader("lights.vert", ShaderType.VERTEX);
        loadShader("lights.frag", ShaderType.FRAGMENT);
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("posSampler");
        uniforms.createUniform("albedoSampler");
        uniforms.createUniform("normalSampler");
        uniforms.createUniform("pbrDataSampler");
        uniforms.createUniform("shadowSampler");

        for (int i = 0; i < 200; i++) {
            String prefix = uniforms.formatUniform("lights", i);
            uniforms.createUniform(prefix + ".lightPos");
            uniforms.createUniform(prefix + ".directional");
            uniforms.createUniform(prefix + ".intensity");
            uniforms.createUniform(prefix + ".color");
        }

//        uniforms.createUniform("fog.activeFog");
//        uniforms.createUniform("fog.color");
//        uniforms.createUniform("fog.density");

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            String prefix = uniforms.formatUniform("shadows", i);
            uniforms.createUniform(prefix + ".projectionMatrix");
            uniforms.createUniform(prefix + ".splitDistance");
        }

        uniforms.createUniform("ambientColor");
        uniforms.createUniform("ambientStrength");
        uniforms.createUniform("lightCount");

        uniforms.createUniform("cameraPosition");
        uniforms.createUniform("viewMatrix");

    }

    @Override
    public void render() {
        Scene scene = manager.getApplication().getCurrentScene();
        shaderProgram.bind();

        updateLights(scene);

        uniforms.setUniform("ambientColor", scene.getAmbientLight().getColor().div().vec3f());
        uniforms.setUniform("ambientStrength", scene.getAmbientLight().getIntensity());
        uniforms.setUniform("lightCount", scene.getLightCount());

        uniforms.setUniform("cameraPosition", scene.getCamera().getPosition());
        uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        //int[] textureIds = manager.getGeometryBuffer().getTextureIDS();
//        for (int i = 0; i < textureIds.length; i++) {
//            glActiveTexture(GL_TEXTURE0 + i);
//            glBindTexture(GL_TEXTURE_2D, textureIds[i]);
//        }
        //shadowRenderer.getShadowBuffer().bindTextures();

        uniforms.setUniform("posSampler", 0);
        uniforms.setUniform("albedoSampler", 1);
        uniforms.setUniform("normalSampler", 2);
        uniforms.setUniform("pbrDataSampler", 3);
        //uniforms.setUniform("shadowSampler", 4);

//        Fog fog = scene.getFog();
//        uniforms.setUniform("fog.activeFog", fog.isActive() ? 1 : 0);
//        uniforms.setUniform("fog.color", fog.getColor().convertToGLVector3f());
//        uniforms.setUniform("fog.density", fog.getDensity());

        Shadows cascadeShadows = shadowRenderer.getShadows();
        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            ShadowData cascadeShadow = cascadeShadows.getShadowData().get(i);
            uniforms.setUniform("shadows[" + i + "]" + ".projectionMatrix", cascadeShadow.getProjViewMatrix());
            uniforms.setUniform("shadows[" + i + "]" + ".splitDistance", cascadeShadow.getSplitDistance());
        }

        quadMesh.getMeshVAO().bind();
        glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        shaderProgram.unbind();
    }

    private void updateLights(Scene scene) {
        int index = 0;
        for (Light light : scene.getLights()) {
            String prefix = uniforms.formatUniform("lights", index);
            uniforms.setUniform(prefix + ".lightPos", light.getPosition());
            uniforms.setUniform(prefix + ".directional", light.isDirectional() ? 1 : 0);
            uniforms.setUniform(prefix + ".intensity", light.getIntensity());
            uniforms.setUniform(prefix + ".color", light.getColor().div().vec3f());
            index++;
        }
    }

    public void setShadowRenderer(ShadowRenderer shadowRenderer) {
        this.shadowRenderer = shadowRenderer;
    }

    @Override
    protected void setupData() {

    }
}