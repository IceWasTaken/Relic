package net.ice.relic.core.ecs.entity;

import net.ice.relic.core.ecs.component.Component;

import java.util.*;

public class Entity {

    private static int lastID = -1;

    private final int id;
    private final String name;
    private final Optional<Entity> parent;
    private final List<Entity> children;

    private Component[] components = new Component[32];

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

    public <T extends Component> Entity addComponent(T component) {
        int id = Component.getType(component.getClass());
        if(id >= components.length) {
            resizeArray(id);
        }
        components[id] = component;
        return this;
    }

    public <T extends Component> Entity removeComponent(Class<T> type) {
        int id = Component.getType(type);
        if(components[id] != null) {
            components[id] = null;
        }
        return this;
    }

    public <T extends Component> T getComponent(Class<T> type) {
        int id = Component.getType(type);
        if (id >= components.length) {
            return null;
        }
        return (T) components[id];
    }

    private void resizeArray(int targetID) {
        Component[] newArray = new Component[Math.max(components.length * 2, targetID + 1)];
        System.arraycopy(components, 0, newArray, 0, components.length);
        components = newArray;
    }

    public List<Entity> getChildren() {
        return children;
    }

    public Component[] getComponents() {
        return components;
    }

    public String getName() {
        return name;
    }
}

