package net.ice.relic.common.console.commands;

import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.Argument;
import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.relic.common.console.nodes.CommandNode;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;
import net.ice.relic.core.cache.ModelCache;
import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

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
				.literal("reload")
						.setActivity(((ctx, args) -> {
							Entity entity = ctx.getCurrentScene().getEntity((String) args.get("name"));
							((GLRenderer) ctx.getCurrentScene().getApplication().getRenderer()).getBufferManager().loadEntity(entity);
						}));

		entityModifyNode
				.literal("components add")
				.addArgument(new ArgumentNode<>("componentName", new Argument.StringArgument()))
				.setActivity((ctx, args) -> {
					Entity entity = ctx.getCurrentScene().getEntity((String) args.get("name"));
					Component component = getNewComponentFromName((String) args.get("componentName"));
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

		entityModifyNode
				.literal("component modify")
				.addArgument(new ArgumentNode<>("componentName", new Argument.StringArgument()))
				.addArgument(new ArgumentNode.StringArgumentNode("fieldName"))
				.addArgument(new ArgumentNode.StringArgumentNode("newValue"))
				.setActivity((ctx, args) -> {
					Entity entity = ctx.getCurrentScene().getEntity((String) args.get("name"));
					Component component = entity.getComponent(getComponentFromName((String) args.get("componentName"), entity).getClass());
					if(component != null) {
						try {
							Field field = component.getClass().getField((String) args.get("fieldName"));
							field.setAccessible(true);
							field.set(entity, ctx.getCurrentScene().getApplication().getModelCache().getModel((String) args.get("newValue")));
						} catch (Exception ignored) {

						}
					}
				});
	}


	private Component getComponentFromName(String name, Entity entity) {
		return switch (name.toLowerCase()) {
			case "transform", "transformcomponent", "transform_component" -> entity.getComponent(TransformComponent.class);
			case "staticmodel", "static_model", "static" -> entity.getComponent(StaticModelComponent.class);
			default -> throw new IllegalStateException("Unexpected value: " + name);
		};
	}

	private Component getNewComponentFromName(String name) {
		return switch (name.toLowerCase()) {
			case "transform", "transformcomponent", "transform_component" -> new TransformComponent();
			case "staticmodel", "static_model", "static" -> new StaticModelComponent(new Model("df", new ArrayList<>(), null));
			default -> throw new IllegalStateException("Unexpected value: " + name);
		};
	}
}
