package net.ice.relic.core.ecs.component;

public abstract class Component {

    private boolean active;

    public abstract void update();

    public Component() {
    }

    public boolean isActive() {
        return active;
    }

    public void disable() {
        this.active = false;
    }
    public void enable() {
        this.active = true;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
