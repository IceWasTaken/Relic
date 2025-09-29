package net.ice.relic.common.asset;

import net.ice.relic.core.interfaces.Cleanable;
import net.ice.relic.core.resource.Resource;

public interface Asset extends Cleanable {

    Resource getResource();

    @Override
    void cleanup();
}
