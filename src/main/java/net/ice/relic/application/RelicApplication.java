package net.ice.relic.application;

import net.ice.relic.EngineState;
import net.ice.relic.common.Stats;
import net.ice.relic.Window;
import net.ice.relic.common.Clock;
import net.ice.relic.common.Input;
import net.ice.relic.common.Version;
import net.ice.relic.common.scene.Scene;
import net.ice.relic.config.Config;
import net.ice.relic.engine.opengl.rendering.renderer.Renderer;
import net.ice.relic.modding.ModManager;
import net.ice.rune.scenes.SceneTest;
import org.tinylog.Logger;

import static net.ice.relic.EngineState.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;

public abstract class RelicApplication implements ApplicationContext {

    protected Scene currentScene;
    protected EngineState currentState;

    protected final Config config;
    protected final Window window;
    protected final Renderer renderer;
    protected final Clock clock;
    protected final Stats stats;
    protected final Input input;
    protected final ModManager modManager;
    protected final Version engineVersion;
    protected final Version applicationVersion;

    protected abstract void init(RelicApplication application);
    protected abstract void update(RelicApplication application);
    protected abstract void render(RelicApplication application);
    protected abstract void cleanup(RelicApplication application);

    protected RelicApplication(Config config) {
        changeState(INITIALIZING);

        this.config = config;
        this.stats = new Stats(this);
        this.window = new Window(this);
        this.renderer = new Renderer(this);
        this.input = new Input(this);
        this.modManager = new ModManager(this);
        this.engineVersion = new Version(0, 3, 0);
        this.applicationVersion = new Version(0, 1, 0);
        this.clock = new Clock();
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
        setupDebugMessageCallback();
        logGLCapabilities();
        clock.init();
        changeState(LOADING);
        currentScene = new SceneTest("test", this);
        currentScene.setApplication(this);
        currentScene.init();
        input.init();
        renderer.setupData();

        init(this);
    }

    private void loop() {
        changeState(RUNNING);
        resume();
        while(!window.shouldClose()) {
            clock.updateTime();
            this.currentScene.getCamera().newFrame();
            this.currentScene.getCamera().update(clock.getDeltaTime());
            this.currentScene.update(clock.getDeltaTime());
            this.update(this);
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

    private void logGLCapabilities() {
        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        String version = glGetString(GL_VERSION);
        String glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);

        Logger.info("OpenGL vendor: " + vendor);
        Logger.info("OpenGL renderer: " + renderer);
        Logger.info("OpenGL version: " + version);
        Logger.info("GLSL version: " + glslVersion);
    }

    private void changeState(EngineState state) {
        if(currentState != state) {
            currentState = state;
            Logger.info("Application state changed to: " + currentState);
        }
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

    public Version getEngineVersion() {
        return engineVersion;
    }
}
