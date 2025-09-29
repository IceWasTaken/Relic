package net.ice.relic.common.util;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;

public class ColorUtil {

    public static void glSetColor4f(Color color, float alpha) {
        glColor4f(color.red, color.green, color.blue, alpha);
    }

    public static void glSetColor3f(Color color) {
        glColor4f(color.red, color.green, color.blue, color.alpha);
    }

    public static Color vectorToColor(Vector3f vector) {
        return new Color(vector.x, vector.y, vector.z);
    }

    public static Color glVectorToColor(Vector3f vector) {
        return new Color(vector.x, vector.y, vector.z).convertFromOpenGLColor();
    }

    public static void glSetClearColor(Color color) {
        glClearColor(color.convertToOpenGLColor().red, color.convertToOpenGLColor().green, color.convertToOpenGLColor().blue, 1f);
    }

    public static Color interpolate(Color a, Color b, float t) {
        int r = (int) (a.red   + (b.red   - a.red)   * t);
        int g = (int) (a.green + (b.green - a.green) * t);
        int bVal = (int) (a.blue  + (b.blue  - a.blue)  * t);
        return new Color(r, g, bVal);
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

        public Vector3f convertToVector3f() {
            return new Vector3f(red, green, blue);
        }

        public Vector3f convertToGLVector3f() {
            return new Vector3f(red / 255f, green / 255f, blue / 255f);
        }

        @Override
        public String toString() {
            return String.format("color(%s, %s, %s, %s)", red, green, blue, alpha);
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
        WHITE(255, 255, 255),

        SUN_NOON(255, 242, 217),
        SUN_AFTERNOON(255, 221, 180),
        SUN_SET_RISE(255, 165, 102),
        SUN_OVERCAST(217, 217, 230),

        MOON_NIGHT(40, 60, 100);

        private final Color color;

        ColorDefaults(float red, float green, float blue) {
            this.color = new Color(red, green, blue);
        }

        public Color getColor() {
            return color;
        }
    }
}
