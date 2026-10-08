package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.Curio;
import net.ice.curio.graphics.Renderer;
import net.ice.relic.RelicApplication;
import net.ice.relic.core.ecs.entity.Entity;

import net.ice.relic.core.rendering.backend.opengl.BufferManager;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.ShadowBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.SwapBuffer;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.KHRDebug.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS;


public class GLRenderer extends Renderer {

    public static final Vector2i SHADOW_MAP_SIZE = new Vector2i(4096);

    private ShadowBuffer shadowBuffer;
    private SwapBuffer swapBuffer;

    private final BufferManager bufferManager;

    final GLSceneRenderer sceneRenderer;
    final GLShadowRenderer shadowRenderer;
    final GLLightRenderer lightRenderer;
    final GLParticleRenderer particleRenderer;
    final GLSwapRenderer swapRenderer;

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
        this.particleRenderer = new GLParticleRenderer(graphicsContext);
        this.swapRenderer = new GLSwapRenderer(graphicsContext);

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
        particleRenderer.init();
        swapRenderer.init();

        postRenderer.init();

        visualizeRenderer.init();
        guiRenderer.init();
    }

    @Override
    public void render() {

        bufferManager.update();

        Vector2i fbSize = curio.getWindow().getFramebufferSize();
        glViewport(0, 0, fbSize.x, fbSize.y);

        sceneRenderer.render(this);
        shadowRenderer.render(this);
        lightRenderer.render(this);
        particleRenderer.render(this.getApplication().getParticleSystem(), relicApplication.getClock().getDeltaTime(), this);
        swapRenderer.render(this);
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
        this.swapRenderer.resize(width, height);
    }

    @Override
    public void resizeWindow(Vector2i size) {
        this.guiRenderer.onWindowResize(size);
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }

    public RelicApplication getApplication() {
        return relicApplication;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public record AnimMeshDrawData(Entity entity, int bindingPoseOffset, int weightsOffset) { }
}
