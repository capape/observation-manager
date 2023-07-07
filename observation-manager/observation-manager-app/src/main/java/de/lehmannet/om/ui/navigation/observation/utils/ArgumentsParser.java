package de.lehmannet.om.ui.navigation.observation.utils;

import java.util.HashMap;
import java.util.Map;

public class ArgumentsParser {

    private final Map<String, String> arguments;

    private ArgumentsParser(String[] paramArgs) {
        this.arguments = new HashMap<>();
        this.parseArguments(paramArgs);

    }

    public String getArgumentValue(ArgumentName nameArgument) {
        return this.arguments.get(nameArgument.getValue());
    }

    public String getArgumentValue(String nameArgument) {
        return this.arguments.get(nameArgument);
    }

    private void parseArguments(String[] args) {

        if (args != null) {
            for (String arg : args) {
                if (ArgumentName.isValid(arg)) {
                    arguments.put(getArgName(arg), getArgValue(arg));
                }

            }
        }

    }

    private String getArgName(String argument) {

        return argument.substring(0, argument.indexOf("="));

    }

    private String getArgValue(String argument) {

        return argument.substring(argument.indexOf("=") + 1);

    }

    public static class Builder {
        private String[] args;

        public Builder(String[] args) {
            this.args = args == null ? null : new String[args.length];
            if (args != null) {
                System.arraycopy(args, 0, this.args, 0, args.length);
            }
        }

        public ArgumentsParser build() {
            return new ArgumentsParser(args);
        }

    }

}