package net.ice.relic.core.rendering.pipeline;

import java.util.function.Consumer;

public class PipelineCommand {

    private String name;
    private Consumer<Void> command;

    public PipelineCommand(String name, Consumer<Void> command) {
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public Consumer<Void> getCommand() {
        return command;
    }
}
