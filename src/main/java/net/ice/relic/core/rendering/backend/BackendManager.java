package net.ice.relic.core.rendering.backend;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;

@Deprecated
public abstract class BackendManager implements Lifecycle {

    protected final RelicApplication application;


    public BackendManager(RelicApplication relicApplication) {
        this.application = relicApplication;
    }



    public RelicApplication getApplication() {
        return application;
    }
}
