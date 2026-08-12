package net.ice.relic.core.ecs.component;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Component {
    private static final AtomicInteger idSequence = new AtomicInteger(0);

    private boolean active;

    public abstract void update();

    public Component() {
    }

    public static <T extends Component> int getType(Class<T> clazz) {
        return IDS.get(clazz);
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

    private static final ClassValue<Integer> IDS = new ClassValue<>() {
        @Override
        protected Integer computeValue(Class<?> type) {
            return idSequence.getAndIncrement();
        }
    };
}
