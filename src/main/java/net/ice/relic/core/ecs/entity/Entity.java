package net.ice.relic.core.ecs.entity;

import net.ice.relic.core.ecs.component.Component;

import java.util.*;

public class Entity {

    private static int lastID = -1;

    private final int id;
    private final String name;
    private final Optional<Entity> parent;
    private final List<Entity> children;

    private final Set<Component> components;

    public static Entity newEntity(String name) {
        return new Entity(name);
    }

    public static Entity newEntity(Entity parent) {
        return new Entity(parent);
    }

    public static Entity newEntity(String name, Entity parent) {
        return new Entity(name, parent);
    }

    protected Entity(String name, Entity parent) {
        this.id = lastID++;
        this.name = name;
        this.parent = Optional.ofNullable(parent);
        this.children = new ArrayList<>();
        this.components = new HashSet<>();
    }

    protected Entity(String name) {
        this(name, null);
    }

    protected Entity(Entity parent) {
        this(UUID.randomUUID().toString(), null);
    }

    protected Entity() {
        this(UUID.randomUUID().toString(), null);
    }

    public Entity newChild(String name) {
        Entity entity = new Entity(name, this);
        children.add(entity);
		return entity;
    }

    public int getID() {
        return id;
    }

    public Entity addComponent(Component component) {
        components.add(component);
        return this;
    }

    public Component getComponent(Class<?> type) {
        Component outComponent = null;
        for(Component component : components) {
            if(component.getClass() == type) {
                outComponent = component;
            }
        }
        return outComponent;
    }

    public List<Entity> getChildren() {
        return children;
    }

    public Set<Component> getComponents() {
        return components;
    }

    public String getName() {
        return name;
    }
}
