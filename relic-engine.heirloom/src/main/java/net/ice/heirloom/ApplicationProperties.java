package net.ice.heirloom;

import java.util.Arrays;

public record ApplicationProperties(
        String applicationName,
        Version applicationVersion,
		Version targetVersion,
		boolean debug,

        String[] arguments
) {

	public ApplicationProperties(
			String applicationName,
			Version applicationVersion,
			Version targetVersion,
			String[] arguments
	) {
		this(
				applicationName,
				applicationVersion,
				targetVersion,
				Arrays.asList(arguments).contains("debug") || Arrays.asList(arguments).contains("-debug") || Arrays.asList(arguments).contains("--debug"),
				arguments
		);
	}

	public boolean hasArgument(String argument) {
		return Arrays.asList(arguments).contains(argument);
	}

}
