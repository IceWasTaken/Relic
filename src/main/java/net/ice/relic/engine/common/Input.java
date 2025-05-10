package net.ice.relic.engine.common;

import java.util.HashSet;
import java.util.Set;

public class Input {
    public static final Set<Integer> keysDown = new HashSet<>();

    public static boolean isKeyDown(int key) {
        return keysDown.contains(key);
    }
}
