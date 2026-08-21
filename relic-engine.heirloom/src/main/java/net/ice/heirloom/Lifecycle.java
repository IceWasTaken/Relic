package net.ice.heirloom;

public interface Lifecycle {

    default void init() {}

    default void update() {}
    default void update(float deltaTime) {}

    default void input() {}
    default void input(float deltaTime) {}

    default void render() {}

    default void cleanup() {}
}
