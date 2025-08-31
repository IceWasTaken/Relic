package net.ice.rune.console;

public class Argument {

    private ArgumentType type;
    private boolean optional;
    private Object value;

    public Argument(ArgumentType type) {
        this.type = type;
    }

    public Argument optional() {
        this.optional = true;
        return this;
    }

    public ArgumentType getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setValue(Object v) {
        this.value = v;
    }

    public Object getValue() {
        return value;
    }

    public enum ArgumentType {
        STRING(String.class, "String"),
        BOOLEAN(Boolean.class, "Boolean"),
        INT(Integer.class, int.class, "Int"),
        FLOAT(Float.class, float.class, "Float");

        private final Class<?>[] supportedTypes;
        private final String name;

        ArgumentType(Class<?> typeClass, String asString) {
            this.supportedTypes = new Class<?>[]{ typeClass };
            this.name = asString;
        }

        ArgumentType(Class<?> typeClass, Class<?> typeClass2, String asString) {
            this.supportedTypes = new Class<?>[]{ typeClass, typeClass2 };
            this.name = asString;
        }

        public boolean isSupported(Class<?> clss) {
            for (Class<?> c : supportedTypes) {
                if (c.equals(clss)) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
