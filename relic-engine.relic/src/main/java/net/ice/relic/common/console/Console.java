package net.ice.relic.common.console;

import net.ice.relic.common.console.nodes.ArgumentNode;
import net.ice.relic.common.console.nodes.CommandNode;
import net.ice.relic.common.console.nodes.RootNode;
import net.ice.relic.common.console.register.CommandRegistry;

import java.util.*;

import static net.ice.relic.common.console.ConsoleItem.ItemType.COMMAND;
import static net.ice.relic.common.console.ConsoleItem.ItemType.ERROR;

public class Console {

    public static CommandHistory commandHistory = new CommandHistory();
    public static LinkedList<ConsoleItem> consoleItems = new LinkedList<>();

    public static CommandRegistry commandRegistry = new CommandRegistry();

    public static RootNode root = new RootNode();

    public static void runCommand(String command, CommandContext ctx) {
        //Logger.info("Issued command: {}", command);
        consoleItems.add(new ConsoleItem(COMMAND, command));

        if(command == null || command.isBlank()) return;

        String[] tokens = command.trim().split("\\s+");

        CommandNode current = root;
        Map<String, Object> arguments = new HashMap<>();

        try {
            for(int i = 0; i < tokens.length; i++) {
                String token = tokens[i];

                //exact match
                CommandNode next = current.getChild(token);

                //literal match fail
                if(next == null) {
                    List<CommandNode> matches = findViaPrefix(token, current);

                    if(matches.size() == 1) {
                        current = matches.getFirst();
                        continue;
                    } else if (matches.size() > 1){
                        error("Ambiguous command: '" + command + "'");
                    }

                    List<ArgumentNode<?>> expectedArguments = current.getArguments();

                    if (expectedArguments == null || expectedArguments.isEmpty()) {
                        error("Unknown command '" + token + "' at position " + i);
                        return;
                    }

                    boolean parsed = false;

                    for (ArgumentNode<?> argNode : expectedArguments) {
                        String key = argNode.getKey();

                        if (arguments.containsKey(key)) {
                            continue;
                        }

                        try {
                            Object value = argNode.getType().parse(token);
                            arguments.put(key, value);
                            parsed = true;
                            break;
                        } catch (Exception ignored) {

                        }
                    }

                    if (!parsed) {
                        error("Invalid argument at position " + i + ": '" + token + "'");
                        return;
                    }

                    continue;

                }
                current = next;
            }

            if(current.getActivity() == null) {
                error("Incomplete command. Missing endpoint at '" + current.getKey() + "'");
                return;
            }

            List<ArgumentNode<?>> expectedArgs = current.getArguments();

            if(expectedArgs != null) {
                for(ArgumentNode<?> node : expectedArgs) {
                    if(!arguments.containsKey(node.getKey())) {
                        error("Missing required argument: <" + node.getKey() + ">");
                        return;
                    }
                }
            }

            current.getActivity().execute(ctx, arguments);
        } catch (Exception e) {
            error("Internal command error: " + e.getMessage());
        }
    }
    

    public static List<CommandNode> findViaPrefix(String token, CommandNode node) {
        List<CommandNode> matches = new ArrayList<>();

        for(CommandNode child : node.getChildren()) {
            if(child.getKey().startsWith(token)) {
                matches.add(child);
            }
        }
        return matches;
    }

    private static void error(String message) {
        consoleItems.add(new ConsoleItem(ERROR, message));
    }

    static {
    }
}
