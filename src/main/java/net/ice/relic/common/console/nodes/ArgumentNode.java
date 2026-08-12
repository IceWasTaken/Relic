package net.ice.relic.common.console.nodes;

import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.common.console.Argument;

public class ArgumentNode<T> extends CommandNode {

    private final String key;
    private final Argument<T> type;

    public ArgumentNode(String key, Argument<T> type) {
        super();
        this.key = key;
        this.type = type;
    }

    @Override
    public String getKey() {
        return key;
    }

    public Argument<T> getType() {
        return type;
    }

    public static class ResourceArgumentNode extends ArgumentNode<Resource> {
        public ResourceArgumentNode(String key) {
            super(key, new Argument.ResourceArgument());
        }
    }
    public static class StringArgumentNode extends ArgumentNode<String> {
        public StringArgumentNode(String key) {
            super(key, new Argument.StringArgument());
        }
    }
    public static class IntegerArgumentNode extends ArgumentNode<Integer> {
        public IntegerArgumentNode(String key) {
            super(key, new Argument.IntegerArgument());
        }
    }
    public static class FloatArgumentNode extends ArgumentNode<Float> {
        public FloatArgumentNode(String key) {
            super(key, new Argument.FloatArgument());
        }
    }
    public static class BooleanArgumentNode extends ArgumentNode<Boolean> {
        public BooleanArgumentNode(String key) {
            super(key, new Argument.BooleanArgument());
        }
    }
}
