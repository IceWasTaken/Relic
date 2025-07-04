package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.engine.Lifecycle;
import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.opengl.GeometryBuffer;
import net.ice.relic.engine.opengl.model.Model;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL43.*;

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
        createCapabilities();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_FRAMEBUFFER_SRGB);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        application.getWindow().refreshSize();
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        geometryBuffer.getGeometryBuffer().bind(GL_READ_FRAMEBUFFER);
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
