package net.ice.relic.common.asset;

import net.ice.relic.core.interfaces.Cleanable;

public interface Asset extends Cleanable {

    String getPath();

    @Override
    void cleanup();
}
