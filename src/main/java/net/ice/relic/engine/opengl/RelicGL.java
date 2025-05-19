package net.ice.relic.engine.opengl;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.Window;
import net.ice.relic.engine.common.event.EventManager;
import net.ice.relic.engine.opengl.model.ModelRenderer;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.shader.ShaderBuilder;
import net.ice.relic.engine.opengl.shader.ShaderModule;
import net.ice.relic.engine.opengl.shader.module.EmissiveModule;
import net.ice.relic.engine.opengl.shader.module.LightingModule;
import net.ice.relic.engine.opengl.shader.module.NormalModule;
import net.ice.relic.engine.opengl.shader.module.TexturingModule;
import net.ice.relic.engine.test.DebugOverlayNew;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.FloatBuffer;
import java.util.List;

import static net.ice.relic.engine.opengl.model.Model.DEFAULT_FLAGS;
import static net.ice.relic.engine.util.IOUtil.readShaderFile;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class RelicGL implements RelicApplication {

    private Window window;
    private Camera camera;
    private EventManager manager;
    private DebugOverlayNew debugOverlay;
    private ModelRenderer cubeRenderer;
    private ModelRenderer planeRenderer;
    private ModelRenderer testRenderer;
    private PhysicsWorld physicsWorld;

    private RigidBody cubeBody;
    private RigidBody groundBody;

    private int modelShader;

    @Override
    public void init(Window window, Scene scene, Renderer renderer) {
        initShaders();

        this.window = window;
        this.manager = new EventManager();
        this.camera = new Camera(window);
        this.debugOverlay = new DebugOverlayNew(window);
        this.testRenderer = new ModelRenderer("EmissiveStrengthTest.gltf", DEFAULT_FLAGS);

        this.physicsWorld = new PhysicsWorld();

        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_CULL_FACE);
        glfwSwapInterval(1);

//        glfwSetFramebufferSizeCallback(window.getWindowHandle(), (windowHandle, width, height) -> {
//            if (width > 0 && height > 0) {
//                window.setWidth(width);
//                window.setHeight(height);
//                camera.resize();
//            }
//        });

        if (glfwGetCurrentContext() != window.getWindowHandle()) {
            throw new RuntimeException("Failed to set OpenGL context.");
        }

        // Setup physics bodies
        cubeBody = new RigidBody(1.0f, new Vector3f(0, 5, 0));
        groundBody = new RigidBody(0.0f, new Vector3f(0, -0.1f, 0));
        groundBody.setStatic(true);
        groundBody.size = 2000.0f; // Large size to match AABB ground

        physicsWorld.addBody(cubeBody);
        physicsWorld.addBody(groundBody);
    }

    public void initShaders() {
        List<ShaderModule> modules = List.of(
                new LightingModule(),
                new TexturingModule(),
                new EmissiveModule(),
                new NormalModule()
        );

        String vertexBase = readShaderFile("modelVertexBase.glsl");
        String fragmentBase = readShaderFile("modelFragmentBase.glsl");

        ShaderBuilder builder = new ShaderBuilder(vertexBase, fragmentBase, modules);
        this.modelShader = builder.build();
    }

    @Override
    public void update(Window window, Scene scene, Renderer renderer) {
        try {
            Logger.debug("Beginning game update.");
            while (!glfwWindowShouldClose(window.getWindowHandle())) {
                glfwPollEvents();

                window.getClock().updateTime();
                float deltaTime = window.getClock().getDeltaTime();

                camera.newFrame();
                camera.update(window.getClock());

                physicsWorld.step(deltaTime);

                //glViewport(0, 0, window.getWidth(), window.getHeight());
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glClearColor(0.6f, 0.7f, 0.8f, 1.0f);

                glUseProgram(modelShader);

                List<EmissiveLight> emissiveLights = List.of(
                        new EmissiveLight(new Vector3f(0, 1, 0), new Vector3f(0.0f, 0.5f, 1.0f), 2.0f),
                        new EmissiveLight(new Vector3f(2.5f, 1, 0), new Vector3f(0.0f, 0.5f, 1.0f), 1f)
                );

                glUseProgram(modelShader);
                glUniform1i(glGetUniformLocation(modelShader, "emissiveLightCount"), emissiveLights.size());

                for (int i = 0; i < emissiveLights.size(); i++) {
                    EmissiveLight light = emissiveLights.get(i);
                    glUniform3f(glGetUniformLocation(modelShader, "emissiveLightPos[" + i + "]"),
                            light.position.x, light.position.y, light.position.z);
                    glUniform3f(glGetUniformLocation(modelShader, "emissiveLightColor[" + i + "]"),
                            light.color.x, light.color.y, light.color.z);
                    glUniform1f(glGetUniformLocation(modelShader, "emissiveLightStrength[" + i + "]"), light.strength);
                }


                try (MemoryStack stack = stackPush()) {
                    FloatBuffer modelBuffer = stack.mallocFloat(16);

                    glUniformMatrix4fv(glGetUniformLocation(modelShader, "view"), false, camera.getViewMatrix().get(stack.mallocFloat(16)));
                    glUniformMatrix4fv(glGetUniformLocation(modelShader, "projection"), false, camera.getProjectionMatrix().get(stack.mallocFloat(16)));

                    glUniform3f(glGetUniformLocation(modelShader, "lightPos"), 2.0f, 4.0f, 2.0f);
                    glUniform3f(glGetUniformLocation(modelShader, "viewPos"), camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
                    glUniform3f(glGetUniformLocation(modelShader, "lightColor"), 1.0f, 1.0f, 1.0f);

                    glUniform1i(glGetUniformLocation(modelShader, "texture0"), 0);
                    glUniform1i(glGetUniformLocation(modelShader, "emissiveMap"), 1);

                    int modelLoc = glGetUniformLocation(modelShader, "model");

                    Matrix4f planeMatrix = new Matrix4f();
                    glUniformMatrix4fv(modelLoc, false, planeMatrix.get(modelBuffer));
                    //planeRenderer.render(modelShader);

                    testRenderer.render(modelShader);
                }

                debugOverlay.render(window, camera);
                glfwSwapBuffers(window.getWindowHandle());
            }
            Logger.debug("Ending update. Closing window.");
        } catch (Exception e) {
            throw new RuntimeException("Error during render update: " + e.getMessage(), e);
        }
    }

    @Override
    public void cleanup() {
        // Cleanup resources if needed
    }

    public Camera getCamera() {
        return camera;
    }

    public Window getWindow() {
        return window;
    }
}
