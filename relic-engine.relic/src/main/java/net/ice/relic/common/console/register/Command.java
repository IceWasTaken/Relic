package net.ice.relic.common.console.register;

import net.ice.heirloom.register.autoregister.Registerable;

public interface Command extends Registerable<Command, CommandRegistry> {

    @Override
    void register(CommandRegistry commandRegistry);

}