package net.ice.heirloom;

public record ApplicationProperties(
        String applicationName,
        Version applicationVersion,
        boolean debugMode
) {


}
