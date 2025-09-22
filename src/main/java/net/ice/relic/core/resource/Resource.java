package net.ice.relic.core.resource;

import org.lwjgl.BufferUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Resource {

    private final char namespaceSeparator = ':';

    private final String path;
    private final String namespace;
    private static final String defaultNamespace = "relic";

    private Resource(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Resource getResourceWithDefaultNamespace(String path) {
        return new Resource(defaultNamespace, path);
    }

    public static Resource getResource(String namespace, String path) {
        return new Resource(namespace, path);
    }


    public String getPath() {
        return path;
    }

    public String getAsPath() {
        return namespace + "/" + path;
    }

    public String getNamespace() {
        return namespace;
    }

    public ByteBuffer load() {
        try (InputStream inputStream = Resource.class.getClassLoader().getResourceAsStream(getAsPath())) {
            if (inputStream == null) {
                Logger.error("Warning. File not found: {}", getAsPath());
                return null;
            }

            byte[] bytes = inputStream.readAllBytes();
            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            return buffer;
        } catch (IOException e) {
            Logger.error(e);
            return null;
        }
    }
}

