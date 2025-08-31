package net.ice.rune.console;

import java.util.HashMap;
import java.util.Map;

public abstract class Command implements CommandActivity {
    private final String name;
    private final Map<String, Object> arguments = new HashMap<>();

    protected Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected void addArgument(String identifier, Object value) {
        arguments.put(identifier, value);
    }

    protected int getIntArgument(String identifier) {
        Object val = arguments.get(identifier);
        if (val instanceof Integer i) return i;
        throw new IllegalArgumentException("Argument '" + identifier + "' is not an int or not provided.");
    }

    protected String getStringArgument(String identifier) {
        Object val = arguments.get(identifier);
        if (val instanceof String s) return s;
        throw new IllegalArgumentException("Argument '" + identifier + "' is not a string or not provided.");
    }
}
