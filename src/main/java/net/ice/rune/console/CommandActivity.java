package net.ice.rune.console;

public interface CommandActivity {

    default String help() {
        return "This command does not have a help message.";
    }

    void execute();
}
