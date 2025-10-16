package net.ice.relic.core.rendering.backend.opengl.rendering;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.rendering.backend.opengl.GLManager;
import net.ice.relic.core.rendering.backend.opengl.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.GeometryBuffer;
import net.ice.relic.core.rendering.backend.opengl.rendering.enums.RenderType;
import net.ice.relic.core.rendering.backend.opengl.rendering.renderer.*;

import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GLDebugMessageCallback.getMessage;

public class GLRenderer extends Renderer implements Lifecycle {

    private final AnimationRenderer animationRenderer;
    private final SceneRenderer sceneRenderer;
    private final ShadowRenderer shadowRenderer;
    private final LightRenderer lightRenderer;
    private final SkyboxRenderer skyboxRenderer;
    private final PostRenderer postRenderer;
    private final GuiRenderer guiRenderer;

    private final GLManager manager;
    private final RelicApplication application;

    private RenderType renderType = RenderType.NORMAL;


    private boolean postShader = false;
    private boolean reloadShader = false;
    private GLShaderProgram postShaderProgram;

    public GLRenderer(RelicApplication application) {
        this.application = application;

        if (application.getBackendManager() instanceof GLManager glManager) {
            this.manager = glManager;

            this.shadowRenderer = new ShadowRenderer(manager);
            this.sceneRenderer = new SceneRenderer(manager);
            this.animationRenderer = new AnimationRenderer(manager);
            this.lightRenderer = new LightRenderer(manager);
            this.skyboxRenderer = new SkyboxRenderer(manager);
            this.postRenderer = new PostRenderer(application);
            this.guiRenderer = new GuiRenderer(manager);
        } else {
            throw new RuntimeException("Engine: Attempted to create GL renderer in a non-openGL backend.");
        }


    }


    @Override
    public void init() {
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_FRAMEBUFFER_SRGB);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            throw new RuntimeException("GL DEBUG: \n" + getMessage(length, message));
        }, 0);


        manager.getGeometryBuffer().init();

        sceneRenderer.init();
        skyboxRenderer.init();
        shadowRenderer.init();
        lightRenderer.init();
        animationRenderer.init();
        guiRenderer.init();
        postRenderer.init();
    }

    @Override
    public void render() {
        switch (renderType) {
            case NORMAL -> renderNormal();
            case NO_LIGHTING, NORMAL_MAPS -> renderNoLighting();
        }
    }

    private void renderNormal() {
        animationRenderer.render();

        manager.getGeometryBuffer().bind(GL_FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, manager.getGeometryBuffer().getWidth(), manager.getGeometryBuffer().getHeight());
        glDisable(GL_BLEND);
        sceneRenderer.render();
        shadowRenderer.render();
        lightRenderStart(manager.getGeometryBuffer());

        lightRenderer.setShadowRenderer(shadowRenderer);
        lightRenderer.render();
        skyboxRenderer.render();

        if (postShader && postShaderProgram != null) {
            //postRenderer.render();
        }

        lightRenderFinish();
        guiRenderer.render();
    }

    private void renderNoLighting() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        animationRenderer.render();
        sceneRenderer.render();
        guiRenderer.render();
    }

    private void lightRenderFinish() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void lightRenderStart(GeometryBuffer geometryBuffer) {

        if (postShader && postShaderProgram != null) {
            postRenderer.getPostBuffer().bindFrameBuffer();
        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, geometryBuffer.getFrameBuffer().getFboID());

    }


    @Override
    public void cleanup() {
        //sceneRenderer.cleanup();
    }

    public void setupData() {
        manager.loadStaticModels();
        manager.loadAnimatedModels();
        sceneRenderer.setupData();
        shadowRenderer.setupData();
    }

    public void enablePostShader(GLShaderProgram shaderProgram) {
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

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
        sceneRenderer.changeRenderType(renderType);



    }
}