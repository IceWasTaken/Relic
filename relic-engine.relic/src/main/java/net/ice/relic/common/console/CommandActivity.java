package net.ice.relic.common.console;

import java.util.Map;

@FunctionalInterface
public interface CommandActivity {

    default String help() {
        return "This command does not have a help message.";
    }

    void execute(CommandContext ctx, Map<String, Object> args);
}
