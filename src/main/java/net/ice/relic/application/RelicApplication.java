package net.ice.relic.application;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.relic.EngineState;
import net.ice.relic.core.Stats;
import net.ice.relic.Window;
import net.ice.relic.core.Clock;
import net.ice.relic.core.Input;
import net.ice.relic.core.Version;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.config.Config;
import net.ice.relic.core.rendering.Renderer;
import net.ice.relic.core.modding.ModManager;
import org.joml.Vector2f;
import org.tinylog.Logger;

import static net.ice.relic.EngineState.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;

public abstract class RelicApplication implements ApplicationContext {

    private static final Version ENGINE_VERSION = new Version(0, 3, 0);

    protected Scene currentScene;
    protected EngineState currentState;

    protected final Clock clock;
    protected final Stats stats;
    protected final Input input;
    protected final Config config;
    protected final Window window;
    protected final Renderer renderer;
    protected final ModManager modManager;
    protected final Version applicationVersion;

    protected final ModelCache modelCache;
    protected final TextureCache textureCache;
    protected final MaterialCache materialCache;

    protected abstract void init(RelicApplication application);
    protected abstract void update(RelicApplication application);
    protected abstract void render(RelicApplication application);
    protected abstract void cleanup(RelicApplication application);

    protected RelicApplication(Config config, Version applicationVersion) {
        changeState(INITIALIZING);

        this.config = config;
        this.applicationVersion = applicationVersion;

        this.clock = new Clock();
        this.modelCache = new ModelCache();
        this.textureCache = new TextureCache();
        this.materialCache = new MaterialCache();

        this.stats = new Stats(this);
        this.input = new Input(this);
        this.window = new Window(this);
        this.renderer = new Renderer(this);
        this.modManager = new ModManager(this);
    }

    public void run() {
        try {
            init();
            loop();
        } catch (RuntimeException exception) {
            Logger.error(exception, "Error while initializing application.");
            changeState(ERROR);
        }
    }

    private void init() {
        if(currentState != INITIALIZING) {
            throw new IllegalStateException("Application is not in initializing state.");
        }

        window.init();
        renderer.init();
        textureCache.init();
        //setupDebugMessageCallback();
        logGLCapabilities();
        clock.init();
        changeState(LOADING);
        input.init();
        init(this);
    }

    private void loop() {
        changeState(RUNNING);
        resume();
        while(!window.shouldClose()) {
            clock.updateTime();

            if(currentScene != null) {
                this.currentScene.getCamera().newFrame();
                this.currentScene.getCamera().update(clock.getDeltaTime());
                this.currentScene.update(clock.getDeltaTime());
                this.update(this);
                handleGUI();
                renderer.render();
            }

            window.update();
        }
    }

    public void loadScene(Scene scene) {
        pause();
        this.currentScene = scene;
        currentScene.setApplication(this);
        currentScene.init();
        renderer.setupData();
        resume();
    }

    public void pause() {
        clock.setScale(0);
        changeState(EngineState.PAUSED);
    }

    public void resume() {
        clock.setScale(1);
        changeState(EngineState.RUNNING);
    }

    private void logGLCapabilities() {
        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        String version = glGetString(GL_VERSION);
        String glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);
        int bufferObjectSize = glGetInteger(GL_MAX_SHADER_STORAGE_BLOCK_SIZE);

        Logger.info("OpenGL vendor: " + vendor);
        Logger.info("OpenGL renderer: " + renderer);
        Logger.info("OpenGL version: " + version);
        Logger.info("GLSL version: " + glslVersion);
        Logger.info("Maximum buffer object size: " + bufferObjectSize);
    }

    private void changeState(EngineState state) {
        if(currentState != state) {
            currentState = state;
            Logger.info("Application state changed to: " + currentState);
        }
    }

    private void handleGUI() {
        ImGuiIO imGuiIO = ImGui.getIO();
        Vector2f mousePos = input.getMousePosition();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, input.getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_LEFT));
        imGuiIO.addMouseButtonEvent(1, input.getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_RIGHT));
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

    public Stats getStats() {
        return stats;
    }

    public Config getConfig() {
        return config;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public Input getInput() {
        return input;
    }

    public Version getApplicationVersion() {
        return applicationVersion;
    }

    public static Version getEngineVersion() {
        return ENGINE_VERSION;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public MaterialCache getMaterialCache() {
        return materialCache;
    }

    public ModelCache getModelCache() {
        return modelCache;
    }
}
