package net.ice.relic.common.console.nodes;

public class LiteralNode extends CommandNode {

    private final String key;

    public LiteralNode(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
