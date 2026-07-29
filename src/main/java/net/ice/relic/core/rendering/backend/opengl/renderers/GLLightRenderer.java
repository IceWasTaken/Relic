package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ShadowData;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;

public class GLLightRenderer implements Lifecycle {

    private GLShaderProgram shaderProgram;
    private Uniforms uniformBufferObject;
    private QuadMesh quadMesh;
    private Viewport viewport;

    private final GLRenderer glRenderer;

    public GLLightRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;
        this.viewport = glRenderer.getApplication().getCurio().getGraphicsContext().createViewport(
                glRenderer.getApplication().getWindow().getWidth(),
                glRenderer.getApplication().getWindow().getHeight()
        );
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram("lights");

        this.uniformBufferObject = new Uniforms(shaderProgram);

        uniformBufferObject.createUniform("posSampler");
        uniformBufferObject.createUniform("albedoSampler");
        uniformBufferObject.createUniform("normalSampler");
        uniformBufferObject.createUniform("pbrSampler");
        uniformBufferObject.createUniform("shadowSampler");


        for (int i = 0; i < 200; i++) {
            String prefix = uniformBufferObject.formatUniform("lights", i);
            uniformBufferObject.createUniform(prefix + ".position");
            uniformBufferObject.createUniform(prefix + ".directional");
            uniformBufferObject.createUniform(prefix + ".intensity");
            uniformBufferObject.createUniform(prefix + ".color");
        }

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            String prefix = uniformBufferObject.formatUniform("shadows", i);
            uniformBufferObject.createUniform(prefix + ".shadowProjectionMatrix");
            uniformBufferObject.createUniform(prefix + ".splitDistance");
        }

        uniformBufferObject.createUniform("ambientLightColor");
        uniformBufferObject.createUniform("ambientLightIntensity");
        uniformBufferObject.createUniform("lightCount");

        uniformBufferObject.createUniform("cameraPos");
        uniformBufferObject.createUniform("viewMatrix");

        this.quadMesh = new QuadMesh();
    }

    @Override
    public void render() {
        shaderProgram.bind();
        Scene scene = glRenderer.getApplication().getCurrentScene();
        updateLights(scene);
        updateShadows();
        viewport.bind();
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        uniformBufferObject.setUniform("ambientLightColor", scene.getAmbientLight().getColor().div().vec3f());
        uniformBufferObject.setUniform("ambientLightIntensity", scene.getAmbientLight().getIntensity());
        uniformBufferObject.setUniform("lightCount", scene.getLightCount());

        uniformBufferObject.setUniform("cameraPos", scene.getCamera().getPosition());
        uniformBufferObject.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        glRenderer.getGeometryBuffer().bindTextures();
        glRenderer.getShadowBuffer().bindTextureArray(GL_TEXTURE4);

        uniformBufferObject.setUniform("posSampler", 0);
        uniformBufferObject.setUniform("albedoSampler", 1);
        uniformBufferObject.setUniform("normalSampler", 2);
        uniformBufferObject.setUniform("pbrSampler", 3);

        uniformBufferObject.setUniform("shadowSampler", 4);

        quadMesh.getMeshVAO().bind();glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glEnable(GL_FRAMEBUFFER_SRGB);
        shaderProgram.unbind();


    }

    public void resize(int width, int height) {
        viewport.resize(width, height);
    }

    private void updateLights(Scene scene) {
        int index = 0;
        for (Light light : scene.getLights()) {
            String prefix = uniformBufferObject.formatUniform("lights", index);
            uniformBufferObject.setUniform(prefix + ".position", light.getPosition());
            uniformBufferObject.setUniform(prefix + ".color", light.getColor().div().vec3f());
            uniformBufferObject.setUniform(prefix + ".directional", light.isDirectional() ? 1 : 0);
            uniformBufferObject.setUniform(prefix + ".intensity", light.getIntensity());
            index++;
        }
    }

    private void updateShadows() {
        int index = 0;
        for(ShadowData shadowData : glRenderer.getShadowRenderer().getShadows().getShadowData()) {
            String prefix = uniformBufferObject.formatUniform("shadows", index);
            uniformBufferObject.setUniform(prefix + ".shadowProjectionMatrix", shadowData.getProjViewMatrix());
            uniformBufferObject.setUniform(prefix + ".splitDistance", new Vector4f(shadowData.getSplitDistance()));
            index++;
        }
    }

}
