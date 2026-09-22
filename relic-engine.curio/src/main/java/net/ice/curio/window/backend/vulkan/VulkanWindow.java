package net.ice.curio.window.backend.vulkan;

import net.ice.curio.Curio;
import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.curio.library.glfw.events.WindowHintEvent;
import net.ice.curio.window.Window;
import net.ice.heirloom.event.EventListener;
import net.ice.heirloom.event.EventManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;

public class VulkanWindow extends GLFWWindow {

    public VulkanWindow(Curio curio) {
	    super(curio);

	}

    public static PointerBuffer getExtensions() {
        PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        if (glfwExtensions == null) {
            throw new RuntimeException("Failed to find the GLFW platform surface extensions");
        }
        return glfwExtensions;
    }

    public void init() {
    }

    public void createSurface(VkInstance instance, LongBuffer buffer) {
        glfwCreateWindowSurface(instance, windowHandle, null, buffer);
    }

    protected void setupInitHints() {

    }

    protected void setupWindowHints() {
        windowHint(GLFWWindowHint.CLIENT_API, GLFWWindowHintValues.NO_API);

    }


//    @Override
//    public void resize(int width, int height) {
//        if(!initialized) {
//            throw new IllegalStateException("Window has not been initialized yet.");
//        }
//
//        this.width = width;
//        this.height = height;
//    }

    @Override
    public void update(float deltaTime) {

    }
}
