package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ShadowData;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.curio.library.opengl.object.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector4f;

import static net.ice.curio.graphics.object.pipeline.depth.CompareFunction.GREATER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class GLLightRenderer implements Lifecycle {

    private QuadMesh quadMesh;
    private Viewport viewport;

    private GLPipeline pipeline;

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
        int width = glRenderer.getApplication().getWindow().getWidth();
        int height = glRenderer.getApplication().getWindow().getHeight();

        this.pipeline = new GLPipeline(
                glRenderer.getApplication().getCurio().getGraphicsContext(),
                "lights",
                null,
                PrimitiveType.TRIANGLE,
                new RasterizationState(
                        PolygonMode.FILL,
                        FrontFace.COUNTER_CLOCKWISE,
                        CullMode.BACK,
                        true,
                        1.0f
                ),
                new DepthState(
                        true,
                        true,
                        GREATER,
                        false,
                        false
                ),
                new GLViewport(
                        0,
                        0,
                        width,
                        height,
                        true
                )
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

    @Override
    public void render() {
        Uniforms uniforms = pipeline.getUniforms();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        GLPipeline scenePipeline = glRenderer.getSceneRenderer().getPipeline();

        pipeline.bindPipeline();

        Scene scene = glRenderer.getApplication().getCurrentScene();
        updateLights(scene);
        updateShadows();
        viewport.bind();
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        scenePipeline.getFramebuffer().ifPresent((fb) -> fb.bindTextures(0));
        glRenderer.getShadowBuffer().bindTextureArray(4);

        uniforms.setUniform("posSampler", 0);
        uniforms.setUniform("albedoSampler", 1);
        uniforms.setUniform("normalSampler", 2);
        uniforms.setUniform("pbrSampler", 3);
        uniforms.setUniform("shadowSampler", 4);

        quadMesh.getMeshVAO().bind();
        glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        //glEnable(GL_FRAMEBUFFER_SRGB);
    }

    public void resize(int width, int height) {
        viewport.resize(width, height);
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

    private void updateShadows() {
        Uniforms uniforms = pipeline.getUniforms();

        int index = 0;
        for(ShadowData shadowData : glRenderer.getShadowRenderer().getShadows().getShadowData()) {
            String prefix = uniforms.formatUniform("shadows", index);
            uniforms.setUniform(prefix + ".shadowProjectionMatrix", shadowData.getProjViewMatrix());
            uniforms.setUniform(prefix + ".splitDistance", new Vector4f(shadowData.getSplitDistance()));
            index++;
        }
    }

}
