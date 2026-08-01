package net.ice.relic.common.console.commands;

import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.Argument;
import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.relic.common.console.nodes.CommandNode;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;
import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import org.tinylog.Logger;

@AutoRegister
public class EntityCommand implements Command {

	@Override
	public void register(CommandRegistry registry) {
		registry
				.literal("entity create")
				.addArgument(new ArgumentNode<>("name", new Argument.StringArgument()))
				.setActivity((ctx, args) -> {
					ctx.getCurrentScene().createEntity((String) args.get("name"));
				});

		CommandNode entityModifyNode = registry.literal("entity").addArgument(new ArgumentNode<>("name", new Argument.StringArgument()));

		entityModifyNode
				.literal("components add")
				.addArgument(new ArgumentNode<>("componentName", new Argument.StringArgument()))
				.setActivity((ctx, args) -> {
					Entity entity = ctx.getCurrentScene().getEntity((String) args.get("name"));
					Component component = getComponentFromName((String) args.get("componentName"));
					entity.addComponent(component);
				});

		entityModifyNode
				.literal("components remove")
				.addArgument(new ArgumentNode<>("componentName", new Argument.StringArgument()))
				.setActivity((ctx, args) -> {
					Entity entity = ctx.getCurrentScene().getEntity((String) args.get("name"));
					for (Component component : entity.getComponents()) {
						if(component.getName().equals((String) args.get("componentName"))) {
							entity.removeComponent(component.getClass());
							return;
						}
					}
				});


		entityModifyNode
				.literal("components list")
				.setActivity((ctx, args) -> {
					Entity entity = ctx.getCurrentScene().getEntity((String) args.get("name"));
					for(Component component : entity.getComponents()) {
						Logger.info(component.getName());
					}
				});
	}


	private Component getComponentFromName(String name) {
		return switch (name.toLowerCase()) {
			case "transform", "transformcomponent", "transform_component" -> new TransformComponent();
			case "staticmodel", "static_model", "static" -> new StaticModelComponent(null);
			default -> throw new IllegalStateException("Unexpected value: " + name);
		};
	}
}
