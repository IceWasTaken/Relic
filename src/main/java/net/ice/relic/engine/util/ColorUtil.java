package net.ice.relic.engine.util;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class ColorUtil {

    public static void glSetColor4f(Color color, float alpha) {
        glColor4f(color.red, color.green, color.blue, alpha);
    }

    public static void glSetColor3f(Color color) {
        glColor4f(color.red, color.green, color.blue, color.alpha);
    }

    public static void glSetClearColor(Color color) {
        glClearColor(color.convertToOpenGLColor().red, color.convertToOpenGLColor().green, color.convertToOpenGLColor().blue, 1f);
    }

    public static class Color {

        public final float red;
        public final float green;
        public final float blue;
        public final float alpha;

        public Color(float red, float green, float blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = 1f;
        }

        public Color(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public Color convertToOpenGLColor() {
            return new Color(red / 255f, green / 255f, blue / 255f);
        }

        public Color convertFromOpenGLColor() {
            return new Color(red * 255f, green * 255f, blue * 255f);
        }

        public Vector4f convertToVector4f() {
            return new Vector4f(red, green, blue, alpha);
        }

        public Vector4f convertToGLVector4f() {
            return new Vector4f(red / 255f, green / 255f, blue / 255f, alpha);
        }
    }

    public enum ColorDefaults {
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

        private final Color color;

        ColorDefaults(float red, float green, float blue) {
            this.color = new Color(red, green, blue);
        }

        public Color getColor() {
            return color;
        }
    }
}
