package net.ice.relic.core.modding;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Input;
import net.ice.relic.core.rendering.Renderer;

public interface IMod {

    default void init(RelicApplication application) {}

    default void preInput(Input input, float deltaTime) {}

    default void postInput(Input input, float deltaTime) {}

    default void preUpdate(float deltaTime) {}

    default void postUpdate(float deltaTime) {}

    default void preRender(Renderer renderer) {}

    default void postRender(Renderer renderer) {}

    default void cleanup() {}

    ModData getModData();
}
