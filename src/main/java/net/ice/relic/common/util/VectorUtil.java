package net.ice.relic.common.util;

import imgui.ImVec2;
import org.joml.Vector2f;

public class VectorUtil {

    public static Vector2f imVecToVector2f(ImVec2 vec2) {
        return new Vector2f(vec2.x, vec2.y);
    }

    public static ImVec2 vector2fToImVec2(Vector2f vec2f) {
        return new ImVec2(vec2f.x, vec2f.y);
    }
}
