package net.ice.relic;

import net.ice.heirloom.Version;

public final class Relic {

	private static final Version RELIC_VERSION = new Version(0, 6, 1);

	//private final RelicApplication relicApplication;

	public Relic() {

	}

	public static Version getVersion() {
		return RELIC_VERSION;
	}
}
