package net.ice.rune.console;

import java.util.ArrayList;
import java.util.List;

public class Console {

    private CommandHistory commandHistory;
    private List<Command> registeredCommands;
    private List<ConsoleItem> consoleItems;

    public Console() {
        this.commandHistory = new CommandHistory();
        this.consoleItems = new ArrayList<>();
        this.registeredCommands = new ArrayList<>();
    }

    public void runCommand(String input) {
        if (input == null || input.isBlank()) return;

        commandHistory.add(input);
        consoleItems.add(new ConsoleItem(ConsoleItem.ItemType.COMMAND, input));

        boolean executed = parseCommand(input);

        if (!executed) {
            consoleItems.add(new ConsoleItem(ConsoleItem.ItemType.ERROR, "Unknown command: " + input));
        }
    }

    private boolean parseCommand(String input) {
        String[] split = input.trim().split("\\s+");
        String commandName = split[0];
        //int commandArg1 = Integer.parseInt(split[1]);

        for (Command cmd : registeredCommands) {
            if (cmd.getName().equalsIgnoreCase(commandName)) {
                try {
                    cmd.execute();
                    consoleItems.add(new ConsoleItem(ConsoleItem.ItemType.LOG, "Executed: " + cmd.getName()));
                } catch (Exception e) {
                    consoleItems.add(new ConsoleItem(ConsoleItem.ItemType.ERROR, "Error: " + e.getMessage()));
                }
                return true;
            }
        }
        return false;
    }

    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    public List<ConsoleItem> getConsoleItems() {
        return consoleItems;
    }

    public void registerCommand(Command command) {
        this.registeredCommands.add(command);
    }
}
