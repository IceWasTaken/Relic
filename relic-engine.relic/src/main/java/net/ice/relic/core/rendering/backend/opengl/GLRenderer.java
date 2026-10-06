package net.ice.relic.core.rendering.backend.opengl;

import net.ice.curio.Curio;
import net.ice.curio.graphics.Renderer;
import net.ice.relic.RelicApplication;
import net.ice.relic.core.ecs.entity.Entity;

import net.ice.relic.core.rendering.backend.opengl.framebuffers.ShadowBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.SwapBuffer;
import net.ice.relic.core.rendering.backend.opengl.renderers.*;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.KHRDebug.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS;


public class GLRenderer extends Renderer {

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

    private final RelicApplication relicApplication;


    public GLRenderer(Curio curio, RelicApplication relicApplication) {
        super(curio);
        this.relicApplication = relicApplication;

        this.bufferManager = new BufferManager(this);

        this.sceneRenderer = new GLSceneRenderer(graphicsContext);
        this.shadowRenderer = new GLShadowRenderer(graphicsContext);
        this.lightRenderer = new GLLightRenderer(graphicsContext);

        this.postRenderer = new GLPostRenderer(this);
        this.visualizeRenderer = new GLDebugRenderer(graphicsContext);
        this.guiRenderer = new GLGuiRenderer(graphicsContext);
    }

    public void init() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glEnable(GL_FRAMEBUFFER_SRGB);
        //setupDebugMessageCallback();
        //SystemInfo.logGLInfo();

        this.swapBuffer = new SwapBuffer(curio.getWindow().getFramebufferSize());
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

        glViewport(0, 0, curio.getWindow().getFramebufferSize().x, curio.getWindow().getFramebufferSize().y);

        sceneRenderer.render(this);
        shadowRenderer.render(this);

        lightRenderer.render(this);

        //postRenderer.render();

        glViewport(0, 0, curio.getWindow().getFramebufferSize().x, curio.getWindow().getFramebufferSize().y);

        visualizeRenderer.render(this);
        guiRenderer.render(this);

        bufferManager.sync();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void resizeFramebuffer(Vector2i size) {
        int width = size.x;
        int height = size.y;

        glViewport(0, 0, width, height);

        this.swapBuffer = new SwapBuffer(width, height);

        this.guiRenderer.onFramebufferResize(size);

        this.sceneRenderer.resize(width, height);
        this.lightRenderer.resize(width, height);
        this.postRenderer.resize(width, height);
    }

    @Override
    public void resizeWindow(Vector2i size) {
        this.guiRenderer.onWindowResize(size);
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
        return relicApplication;
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
