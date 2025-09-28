package net.ice.relic.core.rendering.backend;

public abstract class Renderer {

    public abstract void init();
    public abstract void render();
    public abstract void resize();

    @Deprecated
    public abstract void setupData();

    protected Renderer() {

    }


}
