package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.pipeline.depth.CompareFunction;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.framebuffer.GLFramebuffer;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.relic.core.ShadowData;
import net.ice.relic.core.Shadows;
import net.ice.curio.library.opengl.object.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30.*;

public class GLLightRenderer {

    private QuadMesh quadMesh;
    private GLPipeline pipeline;

    private final GraphicsContext graphicsContext;

    public GLLightRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public void init() {
        this.pipeline = new GLPipeline(
                graphicsContext,
                "lights",
                new GLFramebuffer(
                        graphicsContext,
                        graphicsContext.getCurio().getWindow().getFramebufferSize(),
                        GL_TEXTURE_2D,
                        1,
                        GL_RGBA16F
                ),
                new DepthState(
                        false,
                        false,
                        CompareFunction.ALWAYS,
                        false,
                        false
                ),
                true
        );

        Uniforms uniforms = pipeline.getUniforms();

        uniforms.createUniform("posSampler");
        uniforms.createUniform("albedoSampler");
        uniforms.createUniform("normalSampler");
        uniforms.createUniform("pbrSampler");
        uniforms.createUniform("shadowSampler");

        for (int i = 0; i < 200; i++) {
            String prefix = uniforms.formatUniform("lights", i);
            uniforms.createUniform(prefix + ".position");
            uniforms.createUniform(prefix + ".directional");
            uniforms.createUniform(prefix + ".intensity");
            uniforms.createUniform(prefix + ".color");
        }

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            String prefix = uniforms.formatUniform("shadows", i);
            uniforms.createUniform(prefix + ".shadowProjectionMatrix");
            uniforms.createUniform(prefix + ".splitDistance");
        }

        this.quadMesh = new QuadMesh();
    }

    public void render(GLRenderer renderer) {
        glClearColor(0, 0, 0, 1);

        Uniforms uniforms = pipeline.getUniforms();
        Scene scene = renderer.getApplication().getCurrentScene();
        GLPipeline scenePipeline = renderer.sceneRenderer.getPipeline();
        pipeline.bindPipeline();

        updateLights(scene);
        updateShadows(renderer);

        glClearColor(0, 0, 0, 1);
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        scenePipeline.getFramebuffer().ifPresent(glFramebuffer -> pipeline.getFramebuffer().get().blit(glFramebuffer, GL_DEPTH_BUFFER_BIT, GL_NEAREST));
        scenePipeline.getFramebuffer().ifPresent((fb) -> fb.bindTextures(0));
        renderer.getShadowBuffer().bindTextureArray(4);

        uniforms.setUniform("posSampler", 0);
        uniforms.setUniform("albedoSampler", 1);
        uniforms.setUniform("normalSampler", 2);
        uniforms.setUniform("pbrSampler", 3);
        uniforms.setUniform("shadowSampler", 4);

        quadMesh.getMeshVAO().bind();
        glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
    }

    public void resize(int width, int height) {
        pipeline.resize(width, height);
    }

    private void updateLights(Scene scene) {
        Uniforms uniforms = pipeline.getUniforms();

        int index = 0;
        for (Light light : scene.getLights()) {
            String prefix = uniforms.formatUniform("lights", index);
            uniforms.setUniform(prefix + ".position", light.getPosition());
            uniforms.setUniform(prefix + ".color", light.getColor().div().vec3f());
            uniforms.setUniform(prefix + ".directional", light.isDirectional() ? 1 : 0);
            uniforms.setUniform(prefix + ".intensity", light.getIntensity());
            index++;
        }
    }

    private void updateShadows(GLRenderer renderer) {
        Uniforms uniforms = pipeline.getUniforms();

        int index = 0;
        for(ShadowData shadowData : renderer.shadowRenderer.getShadows().getShadowData()) {
            String prefix = uniforms.formatUniform("shadows", index);
            uniforms.setUniform(prefix + ".shadowProjectionMatrix", shadowData.getProjViewMatrix());
            uniforms.setUniform(prefix + ".splitDistance", new Vector4f(shadowData.getSplitDistance()));
            index++;
        }
    }

    public GLPipeline getPipeline() {
        return pipeline;
    }
}
