package net.ice.relic.core.rendering;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.GeometryBuffer;
import net.ice.relic.core.rendering.backend.opengl.ShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.ReflectionBuffer;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.RefractionBuffer;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.RenderingBuffers;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GLDebugMessageCallback.getMessage;

public class Renderer implements Lifecycle {

    private final RenderingBuffers renderingBuffer;
    private final GeometryBuffer geometryBuffer;
    private final ReflectionBuffer reflectionBuffer;
    private final RefractionBuffer refractionBuffer;

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
        this.renderingBuffer = new RenderingBuffers(application);
        this.geometryBuffer = new GeometryBuffer(application);
        this.reflectionBuffer = new ReflectionBuffer(application);
        this.refractionBuffer = new RefractionBuffer(application);

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
        reflectionBuffer.init();
        refractionBuffer.init();

        sceneRenderer.init(renderingBuffer, geometryBuffer, refractionBuffer, reflectionBuffer);
        skyboxRenderer.init(renderingBuffer, geometryBuffer, refractionBuffer, reflectionBuffer);
        shadowRenderer.init(renderingBuffer, geometryBuffer, refractionBuffer, reflectionBuffer);
        lightRenderer.init(renderingBuffer, geometryBuffer, refractionBuffer, reflectionBuffer);
        animationRenderer.init(renderingBuffer, geometryBuffer, refractionBuffer, reflectionBuffer);
        guiRenderer.init(renderingBuffer, geometryBuffer, refractionBuffer, reflectionBuffer);
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
            //postRenderer.render();
        }

        lightRenderFinish();
        guiRenderer.render();
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
