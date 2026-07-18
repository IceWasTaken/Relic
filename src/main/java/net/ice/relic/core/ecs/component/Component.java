package net.ice.relic.core.ecs.component;

public abstract class Component {

    private boolean active;

    public Component() {
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }



}
