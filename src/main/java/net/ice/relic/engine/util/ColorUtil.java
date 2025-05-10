package net.ice.relic.engine.util;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;

public class ColorUtil {

    public static void glSetColor4f(Color color, float alpha) {
        glColor4f(color.red, color.green, color.blue, alpha);
    }

    public static void glSetColor3f(Color color) {
        glColor4f(color.red, color.green, color.blue, 0f);
    }

    public enum Color {
        RED(232, 13, 13),
        ORANGE(255, 119, 0),
        YELLOW(255, 218, 10),
        GREEN(16, 173, 55),
        LIME(0, 255, 64),
        BLUE(10, 27, 255),
        LIGHT_BLUE(10, 206, 255),
        PURPLE(147, 13, 224),
        BLACK(0, 0, 0),
        LIGHT_GRAY(196, 196, 196),
        GRAY(64, 64, 64),
        DARK_GRAY(36, 36, 36),
        WHITE(255, 255, 255);

        public final float red;
        public final float blue;
        public final float green;

        Color(float red, float green, float blue) {
            this.red = red / 255.0f;
            this.green = green /255.0f;
            this.blue = blue / 255.0f;
        }
    }
}
