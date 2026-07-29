package net.ice.relic.application;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.curio.Curio;
import net.ice.curio.input.Input;
import net.ice.curio.window.Window;
import net.ice.heirloom.ApplicationProperties;
import net.ice.heirloom.Version;
import net.ice.heirloom.register.RegistrationManager;
import net.ice.relic.EngineState;
import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.ConsoleItem;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;
import net.ice.relic.core.Stats;
import net.ice.relic.core.Timer;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.scene.Scene;
import org.joml.Vector2f;
import org.lwjgl.system.Configuration;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.ice.relic.EngineState.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public abstract class RelicApplication implements ApplicationContext {

    private static final Version RELIC_VERSION = new Version(0, 5, 1);

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
        checkApplicationProperties(info);
        changeState(INITIALIZING);

        this.applicationProperties = info;

        this.clock = new Timer();
        this.stats = new Stats(this);
        this.registrationManager = new RegistrationManager();

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
            crashReport(exception);
        }
    }

    private void init() {
        if(currentState != INITIALIZING) {
            throw new IllegalStateException("[Relic]: Attempted initialization not in initializing state");
        }

        deleteOldCrashLogs();

        registrationManager.openRegistry(Command.class, new CommandRegistry());
        registrationManager.register("net.ice.relic");
        registrationManager.register(this.getClass().getPackageName());

        Configuration.DEBUG.set(true);

        curio.init();
        renderer.init();
        textureCache.init();
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
        //renderer.setupData();
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
            Logger.info("[Relic]: State changed to: " + currentState);
        }
    }


    private void handleGUI() {
        ImGuiIO imGuiIO = ImGui.getIO();
        Vector2f mousePos = Input.getInstance().getMousePosition();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, Input.getInstance().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_LEFT));
        imGuiIO.addMouseButtonEvent(1, Input.getInstance().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_RIGHT));
    }

    private void crashReport(Exception exception) {
        Logger.error("[Relic]: Error while running application: ", exception);
        File file;
        try {
            if((file = new File(System.currentTimeMillis() + "-crash.log")).createNewFile()) {
                try(FileWriter writer = new FileWriter(file)) {
                    writer.append("---- Relic Crash Report ----\n");
                    writer.append(exception.toString()).append("\n");
                    for(StackTraceElement element : exception.getStackTrace()) {
                        writer.append(element.toString()).append("\n");
                    }
                    writer.append("\n");

                    writer.append("===TERMINAL LOG===").append("\n");
                    for(ConsoleItem consoleItem : Console.consoleItems) {
                        writer.append(consoleItem.getData());
                    }
                    writer.append("============================");
                }
                throw exception;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkApplicationProperties(ApplicationProperties properties) {
        Logger.info("[Relic]: Loading application: '{}'", properties.applicationName());

        if(properties.targetRelicVersion().isNewer(RELIC_VERSION)) {
            Logger.info("[Relic]: Application '{}' is expecting a newer engine version than current version. Expected: {} - Current: {}", properties.applicationName(), properties.targetRelicVersion().toString(), RELIC_VERSION.toString());
        }

        if(properties.targetRelicVersion().isOlder(RELIC_VERSION)) {
            Logger.info("[Relic]: Application '{}' is expecting an older engine version than current version. Expected: {} - Current: {}", properties.applicationName(), properties.targetRelicVersion().toString(), RELIC_VERSION.toString());
        }

        if(properties.debugMode()) {
            Logger.info("[Relic]: Debugging for application '{}' enabled", properties.applicationName());
        }
    }

    private void deleteOldCrashLogs() {
        Path workingPath = Paths.get(System.getProperty("user.dir"));
        try(Stream<Path> files = Files.walk(workingPath)) {
            files.forEach((path) -> {
                if(path.getFileName().toString().endsWith("-crash.log")) {
                    String fileName = path.getFileName().toString();
                    Logger.info("[Relic]: Deleting old crash log: {}", fileName);
                    if(path.toFile().delete()) {
                        Logger.info("[Relic]: Deleted crash log: {}", fileName);
                    } else {
                        Logger.info("[Relic]: Failed to delete crash log: {}", fileName);
                    }
                }
            });
        } catch (Exception e) {
            Logger.error("[Relic]: Error while attempting to delete old crash files:", e);
        }

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

    public static Version getRelicVersion() {
        return RELIC_VERSION;
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
