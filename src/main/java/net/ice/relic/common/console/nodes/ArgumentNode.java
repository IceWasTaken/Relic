package net.ice.relic.common.console.nodes;

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
}
