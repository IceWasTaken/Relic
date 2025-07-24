package net.ice.relic.resource;

public class Resource {

    public static final char SEPARATOR = '.';
    public static final String DEFAULT_NAMESPACE = "relic";

    private final String namespace;
    private final String path;

    private Resource(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String toString() {
        return namespace + SEPARATOR + path;
    }

    public static Resource fromString(String str) {
        String[] resource = str.split("\\.");
        if(resource.length == 1) {
            return new Resource(DEFAULT_NAMESPACE, resource[0]);
        } else {
            return new Resource(resource[0], resource[1]);
        }
    }

    public static Resource fromNamespaceAndPath(String namespace, String path) {
        return new Resource(namespace, path);
    }

    public static Resource fromDefault(String path) {
        return new Resource(DEFAULT_NAMESPACE, path);
    }
}
