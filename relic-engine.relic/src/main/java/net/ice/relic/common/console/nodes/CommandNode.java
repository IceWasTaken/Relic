package net.ice.relic.common.console.nodes;

import net.ice.relic.common.console.CommandActivity;

import java.util.*;

public abstract class CommandNode {

    private final Map<String, CommandNode> children = new LinkedHashMap<>();
    private final List<ArgumentNode<?>> arguments = new ArrayList<>();
    private CommandActivity activity;

    public abstract String getKey();

    protected CommandNode() {
    }

    public CommandNode literal(String key) {
        return addChild(new LiteralNode(key));
    }

    public CommandNode getChild(String key) {
        return children.get(key);
    }
    public CommandNode addChild(CommandNode node) {
        String key = node.getKey();

        if(key == null || key.trim().isEmpty()) {
            return null;
        }

        String[] tokens = key.trim().split("\\s+");

        CommandNode currentParent = this;
        CommandNode currentNode = null;

        for (String token : tokens) {
            CommandNode existing = currentParent.children.get(token);

            if (existing == null) {
                existing = new LiteralNode(token);
                currentParent.children.put(token, existing);
            }

            currentNode = existing;
            currentParent = existing;
        }

        if (node.getActivity() != null) {
            currentNode.setActivity(node.getActivity());
        }

        for (CommandNode grandchild : node.getChildren()) {
            currentNode.addChild(grandchild);
        }

        for(ArgumentNode<?> argumentNode : node.getArguments()) {
            currentNode.addArgument(argumentNode);
        }

        return currentNode;
    }


    public List<ArgumentNode<?>> getArguments() {
        return arguments;
    }
    public CommandNode addArgument(ArgumentNode<?> argumentNode) {
        arguments.add(argumentNode);
        return this;
    }

    public CommandActivity getActivity() {
        return activity;
    }
    public CommandNode setActivity(CommandActivity activity) {
        this.activity = activity;
        return this;
    }

    public Collection<CommandNode> getChildren() {
        return children.values();
    }
}


