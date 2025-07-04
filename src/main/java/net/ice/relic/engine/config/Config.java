package net.ice.relic.engine.config;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.config.configs.RendererConfig;
import net.ice.relic.engine.config.configs.WindowConfig;
import org.tinylog.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Rewrite
public class Config {

    private final WindowConfig windowConfig;
    private final RendererConfig rendererConfig;

    public Config() {
        this.windowConfig = new WindowConfig().loadConfig();
        this.rendererConfig = new RendererConfig().loadConfig();
    }

    public static String readFileContents(String path, Charset charset) {
        Logger.debug("Attempting to read file: net/ice/relic/" + path);
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found in JAR: " + path);
            }

            // Read the content from the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to read file (or file not found): " + path, exception);
        }
    }

    public WindowConfig getWindowConfig() {
        return windowConfig;
    }

    public RendererConfig getRendererConfig() {
        return rendererConfig;
    }
}
