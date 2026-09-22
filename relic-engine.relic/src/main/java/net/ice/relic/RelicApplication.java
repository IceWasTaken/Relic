package net.ice.relic;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.curio.Curio;
import net.ice.curio.input.Input;
import net.ice.curio.input.enums.MouseButton;
import net.ice.curio.window.Window;
import net.ice.heirloom.application.Application;
import net.ice.heirloom.application.ApplicationProperties;
import net.ice.heirloom.register.RegistrationManager;
import net.ice.relic.core.EngineState;
import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.ConsoleItem;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;
import net.ice.relic.core.Timer;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.scene.Scene;
import org.joml.Vector2f;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static net.ice.relic.core.EngineState.*;

public abstract class RelicApplication extends Application {

    protected Scene currentScene;
    protected EngineState currentState;

    protected final Timer clock;
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
	    super(info);

		checkApplicationProperties(info);
        changeState(INITIALIZING);

        this.clock = new Timer();
        this.registrationManager = new RegistrationManager();

        this.curio = new Curio(this);
        this.renderer = Renderer.getRendererType(this);

        this.modelCache = new ModelCache();
        this.textureCache = new TextureCache(curio.getGraphicsContext());
        this.materialCache = new MaterialCache();

//        curio.getWindow().getWindowProperties().setTitle(info.applicationName());
//        curio.getWindow().refreshName();
    }

    public void run() {
        try {
            initApplication(this);
            updateApplication(this);
            cleanupApplication(this);
        } catch (Exception exception) {
            crashReport(exception);
        }
    }

    @Override
    protected void initApplication(Application application) {
        if(currentState != INITIALIZING) {
            throw new IllegalStateException("[Relic]: Attempted initialization not in initializing state");
        }

        deleteOldCrashLogs();

        registrationManager.openRegistry(Command.class, new CommandRegistry());
        registrationManager.register("net.ice.relic.common.console.commands");
        registrationManager.register(this.getClass().getPackageName());
        registrationManager.closeRegistry(Command.class);

        ImGui.createContext();

        curio.init();
        renderer.init();
        textureCache.init();
        clock.init();
        changeState(LOADING);

        init(this);
    }

    @Override
    protected void updateApplication(Application application) {
        changeState(RUNNING);
        resume();
        while(!curio.getWindow().shouldClose()) {
            clock.newFrame();

            if(currentScene != null) {
                if(getWindow().shouldResize()) {
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

    @Override
    protected void cleanupApplication(Application application) {

    }

    public void loadScene(Scene scene) {
        pause();
        this.currentScene = scene;
        this.currentScene.init();
        resume();
    }

    public void pause() {
        clock.setScale(0);
    }

    public void resume() {
        clock.setScale(1);
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
        imGuiIO.addMouseButtonEvent(0, Input.getInstance().getMouseButtonsDown().contains(MouseButton.BUTTON_LEFT));
        imGuiIO.addMouseButtonEvent(1, Input.getInstance().getMouseButtonsDown().contains(MouseButton.BUTTON_RIGHT));
    }

    private void crashReport(Exception exception) {
        Logger.error("[Relic]: Error while running application: ", exception);
        File file;
        try {
            if((file = new File(System.currentTimeMillis() + "-crash.log")).createNewFile()) {
                try(FileWriter writer = new FileWriter(file)) {
                    writer.append("---- Relic Crash Report ----").append("\n\n");
                    writer.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))).append("\n");
                    writer.append("Description: ").append(exception.getMessage()).append("\n\n");

                    writer.append(exception.toString()).append("\n");
                    for(StackTraceElement element : exception.getStackTrace()) {
                        writer.append("    at ").append(element.toString()).append("\n");
                    }
                    writer.append("\n");

                    writer.append("-- System Info --\n");
                    writer.append("Relic Version: ").append(Relic.getVersion().toString()).append("\n");
                    writer.append("Application Version").append(properties.applicationVersion().toString()).append("\n");
                    writer.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
                    writer.append("Operating System Version: ").append(System.getProperty("os.version")).append("\n");
                    writer.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
                    writer.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
                    writer.append("Java VM Version: ").append(System.getProperty("java.vm.specification.version")).append("\n");
                    writer.append("Java VM Name: ").append(System.getProperty("java.vm.name")).append("\n");
                    writer.append("Java VM Vendor: ").append(System.getProperty("java.vm.vendor")).append("\n");
                    writer.append("Memory Heap: ").append(Long.toString(Runtime.getRuntime().maxMemory() / (1024 * 1024))).append("\n");
                    writer.append("CPUs: ").append(Integer.toString(Runtime.getRuntime().availableProcessors())).append("\n");
                    writer.append("\n");

                    writer.append("--- Log History ---").append("\n");
                    for(ConsoleItem consoleItem : Console.consoleItems) {
                        writer.append(consoleItem.getData());
                    }
                }
                throw exception;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkApplicationProperties(ApplicationProperties properties) {
        Logger.info("[Relic]: Loading application: '{}'", properties.applicationName());

        if(properties.targetVersion().isNewer(Relic.getVersion())) {
            Logger.info("[Relic]: Application '{}' is expecting a newer engine version than current version. Expected: {} - Current: {}", properties.applicationName(), properties.targetVersion().toString(), Relic.getVersion());
        }

        if(properties.targetVersion().isOlder(Relic.getVersion())) {
            Logger.info("[Relic]: Application '{}' is expecting an older engine version than current version. Expected: {} - Current: {}", properties.applicationName(), properties.targetVersion().toString(), Relic.getVersion());
        }

        if(properties.debug()) {
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

    public Window getWindow() {
        return curio.getWindow();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Timer getClock() {
        return clock;
    }

    public Scene getCurrentScene() {
        return currentScene;
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
