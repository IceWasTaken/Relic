package net.ice.relic.common.console.register;

import net.ice.heirloom.register.Registry;
import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.nodes.CommandNode;
import net.ice.relic.common.console.nodes.LiteralNode;

public class CommandRegistry extends Registry<Command> {

    public CommandNode literal(String name) {
        return Console.root.addChild(new LiteralNode(name));
    }

}
