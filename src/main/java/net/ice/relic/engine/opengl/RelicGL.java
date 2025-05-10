package net.ice.relic.engine.opengl;

import net.ice.relic.engine.Relic;
import net.ice.relic.engine.Window;
import net.ice.relic.engine.common.*;
import net.ice.relic.engine.common.event.EventManager;
import net.ice.relic.engine.opengl.model.ModelRenderer;
import net.ice.relic.engine.opengl.shader.Shader;
import net.ice.relic.engine.opengl.shader.ShaderBuilder;
import net.ice.relic.engine.opengl.shader.ShaderModule;
import net.ice.relic.engine.opengl.shader.module.EmissiveModule;
import net.ice.relic.engine.opengl.shader.module.LightingModule;
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
import static net.ice.relic.engine.util.ShaderUtil.validateLink;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class RelicGL implements Relic {

    private Window window;
    private Camera camera;
    private EventManager manager;
    private DebugOverlayNew debugOverlay;
    private ModelRenderer cubeRenderer;
    private ModelRenderer planeRenderer;

    AABB ground = new AABB(
            new Vector3f(-1000, -0.1f, -1000),
            new Vector3f(1000, 0, 1000)
    );

    RigidBody cubeBody = new RigidBody(1.0f, new Vector3f(0, 5, 0));

    private int modelShader;

    @Override
    public void init(Window window) {
        this.window = window;
        this.manager = new EventManager();
        this.camera = new Camera(window);
        this.debugOverlay = new DebugOverlayNew(window);
        this.cubeRenderer = new ModelRenderer("models/cube.glb", DEFAULT_FLAGS);
        this.planeRenderer = new ModelRenderer("models/plane.glb", DEFAULT_FLAGS);


        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
//        glFrontFace(GL_CCW);
//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        glfwSwapInterval(1);


        glfwSetFramebufferSizeCallback(window.getWindowHandle(), (windowHandle, width, height) -> {
            if (width > 0 && height > 0) {
                window.setWidth(width);
                window.setHeight(height);
                camera.resize();
            }
        });

        if (glfwGetCurrentContext() != window.getWindowHandle()) {
            throw new RuntimeException("Failed to set OpenGL context.");
        }
    }

    @Override
    public void initShaders() {
        List<ShaderModule> modules = List.of(
                new LightingModule(),
                new TexturingModule(),
                new EmissiveModule()
        );

        String vertexBase = readShaderFile("modelVertexBase.glsl");
        String fragmentBase = readShaderFile("modelFragmentBase.glsl");

        ShaderBuilder builder = new ShaderBuilder(vertexBase, fragmentBase, modules);
        this.modelShader = builder.build();
    }

    @Override
    public void loop() {
        try {
            Logger.debug("Beginning game loop.");
            while (!glfwWindowShouldClose(window.getWindowHandle())) {
                glfwPollEvents();

                window.getClock().updateTime();
                camera.newFrame();
                camera.update(window.getClock());

                // Apply gravity
                cubeBody.applyForce(new Vector3f(0, -9.81f * cubeBody.mass, 0));
                cubeBody.update(window.getClock().getDeltaTime());

                AABB cubeBox = cubeBody.getAABB();
                if (cubeBox.intersects(ground)) {
                    // Collision resolution: snap to top of ground
                    cubeBody.position.y = ground.getMax().y + cubeBody.size / 2f;

                    // Simple bounce (reverse Y velocity with damping)
                    if (cubeBody.velocity.y < 0) {
                        cubeBody.velocity.y *= -0.5f; // damping factor
                    }

                    // Optional: zero small velocities
                    if (Math.abs(cubeBody.velocity.y) < 0.1f) {
                        cubeBody.velocity.y = 0;
                    }
                }

                // === OpenGL Rendering Setup ===
                glViewport(0, 0, window.getWidth(), window.getHeight());
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glClearColor(0.6f, 0.7f, 0.8f, 1.0f);

                glUseProgram(modelShader);

                try (MemoryStack stack = stackPush()) {
                    FloatBuffer modelBuffer = stack.mallocFloat(16);

                    // Shared Uniforms
                    glUniformMatrix4fv(glGetUniformLocation(modelShader, "view"), false, camera.getViewMatrix().get(stack.mallocFloat(16)));
                    glUniformMatrix4fv(glGetUniformLocation(modelShader, "projection"), false, camera.getProjectionMatrix().get(stack.mallocFloat(16)));

                    glUniform3f(glGetUniformLocation(modelShader, "lightPos"), 2.0f, 4.0f, 2.0f);
                    glUniform3f(glGetUniformLocation(modelShader, "viewPos"), camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
                    glUniform3f(glGetUniformLocation(modelShader, "lightColor"), 1.0f, 1.0f, 1.0f);

                    glUniform1i(glGetUniformLocation(modelShader, "texture0"), 0);
                    glUniform1i(glGetUniformLocation(modelShader, "emissiveMap"), 1);

                    int modelLoc = glGetUniformLocation(modelShader, "model");

                    // === Cube ===
                    Matrix4f cubeMatrix = new Matrix4f()
                            .translate(cubeBody.position)
                            .rotate(cubeBody.rotation)
                            .scale(1.0f); // Adjust depending on cube size

                    glUniformMatrix4fv(modelLoc, false, cubeMatrix.get(modelBuffer));
                    cubeRenderer.render(modelShader);

                    // === Plane ===
                    Matrix4f planeMatrix = new Matrix4f()
                            .translate(0, 0, 0) // Adjust if needed
                            .scale(1.0f); // Adjust scale to match plane size

                    glUniformMatrix4fv(modelLoc, false, planeMatrix.get(modelBuffer));
                    planeRenderer.render(modelShader);
                }

                debugOverlay.render(window, camera);
                glfwSwapBuffers(window.getWindowHandle());
            }
            Logger.debug("Ending loop. Closing window.");
        } catch (Exception e) {
            throw new RuntimeException("Error during render loop: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {

    }

    public Camera getCamera() {
        return camera;
    }

    public Window getWindow() {
        return window;
    }

}
