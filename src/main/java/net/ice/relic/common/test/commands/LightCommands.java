package net.ice.relic.common.test.commands;

import net.ice.heirloom.color.RGBColor;
import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.Argument;
import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;
import org.joml.Vector3f;
import org.tinylog.Logger;

@AutoRegister
public class LightCommands implements Command {

	@Override
	public void register(CommandRegistry commandRegistry) {
		commandRegistry
				.literal("show light pos")
				.addArgument(new ArgumentNode<>("index", new Argument.IntegerArgument()))
				.setActivity(((ctx, args) -> {
					int index = (int) args.get("index");
					Vector3f pos = ctx.getCurrentScene().getLights().get(index).getPosition();
					Logger.info("Light {} position: {}, {}, {}", index, pos.x, pos.y, pos.z);
				}));

		commandRegistry
				.literal("light")
				.addArgument(new ArgumentNode<>("index", new Argument.IntegerArgument()))
				.literal("position")
				.addArgument(new ArgumentNode<>("x", new Argument.FloatArgument()))
				.addArgument(new ArgumentNode<>("y", new Argument.FloatArgument()))
				.addArgument(new ArgumentNode<>("z", new Argument.FloatArgument()))
				.setActivity(((ctx, args) -> {
					int index = (int) args.get("index");
					float x = (float) args.get("x");
					float y = (float) args.get("y");
					float z = (float) args.get("z");
					ctx.getCurrentScene().getLights().get(index).setPosition(new Vector3f(x, y, z));
				}));

		commandRegistry
				.literal("light")
				.addArgument(new ArgumentNode<>("index", new Argument.IntegerArgument()))
				.literal("color")
				.addArgument(new ArgumentNode<>("r", new Argument.FloatArgument()))
				.addArgument(new ArgumentNode<>("g", new Argument.FloatArgument()))
				.addArgument(new ArgumentNode<>("b", new Argument.FloatArgument()))
				.setActivity(((ctx, args) -> {
					int index = (int) args.get("index");
					float r = (float) args.get("r");
					float g = (float) args.get("g");
					float b = (float) args.get("b");
					ctx.getCurrentScene().getLights().get(index).setColor(new RGBColor(r, g, b));
				}));
	}
}
