package net.ice.relic.common.console.commands;

import net.ice.curio.system.SystemInfo;
import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.nodes.LiteralNode;

public class ShowCommands {

    public ShowCommands() {

        Console.root.addChild(new LiteralNode("show memory used").setActivity((ctx, args) -> SystemInfo.logUsedMemory()));
        Console.root.addChild(new LiteralNode("show memory unused").setActivity((ctx, args) -> SystemInfo.logUnusedMemory()));
    }
}
