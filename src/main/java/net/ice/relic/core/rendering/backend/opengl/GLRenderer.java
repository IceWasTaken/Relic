package net.ice.relic.core.rendering.backend.opengl;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.ecs.entity.Entity;

import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.GeometryBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.ShadowBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.SwapBuffer;
import net.ice.relic.core.rendering.backend.opengl.renderers.*;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;

public class GLRenderer extends Renderer implements Lifecycle {

    public static final Vector2i SHADOW_MAP_SIZE = new Vector2i(4096);

    private GeometryBuffer geometryBuffer;
    private ShadowBuffer shadowBuffer;
    private SwapBuffer lightBuffer;
    private SwapBuffer swapBuffer;

    private final BufferManager bufferManager;

    private final GLSceneRenderer sceneRenderer;
    private final GLShadowRenderer shadowRenderer;
    private final GLLightRenderer lightRenderer;

    private final GLBloomRenderer bloomRenderer;

    private final GLPostRenderer postRenderer;
    private final GLDebugRenderer visualizeRenderer;
    private final GLGuiRenderer guiRenderer;


    @Override
    public void resize(int width, int height) {
        this.geometryBuffer = new GeometryBuffer(width, height);
        this.lightBuffer = new SwapBuffer(width, height);
        this.swapBuffer = new SwapBuffer(width, height);

        this.guiRenderer.onResize(width, height);

        this.sceneRenderer.resize(width, height);
        this.lightRenderer.resize(width, height);
        this.postRenderer.resize(width, height);
        this.bloomRenderer.resize(width, height);
    }

    @Override
    public void setupData() {

    }

    public GLRenderer(RelicApplication relicApplication) {
        super(relicApplication);
        this.bufferManager = new BufferManager(this);

        this.sceneRenderer = new GLSceneRenderer(this);
        this.shadowRenderer = new GLShadowRenderer(this);
        this.lightRenderer = new GLLightRenderer(this);

        this.bloomRenderer = new GLBloomRenderer(this);

        this.postRenderer = new GLPostRenderer(this);
        this.visualizeRenderer = new GLDebugRenderer(this);
        this.guiRenderer = new GLGuiRenderer(this);
    }

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glEnable(GL_FRAMEBUFFER_SRGB);
        setupDebugMessageCallback();
        //SystemInfo.logGLInfo();

        this.geometryBuffer = new GeometryBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.lightBuffer = new SwapBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.swapBuffer = new SwapBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.shadowBuffer = new ShadowBuffer();

        bufferManager.init();

        sceneRenderer.init();
        shadowRenderer.init();
        lightRenderer.init();
        bloomRenderer.init();

        postRenderer.init();

        visualizeRenderer.init();
        guiRenderer.init();
    }

    @Override
    public void render() {
        bufferManager.update();

        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        sceneRenderer.render();
        shadowRenderer.render();

        //lightBuffer.bind();
        lightRenderer.render();
        //lightBuffer.unbind();

        //bloomRenderer.render();

        //postRenderer.render();

        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        visualizeRenderer.render();
        guiRenderer.render();

        bufferManager.sync();
    }


    public GeometryBuffer getGeometryBuffer() {
        return geometryBuffer;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }

    public SwapBuffer getSwapBuffer() {
        return swapBuffer;
    }

    public SwapBuffer getLightBuffer() {
        return lightBuffer;
    }

    public RelicApplication getApplication() {
        return application;
    }

    public GLShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }

    public GLPostRenderer getPostRenderer() {
        return postRenderer;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public record AnimMeshDrawData(Entity entity, int bindingPoseOffset, int weightsOffset) { }
}
