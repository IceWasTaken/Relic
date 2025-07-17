package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.Lifecycle;
import net.ice.relic.RelicApplication;
import net.ice.relic.engine.opengl.rendering.GeometryBuffer;
import net.ice.relic.engine.opengl.ShaderProgram;
import net.ice.relic.engine.opengl.model.Model;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GLDebugMessageCallback.getMessage;

public class Renderer implements Lifecycle {

    private final RenderingBuffer renderingBuffer;
    private final GeometryBuffer geometryBuffer;

    private final AnimationRenderer animationRenderer;
    private final SceneRenderer sceneRenderer;
    private final ShadowRenderer shadowRenderer;
    private final LightRenderer lightRenderer;
    private final SkyboxRenderer skyboxRenderer;
    private final PostRenderer postRenderer;
    private final GuiRenderer guiRenderer;

    private final RelicApplication application;

    private boolean postShader = false;
    private ShaderProgram postShaderProgram;

    public Renderer(RelicApplication application) {
        this.application = application;
        this.renderingBuffer = new RenderingBuffer(application);
        this.geometryBuffer = new GeometryBuffer(application);
        this.sceneRenderer = new SceneRenderer(application);
        this.shadowRenderer = new ShadowRenderer(application);
        this.animationRenderer = new AnimationRenderer(application);
        this.lightRenderer = new LightRenderer(application);
        this.skyboxRenderer = new SkyboxRenderer(application);
        this.postRenderer = new PostRenderer(application);
        this.guiRenderer = new GuiRenderer(application);
    }

    @Override
    public void init() {
        createCapabilities();

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_FRAMEBUFFER_SRGB);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            System.err.println("GL DEBUG: \n" + getMessage(length, message));
        }, 0);

        geometryBuffer.init();

        sceneRenderer.init(renderingBuffer, geometryBuffer);
        skyboxRenderer.init(renderingBuffer, geometryBuffer);
        shadowRenderer.init(renderingBuffer, geometryBuffer);
        lightRenderer.init(renderingBuffer, geometryBuffer);
        animationRenderer.init(renderingBuffer, geometryBuffer);
        guiRenderer.init(renderingBuffer, geometryBuffer);
        postRenderer.init();
    }

    @Override
    public void render() {
        animationRenderer.render();
        shadowRenderer.render();
        sceneRenderer.render();
        lightRenderStart();
        lightRenderer.setShadowRenderer(shadowRenderer);
        lightRenderer.render();
        skyboxRenderer.render();

        if (postShader && postShaderProgram != null) {
            postRenderer.render();
        }

        lightRenderFinish();
        //guiRenderer.render();
    }

    private void lightRenderFinish() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void lightRenderStart() {

        if (postShader && postShaderProgram != null) {
            postRenderer.getPostBuffer().bind(GL_FRAMEBUFFER);
        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, geometryBuffer.getGeometryBuffer().getFboID());

    }

    @Override
    public void cleanup() {
        sceneRenderer.cleanup();
    }

    public void setupData() {
        renderingBuffer.loadStaticModels();
        renderingBuffer.loadAnimatedModels();
        sceneRenderer.setupData();
        shadowRenderer.setupData();
        List<Model> models = new ArrayList<>(application.getCurrentScene().getModels().values());
        models.forEach(model -> model.getMeshData().clear());
    }

    public void enablePostShader(ShaderProgram shaderProgram) {
        this.postShader = true;
        this.postShaderProgram = shaderProgram;
        this.postRenderer.loadShader(shaderProgram);
    }

    public void disablePostShader() {
        this.postShader = false;
        this.postShaderProgram = null;
        this.postRenderer.unloadShader();
    }

    public void resize() {
        postRenderer.resize();
        guiRenderer.resize();
    }
}
