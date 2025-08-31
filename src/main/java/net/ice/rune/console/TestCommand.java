package net.ice.rune.console;

public class TestCommand extends Command {

    public TestCommand() {
        super("test");
        addArgument("testInt", new Argument(Argument.ArgumentType.INT));
    }

    @Override
    public void execute() {
        int val1 = getIntArgument("testInt");
        System.out.println("Executed test with value: " + val1);
    }

    @Override
    public String help() {
        return "Usage: test <int>";
    }
}
