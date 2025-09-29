package net.ice.relic.core.window.backend;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.window.Window;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLWindow extends Window {

    public GLWindow(RelicApplication application) {
        super(application);
    }

    @Override
    protected void backendInit() {
        GLFWVidMode vidMode = glfwGetVideoMode(monitor);

        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        //glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        this.windowHandle = glfwCreateWindow(width, height, title, config.isFullscreen() ? monitor : NULL, NULL);

        if(this.windowHandle == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window.");
        }
        glfwSetWindowPos(windowHandle, vidMode.width() / 3, vidMode.height() / 5);
        glfwMakeContextCurrent(windowHandle);

        if(config.isVsync()) {
            glfwSwapInterval(1);
        }


        glfwSetFramebufferSizeCallback(windowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resize(width, height);
            }
        });

        glfwSetErrorCallback((int errorCode, long msgPtr) ->
                Logger.error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr))
        );



        this.initialized = true;
    }

    @Override
    public void resize(int width, int height) {
        if(!initialized) {
            throw new IllegalStateException("Window has not been initialized yet.");
        }
        this.width = width;
        this.height = height;

        application.getCurrentScene().getMatrix().updateProjMatrix(width, height);
        application.getRenderer().resize();

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }


    @Override
    public void update(float deltaTime) {
        if(!initialized) {
            throw new IllegalStateException("Window has not been initialized yet.");
        }

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }
}
