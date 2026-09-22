package net.ice.relic.common.console;

import net.ice.relic.RelicApplication;
import net.ice.relic.core.scene.Scene;

public class CommandContext {

    private final RelicApplication relicApplication;

    public CommandContext(RelicApplication relicApplication) {
        this.relicApplication = relicApplication;
    }

    public Scene getCurrentScene() {
        return relicApplication.getCurrentScene();
    }
}
