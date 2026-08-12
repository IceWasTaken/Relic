package net.ice.relic.common.console.commands;

import net.ice.heirloom.io.resource.Resource;
import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.relic.common.console.nodes.CommandNode;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;

@AutoRegister
public class ModelCommand implements Command {

	@Override
	public void register(CommandRegistry commandRegistry) {
		CommandNode modelCommandNode = commandRegistry.literal("model");

		modelCommandNode
				.literal("load")
				.addArgument(new ArgumentNode.ResourceArgumentNode("resource"))
				.addArgument(new ArgumentNode.BooleanArgumentNode("animated"))
				.setActivity(((ctx, args) -> {
					ctx.getCurrentScene().getModelLoader().loadModel(
							(Resource) args.get("resource"),
							(Boolean) args.get("animated")
					);
				}));
	}


}
