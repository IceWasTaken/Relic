package net.ice.heirloom.register.autoregister;

import net.ice.heirloom.register.Registry;

@FunctionalInterface
public interface Registerable<T extends Registerable<T, R>, R extends Registry<T>> {

    void register(R registry);

}
