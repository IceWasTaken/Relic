package net.ice.relic.core.rendering.backend.opengl;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.RelicApplication;
import net.ice.relic.core.ecs.entity.Entity;

import net.ice.relic.core.rendering.backend.Renderer;
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

    private ShadowBuffer shadowBuffer;
    private SwapBuffer swapBuffer;

    private final BufferManager bufferManager;

    private final GLSceneRenderer sceneRenderer;
    private final GLShadowRenderer shadowRenderer;
    private final GLLightRenderer lightRenderer;

    private final GLPostRenderer postRenderer;
    private final GLDebugRenderer visualizeRenderer;
    private final GLGuiRenderer guiRenderer;


    @Override
    public void resize(int width, int height) {
        glViewport(0, 0, width, height);

        this.swapBuffer = new SwapBuffer(width, height);

        this.guiRenderer.onResize(width, height);

        this.sceneRenderer.resize(width, height);
        this.lightRenderer.resize(width, height);
        this.postRenderer.resize(width, height);
    }

    public GLRenderer(RelicApplication relicApplication) {
        super(relicApplication);
        this.bufferManager = new BufferManager(this);

        this.sceneRenderer = new GLSceneRenderer(this);
        this.shadowRenderer = new GLShadowRenderer(this);
        this.lightRenderer = new GLLightRenderer(this);

        this.postRenderer = new GLPostRenderer(this);
        this.visualizeRenderer = new GLDebugRenderer(this);
        this.guiRenderer = new GLGuiRenderer(this);
    }

    @Override
    public void init() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glEnable(GL_FRAMEBUFFER_SRGB);
        //setupDebugMessageCallback();
        //SystemInfo.logGLInfo();

        this.swapBuffer = new SwapBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.shadowBuffer = new ShadowBuffer();

        bufferManager.init();

        sceneRenderer.init();
        shadowRenderer.init();
        lightRenderer.init();

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

        lightRenderer.render();

        //postRenderer.render();

        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        visualizeRenderer.render();
        guiRenderer.render();

        bufferManager.sync();
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }

    public GLSceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public SwapBuffer getSwapBuffer() {
        return swapBuffer;
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
