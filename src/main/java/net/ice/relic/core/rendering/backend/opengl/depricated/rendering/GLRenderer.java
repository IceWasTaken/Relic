package net.ice.relic.core.rendering.backend.opengl.depricated.rendering;

import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;
import static org.lwjgl.system.APIUtil.apiUnknownToken;

//public class glrenderer {

//    private final RelicApplication application;
//
//    private GLDebugMessageCallback messageCallback;
//
//    private RenderType renderType = RenderType.NORMAL;
//    private RenderType lastFrameRenderType = renderType;
//
//
//    private boolean postShader = false;
//    private boolean reloadShader = false;
//    private GLShaderProgram postShaderProgram;
//
//    public GLRenderer(RelicApplication application) {
//        this.application = application;

//        if (application.getBackendManager() instanceof GLManager glManager) {
//            this.manager = glManager;
//
//            this.shadowRenderer = new ShadowRenderer(manager);
//            this.sceneRenderer = new SceneRenderer(manager);
//            this.animationRenderer = new AnimationRenderer(manager);
//            this.lightRenderer = new LightRenderer(manager);
//            this.skyboxRenderer = new SkyboxRenderer(manager);
//            //this.postRenderer = new PostRenderer(application);
//            this.guiRenderer = new GuiRenderer(manager);
//        } else {
//            throw new RuntimeException("Engine: Attempted to create GL renderer in a non-openGL backend.");
//        }


//    }

//    @Override
//    public void init() {
//        glEnable(GL_MULTISAMPLE);
//        glEnable(GL_DEPTH_TEST);
//        //glEnable(GL_FRAMEBUFFER_SRGB);
//
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//        //this.messageCallback = GLDebugMessageCallback.create(new GLDebugCallback());
//        setupDebugMessageCallback();
//
//        GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
//            if(type == GL_DEBUG_TYPE_ERROR || severity == GL_DEBUG_SEVERITY_HIGH) {
//                StringBuilder sb = new StringBuilder(300);
//
//                sb.append("[Relic] OpenGL error message\n");
//                printDetail(sb, "ID", "0x" + Integer.toHexString(id).toUpperCase());
//                printDetail(sb, "Source", getDebugSource(source));
//                printDetail(sb, "Type", getDebugType(type));
//                printDetail(sb, "Severity", getDebugSeverity(severity));
//                printDetail(sb, "Message", GLDebugMessageCallback.getMessage(length, message));
//
//                DEBUG_STREAM.print(sb);
//            }
//        });
//        glDebugMessageCallback(proc, NULL);
//
//
//
//        manager.getGeometryBuffer().init();
//
//        sceneRenderer.init();
//        skyboxRenderer.init();
//        shadowRenderer.init();
//        lightRenderer.init();
//        animationRenderer.init();
//        guiRenderer.init();
//        //postRenderer.init();
//    }
//
//    @Override
//    public void render() {
//        if(lastFrameRenderType != renderType) {
//            sceneRenderer.changeRenderType(renderType);
//            this.lastFrameRenderType = renderType;
//            setupData();
//        }
//        switch (renderType) {
//            case NORMAL -> renderNormal();
//            case ALBEDO, NORMALS, POS, PBR, DEPTH -> renderNoLighting();
//        }
//    }
//
//    private void renderNormal() {
//        animationRenderer.render();
//
//        manager.getGeometryBuffer().bind(FRAMEBUFFER);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glViewport(0, 0, manager.getGeometryBuffer().getWidth(), manager.getGeometryBuffer().getHeight());
//        glDisable(GL_BLEND);
//        sceneRenderer.render();
//        //shadowRenderer.render();
//        lightRenderStart(manager.getGeometryBuffer());
//
//        lightRenderer.setShadowRenderer(shadowRenderer);
//        lightRenderer.render();
//        skyboxRenderer.render();
//
//        if (postShader && postShaderProgram != null) {
//            //postRenderer.render();
//        }
//
//        lightRenderFinish();
//        guiRenderer.render();
//    }
//
//    private void renderNoLighting() {
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//
//        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());
//
//        animationRenderer.render();
//        sceneRenderer.render();
//        guiRenderer.render();
//    }
//
//    private void lightRenderFinish() {
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//    }
//
//    private void lightRenderStart(GeometryBuffer geometryBuffer) {
//
//        if (postShader && postShaderProgram != null) {
//            //postRenderer.getPostBuffer().bindFrameBuffer();
//        } else {
//            glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        }
//
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());
//
//        glEnable(GL_BLEND);
//        glBlendEquation(GL_FUNC_ADD);
//        glBlendFunc(GL_ONE, GL_ONE);
//
//        geometryBuffer.bind(READ_FRAMEBUFFER);
//    }
//
//
//    @Override
//    public void cleanup() {
//        //sceneRenderer.cleanup();
//    }
//
//    public void setupData() {
//        manager.loadStaticModels();
//        manager.loadAnimatedModels();
//        sceneRenderer.setupData();
//        shadowRenderer.setupData();
//    }
//
//    public void resize() {
//        //postRenderer.resize();
//        guiRenderer.resize();
//    }


//    @Override
//    public void resize() {
//
//    }
//
//    @Override
//    public void setupData() {
//
//    }
//}