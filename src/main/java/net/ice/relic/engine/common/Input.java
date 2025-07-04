package net.ice.relic.engine.common;
import net.ice.relic.engine.RelicApplication;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Input {


    private static final Set<Integer> keysDown = new HashSet<>();
    private static final Set<Integer> mouseButtonsDown = new HashSet<>();


    private final Vector2f scroll = new Vector2f();
    private final Vector2i mousePosition = new Vector2i();
    private long cursorHandle;

    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback scrollCallback;
    private GLFWCursorPosCallback mousePosCallback;

    private final RelicApplication application;

    public static boolean isKeyDown(int key) {
        return keysDown.contains(key);
    }

    public Input(RelicApplication application) {
        this.application = application;


    }

    public void init() {
        glfwSetKeyCallback(application.getWindow().getWindowHandle(), keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(action == GLFW_PRESS) {
                    keysDown.add(key);
                } else if(action == GLFW_RELEASE) {
                    keysDown.remove(key);
                }
            }
        });

        glfwSetScrollCallback(application.getWindow().getWindowHandle(), scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scroll.x = (float) xoffset;
                scroll.y = (float) yoffset;
            }
        });

        glfwSetCursorPosCallback(application.getWindow().getWindowHandle(), mousePosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mousePosition.x = (int) xpos;
                mousePosition.y = (int) ypos;
            }
        });

        glfwSetMouseButtonCallback(application.getWindow().getWindowHandle(), mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if(action == GLFW_PRESS) {
                    mouseButtonsDown.add(button);
                } else if(action == GLFW_RELEASE) {
                    mouseButtonsDown.remove(button);
                }
            }
        });


    }


}
