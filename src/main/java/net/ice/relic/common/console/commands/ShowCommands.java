package net.ice.relic.common.console.commands;

import net.ice.curio.system.SystemInfo;
import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.nodes.LiteralNode;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;

@AutoRegister
public class ShowCommands implements Command {

    @Override
    public void register(CommandRegistry registry) {
        registry.literal("show memory used").setActivity((ctx, args) -> SystemInfo.logUsedMemory());
        registry.literal("show memory unused").setActivity((ctx, args) -> SystemInfo.logUnusedMemory());
    }
}
