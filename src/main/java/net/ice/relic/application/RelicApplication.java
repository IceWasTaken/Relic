package net.ice.relic.application;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.curio.Curio;
import net.ice.curio.input.Input;
import net.ice.curio.window.Window;
import net.ice.heirloom.ApplicationProperties;
import net.ice.heirloom.register.RegistrationManager;
import net.ice.relic.EngineState;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;
import org.joml.Vector2f;
import org.tinylog.Logger;
import net.ice.relic.core.Timer;
import net.ice.relic.core.Stats;
import net.ice.heirloom.Version;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.scene.Scene;
import org.lwjgl.system.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static net.ice.relic.EngineState.*;
import static net.ice.curio.system.SystemInfo.logSystemInfo;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public abstract class RelicApplication implements ApplicationContext {

    private static final Version ENGINE_VERSION = new Version(0, 5, 0);

    protected final ApplicationProperties applicationProperties;

    protected Scene currentScene;
    protected EngineState currentState;

    protected final Timer clock;
    protected final Stats stats;
    protected final RegistrationManager registrationManager;

    protected final Curio curio;
    protected final Renderer renderer;

    protected final ModelCache modelCache;
    protected final TextureCache textureCache;
    protected final MaterialCache materialCache;

    protected abstract void init(RelicApplication application);
    protected abstract void update(RelicApplication application);
    protected abstract void render(RelicApplication application);
    protected abstract void cleanup(RelicApplication application);

    protected RelicApplication(ApplicationProperties info) {
        changeState(INITIALIZING);

        this.applicationProperties = info;

        this.clock = new Timer();
        this.registrationManager = new RegistrationManager();
        this.stats = new Stats(this);
        this.curio = new Curio(info);
        this.renderer = Renderer.getRendererType(this);

        this.modelCache = new ModelCache();
        this.textureCache = new TextureCache(curio.getGraphicsContext());
        this.materialCache = new MaterialCache();
    }



    public void run() {
        try {
            init();
            loop();
        } catch (Exception exception) {
            Logger.error("Error while initializing application: ", exception);
            changeState(ERROR);
            File file;
            try {
                if((file = new File(System.currentTimeMillis() + "-crash.log")).createNewFile()) {
                    try(FileWriter writer = new FileWriter(file)) {
                        writer.append(exception.getMessage()).append("\n");
                        for(StackTraceElement element : exception.getStackTrace()) {
                            writer.append(element.toString()).append("\n");
                        }
                    }
                    throw exception;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void init() {
        if(currentState != INITIALIZING) {
            throw new IllegalStateException("Application is not in initializing state");
        }

        registrationManager.openRegistry(Command.class, new CommandRegistry());
        registrationManager.register("net.ice.relic");
        registrationManager.register(this.getClass().getPackageName());

        Configuration.DEBUG.set(true);

        curio.init();
        renderer.init();
        textureCache.init();
        logSystemInfo();
        clock.init();
        changeState(LOADING);

        init(this);
    }

    private void loop() {
        changeState(RUNNING);
        resume();
        while(!curio.getWindow().shouldClose()) {
            clock.updateTime();

            if(currentScene != null) {
                if(getWindow().getWindow().isResized()) {
                    renderer.resize(getWindow().getWidth(), getWindow().getHeight());
                }

                this.currentScene.update(clock.getDeltaTime());
                Input.update();
                this.update(this);
                if(currentScene.getGUI() != null) {
                    handleGUI();
                }
                renderer.render();
            }

            curio.getWindow().update(clock.getDeltaTime());
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
        Vector2f mousePos = Input.getInstance().getMousePosition();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, Input.getInstance().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_LEFT));
        imGuiIO.addMouseButtonEvent(1, Input.getInstance().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_RIGHT));
    }

    public Curio getCurio() {
        return curio;
    }

    @Override
    public Window getWindow() {
        return curio.getWindow();
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public Timer getClock() {
        return clock;
    }

    public Stats getStats() {
        return stats;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }


    public ApplicationProperties getApplicationInfo() {
        return applicationProperties;
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

//    public BackendManager getBackendManager() {
//        return backendManager;
//    }

}
