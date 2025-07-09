package net.ice.relic.engine;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.common.Clock;
import net.ice.relic.engine.common.Input;
import net.ice.relic.engine.common.event.EventManager;
import net.ice.relic.engine.config.Config;
import net.ice.relic.engine.opengl.rendering.Renderer;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.rune.scenes.SceneTest;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;

@Rewrite
public abstract class RelicApplication implements ApplicationContext {

    private boolean hasInitOpenGL;
    private boolean hasInitVulkan;

    private GLFWErrorCallback glfwErrorCallback;
    private EngineState engineState;

    protected Scene currentScene;
    private final Map<String, Scene> scenes;
    private Callback callback;

    protected EngineState currentState;
    protected final Config config;
    protected final Clock clock;
    protected final Renderer renderer;
    protected final Window window;
    protected final Stats stats;
    protected final Input input;

    protected abstract void init(RelicApplication application, Config config);
    protected abstract void update(RelicApplication application, float deltaTime);
    protected abstract void render(RelicApplication application, Renderer renderer);
    protected abstract void cleanup(RelicApplication application);

    public RelicApplication(Config config) {
        changeState(EngineState.INITIALIZING);

        this.config = config;
        this.clock = new Clock();
        this.stats = new Stats(this);
        this.window = new Window(this);
        this.renderer = new Renderer(this);
        this.input = new Input(this);
        this.scenes = new HashMap<>();
    }

    public void run() {
        try {
            init();
            loop();
        } catch (RuntimeException exception) {
            throw new RuntimeException("An error occurred while running the engine.", exception);
        }
    }

    public void init() throws RuntimeException {


        if(currentState != EngineState.INITIALIZING) {
            throw new RuntimeException("init() called while not in initializing state.");
        }

        Logger.info("Beginning engine initialization.");

        glfwErrorCallback = GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW.");
        }

        Logger.info("Initialized GLFW. (1/4)");

        window.init();
        Logger.info("Created Window. (2/4)");

        renderer.init();
        callback = GLUtil.setupDebugMessageCallback();

        System.out.println("RENDERER DONE.");
        printOpenGLInfo();

        glEnable(GL_DEBUG_OUTPUT);

        hasInitOpenGL = true;
        checkForOpenGLErrors();
        Logger.info("Initialized OpenGL. (3/4)");


        clock.timerInit();

        changeState(EngineState.LOADING);
        currentScene = new SceneTest("default_scene");
        currentScene.setApplication(this);
        currentScene.init();
        input.init();
        renderer.setupData();

        init(this, config);

        Logger.info("Initialized Application. (4/4)");
    }

    public void loop() {
        changeState(EngineState.RUNNING);
        resume();

        while(!window.shouldClose()) {

            clock.updateTime();
            float deltaTime = clock.getDeltaTime();
            this.currentScene.getCamera().newFrame();
            this.currentScene.getCamera().update(deltaTime);
            this.currentScene.getCamera().resize();
            this.update(this, deltaTime);
            renderer.render();
            window.update();
        }
    }

    public void pause() {
        clock.setScale(0);
        changeState(EngineState.PAUSED);
    }

    public void resume() {
        clock.setScale(1);
        changeState(EngineState.RUNNING);
    }

    public void loadScene(Scene scene) {
        if(currentScene != null) {
            if(scene.equals(currentScene)) {
                return;
            } else {
                unloadScene(scene);
            }
        }

        Logger.info("Loading scene: {}", scene.getClass().getSimpleName());

        currentScene = scene;
        changeState(EngineState.LOADING);
        currentScene.init();
        resume();
    }

    public void unloadScene(Scene scene) {
        Logger.info("Unloading scene: {}", scene.getClass().getSimpleName());
        changeState(EngineState.LOADING);

        if(currentScene != null) {
            if(currentScene.isLoaded()) {
                currentScene.destroy();
            }
            currentScene.resetState();
        }
        currentScene = null;
    }

    public static void printOpenGLInfo() {
        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        String version = glGetString(GL_VERSION);
        String glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);

        System.out.println("OpenGL Vendor: " + vendor);
        System.out.println("OpenGL Renderer: " + renderer);
        System.out.println("OpenGL Version: " + version);
        System.out.println("GLSL Version: " + glslVersion);
    }


    private void changeState(EngineState state) {
        if(currentState == state) {
            return;
        }
        currentState = state;
        Logger.info("Engine state changed to: {}", state);
    }

    public void checkForOpenGLErrors() {
        int error = glGetError();
        if(error != 0) {
            Logger.error("OpenGL Error: {}", error);
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public Clock getClock() {
        return clock;
    }

    public Input getInput() {
        return input;
    }
}
