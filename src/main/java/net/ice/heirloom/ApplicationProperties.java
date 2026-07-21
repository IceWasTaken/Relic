package net.ice.heirloom;

public record ApplicationProperties(
        String applicationName,
        Version applicationVersion,
		Version targetRelicVersion,
        boolean debugMode
) {


}
