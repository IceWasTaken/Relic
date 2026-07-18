package net.ice.relic.common.console.commands;

import net.ice.relic.common.console.Argument;
import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.common.console.register.Command;
import net.ice.relic.common.console.register.CommandRegistry;

@AutoRegister
public class TeleportCommand implements Command {

    @Override
    public void register(CommandRegistry registry) {
        registry.literal("tp")
                .addArgument(new ArgumentNode<>("x", new Argument.FloatArgument()))
                .addArgument(new ArgumentNode<>("y", new Argument.FloatArgument()))
                .addArgument(new ArgumentNode<>("z", new Argument.FloatArgument()))
                .setActivity((ctx, args) -> {
                    ctx.getCurrentScene().getCamera().setPosition((Float) args.get("x"), (Float) args.get("y"), (Float) args.get("z"));
                });
    }

}
