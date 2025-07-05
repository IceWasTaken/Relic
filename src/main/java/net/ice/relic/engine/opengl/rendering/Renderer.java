package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.engine.Lifecycle;
import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.opengl.GeometryBuffer;
import net.ice.relic.engine.opengl.model.Model;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GLDebugMessageCallback.getMessage;

public class Renderer implements Lifecycle {

    private RenderingBuffer renderingBuffer;

    private final GeometryBuffer geometryBuffer;

    private final AnimationRenderer animationRenderer;
    private final SceneRenderer sceneRenderer;
    private final ShadowRenderer shadowRenderer;
    private final LightRenderer lightRenderer;

    private final RelicApplication application;

    public Renderer(RelicApplication application) {
        this.application = application;
        this.geometryBuffer = new GeometryBuffer(application);
        this.sceneRenderer = new SceneRenderer(application);
        this.shadowRenderer = new ShadowRenderer(application);
        this.animationRenderer = new AnimationRenderer(application);
        this.lightRenderer = new LightRenderer(application);
    }

    @Override
    public void init() {
        glfwMakeContextCurrent(application.getWindow().getWindowHandle());
        createCapabilities();

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            System.err.println("GL DEBUG: " + getMessage(length, message));
        }, 0);

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_FRAMEBUFFER_SRGB);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        IntBuffer maxTextures = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, maxTextures);
        System.out.println("GL_MAX_TEXTURE_IMAGE_UNITS: " + maxTextures.get(0));

        this.renderingBuffer = new RenderingBuffer(application);

        geometryBuffer.init();

        sceneRenderer.init();
        shadowRenderer.init();
        animationRenderer.init();
        lightRenderer.init();

        lightRenderer.setShadowRenderer(shadowRenderer);
    }

    @Override
    public void render() {
        animationRenderer.render(renderingBuffer, geometryBuffer);
        shadowRenderer.render(renderingBuffer, geometryBuffer);
        sceneRenderer.render(renderingBuffer, geometryBuffer);

        // Bind default framebuffer for lighting pass
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        application.getWindow().refreshSize();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        lightRenderer.render(renderingBuffer, geometryBuffer);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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

    public GeometryBuffer getGeometryBuffer() {
        return geometryBuffer;
    }
}
