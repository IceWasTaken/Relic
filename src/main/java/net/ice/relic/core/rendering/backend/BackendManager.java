package net.ice.relic.core.rendering.backend;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.interfaces.Cleanable;
import net.ice.relic.core.interfaces.Initializable;
import net.ice.relic.core.interfaces.Renderable;

public abstract class BackendManager implements Initializable, Cleanable, Renderable {

    protected final RelicApplication application;

    public BackendManager(RelicApplication relicApplication) {
        this.application = relicApplication;
    }
}
