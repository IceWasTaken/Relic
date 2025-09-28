package net.ice.relic.core.modding;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Input;
import net.ice.relic.core.rendering.backend.opengl.rendering.GLRenderer;

public interface IMod {

    default void init(RelicApplication application) {}

    default void preInput(Input input, float deltaTime) {}

    default void postInput(Input input, float deltaTime) {}

    default void preUpdate(float deltaTime) {}

    default void postUpdate(float deltaTime) {}

    default void preRender(GLRenderer renderer) {}

    default void postRender(GLRenderer renderer) {}

    default void cleanup() {}

    ModData getModData();
}
