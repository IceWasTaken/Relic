package net.ice.relic.engine.util;

import net.ice.relic.Relic;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class IOUtil {

    public static String readShaderFile(String fileName) {
        return readFileContents("shaders/" + fileName, StandardCharsets.UTF_8);
    }

    public static String readModelFile(String fileName) {
        return readFileContents("models/" + fileName, StandardCharsets.UTF_8);
    }

    public static String readFileContents(String path, Charset charset) {
        Logger.debug("Attempting to read file: net/ice/relic/" + path);
        try {
            List<String> fileLines = Files.readAllLines(
                    Path.of(
                            new File(Relic.class.getClassLoader().getResource("net/ice/relic/" + path).getFile()).toURI()
                    ),
                    charset
            );

            StringBuilder builder = new StringBuilder();
            for(String str : fileLines) {
                builder.append(str).append("\n");
            }

            return builder.toString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to read file (or file not found): " + path, exception);
        }
    }
}
