package net.ice.relic.application;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.relic.EngineState;
import net.ice.relic.core.Clock;
import net.ice.relic.core.Input;
import net.ice.relic.core.Stats;
import net.ice.relic.core.Version;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.config.Config;
import net.ice.relic.core.modding.ModManager;
import net.ice.relic.core.rendering.backend.BackendManager;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.rendering.backend.opengl.GLManager;
import net.ice.relic.core.rendering.backend.opengl.rendering.GLRenderer;
import net.ice.relic.core.rendering.backend.vulkan.VulkanManager;
import net.ice.relic.core.rendering.backend.vulkan.rendering.VulkanRenderer;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.window.Window;
import net.ice.relic.core.window.backend.GLWindow;
import net.ice.relic.core.window.backend.vulkan.VulkanWindow;
import org.joml.Vector2f;
import org.tinylog.Logger;

import static net.ice.relic.EngineState.*;
import static net.ice.relic.core.system.SystemInfo.logSystemInfo;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public abstract class RelicApplication implements ApplicationContext {

    private static final Version ENGINE_VERSION = new Version(0, 4, 1);

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
    protected final BackendManager backendManager;

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
        this.window = getWindowType();
        this.backendManager = getBackend();
        this.renderer = getRendererType();
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
        backendManager.init();
        renderer.init();
        textureCache.init();
        logSystemInfo();
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

            window.update(clock.getDeltaTime());
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
        changeState(PAUSED);
    }

    public void resume() {
        clock.setScale(1);
        changeState(RUNNING);
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

    private Window getWindowType() {
        return switch (config.getRendererConfig().getBackendType()) {
            case OPENGL -> new GLWindow(this);
            case VULKAN -> new VulkanWindow(this);
        };
    }

    private Renderer getRendererType() {
        return switch (config.getRendererConfig().getBackendType()) {
            case OPENGL -> new GLRenderer(this);
            case VULKAN -> new VulkanRenderer(this);
        };
    }
    
    private BackendManager getBackend() {
        return switch (config.getRendererConfig().getBackendType()) {
            case OPENGL -> new GLManager(this);
            case VULKAN -> new VulkanManager(this);
        };
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

    public BackendManager getBackendManager() {
        return backendManager;
    }
}
