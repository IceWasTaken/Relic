package net.ice.relic.application;

import net.ice.heirloom.Version;

import java.util.Arrays;

public record ApplicationProperties(
        String applicationName,
        Version applicationVersion,
		Version targetRelicVersion,
		boolean debug
) {

	public ApplicationProperties(
			String applicationName,
			Version applicationVersion,
			Version targetRelicVersion,
			String[] arguments
	) {
		this(
				applicationName,
				applicationVersion,
				targetRelicVersion,
				Arrays.asList(arguments).contains("debug") || Arrays.asList(arguments).contains("-debug") || Arrays.asList(arguments).contains("--debug")
		);
	}

}
