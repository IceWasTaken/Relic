package net.ice.relic.common;
import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Input implements Lifecycle {

    private boolean inWindow;

    private static final Set<Integer> keysDown = new HashSet<>();
    private static final Set<Integer> mouseButtonsDown = new HashSet<>();

    private final Vector2f scroll = new Vector2f();
    private final Vector2f mouseDelta = new Vector2f();
    private final Vector2f mousePosition = new Vector2f();
    private final Vector2f prevMousePosition = new Vector2f();

    private final RelicApplication application;

    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback scrollCallback;
    private GLFWCursorPosCallback mousePosCallback;


    public static boolean isKeyDown(int key) {
        return keysDown.contains(key);
    }

    public Input(RelicApplication application) {
        this.application = application;
        this.inWindow = false;
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
                mousePosition.x = (float) xpos;
                mousePosition.y = (float) ypos;
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
        glfwSetCursorEnterCallback(application.getWindow().getWindowHandle(), (window, entered) -> inWindow = entered);
    }

    @Override
    public void update() {
        mouseDelta.x = 0;
        mouseDelta.y = 0;
        if(prevMousePosition.x > 0 && prevMousePosition.y > 0 && inWindow) {
            float deltaX = mousePosition.x - prevMousePosition.x;
            float deltaY = mousePosition.y - prevMousePosition.y;
            if(deltaX != 0) {
                mouseDelta.x = deltaX;
            }
            if(deltaY != 0) {
                mouseDelta.y = deltaY;
            }
        }
        prevMousePosition.x = mousePosition.x;
        prevMousePosition.y = mousePosition.y;
    }

    @Override
    public void cleanup() {
    }

    public Vector2f getMousePosition() {
        return mousePosition;
    }

    public Set<Integer> getMouseButtonsDown() {
        return mouseButtonsDown;
    }

    public Vector2f getPrevMousePosition() {
        return prevMousePosition;
    }

    public Vector2f getMouseDelta() {
        return mouseDelta;
    }

    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    public static Set<Integer> getKeysDown() {
        return keysDown;
    }
}
