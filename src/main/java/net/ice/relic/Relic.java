package net.ice.relic;

import net.ice.heirloom.Version;
import net.ice.relic.application.RelicApplication;

public final class Relic {

	private static final Version RELIC_VERSION = new Version(0, 6, 0);

	//private final RelicApplication relicApplication;

	public Relic() {

	}

	public static Version getVersion() {
		return RELIC_VERSION;
	}
}
