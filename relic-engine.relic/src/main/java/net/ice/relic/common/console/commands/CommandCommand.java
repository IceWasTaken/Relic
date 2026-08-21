package net.ice.relic.common.console.commands;

import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.Argument;
import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.relic.common.console.nodes.CommandNode;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;

@AutoRegister
public class CommandCommand implements Command {

	@Override
	public void register(CommandRegistry registry) {
		CommandNode modifyCommandNode = registry
				.literal("command")
				.addArgument(new ArgumentNode<>("index", new Argument.IntegerArgument()))
				.literal("modify");


		modifyCommandNode.literal("instanceCount")
				.addArgument(new ArgumentNode<>("count", new Argument.IntegerArgument()))
				.setActivity((ctx, args) -> {
//					List<StaticCommandBuffer.DrawCommandStruct> commands = StaticCommandBuffer.COMMANDS;
//					StaticCommandBuffer.DrawCommandStruct command = commands.get((Integer) args.get("index"));

				});

	}
}
