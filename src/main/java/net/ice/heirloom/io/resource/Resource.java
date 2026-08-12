package net.ice.heirloom.io.resource;

import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class Resource {

    public static final Resource EMPTY = new Resource();

    private final char namespaceSeparator = ':';

    private final String path;
    private final String namespace;

    private boolean exists = true;

    private Resource(){
        this.path = "";
        this.namespace = "";
        this.exists = false;
    }

    private Resource(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Resource getResource(String namespace, String path) {
        return new Resource(namespace, path);
    }

    public static Resource getResourceFromString(String str) {
        String[] split = str.split(":");

        if(split.length != 2) {
            Logger.warn("Malformed string passed in resource constructor. Returning empty resource.");
            return EMPTY;
        }

        return getResource(split[0], split[1]);
    }


    public String getPath() {
        return path;
    }

    public String getAsPath() {
        return "resources/" +  namespace + "/" + path;
    }

    public String getNamespace() {
        return namespace;
    }

    //fix later
    public File getFromJar() {
        try {
            return new File(Resource.class.getClassLoader().getResource(getAsPath()).toURI());
        } catch (URISyntaxException e) {
            Logger.error(e);
        }
        return null;
    }

    public File getFromFileSystem() {
        return new File(path);
    }

    public ByteBuffer load() {
        if(exists) {
            try (InputStream inputStream = Resource.class.getClassLoader().getResourceAsStream(getAsPath())) {
                if (inputStream == null) {
                    Logger.error("Resource not found: {}", getAsPath());
                    return null;
                }

                byte[] bytes = inputStream.readAllBytes();
                ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
                buffer.put(bytes);
                buffer.flip();

                return buffer;
            } catch (IOException e) {
                Logger.error(e);
                return null;
            }
        }
        return null;
    }



    @Override
    public String toString() {
        return getAsPath();
    }
}

