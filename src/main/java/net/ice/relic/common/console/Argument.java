package net.ice.relic.common.console;

public interface Argument<T> {

    T parse(String input);

    public class StringArgument implements Argument<String> {

        @Override
        public String parse(String input) {
            return input;
        }

    }
    public class IntegerArgument implements Argument<Integer> {

        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }

    }
    public class FloatArgument implements Argument<Float> {

        @Override
        public Float parse(String input) {
            return Float.parseFloat(input);
        }

    }
    public class BooleanArgument implements Argument<Boolean> {

        @Override
        public Boolean parse(String input) {
            return Boolean.parseBoolean(input);
        }

    }
}
