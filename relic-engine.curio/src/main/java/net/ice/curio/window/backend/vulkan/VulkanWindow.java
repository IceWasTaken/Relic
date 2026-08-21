package net.ice.curio.window.backend.vulkan;

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

public class VulkanWindow extends Window {

    public VulkanWindow() {

    }

    public static PointerBuffer getExtensions() {
        PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        if (glfwExtensions == null) {
            throw new RuntimeException("Failed to find the GLFW platform surface extensions");
        }
        return glfwExtensions;
    }

    @Override
    public void init() {
        window.init();
    }

    public void createSurface(VkInstance instance, LongBuffer buffer) {
        window.createWindowSurface(instance, buffer);
    }


    @EventListener
    public static void setupWindowHints(WindowHintEvent event) {
        event.windowHint(GLFWWindowHint.CLIENT_API, GLFWWindowHintValues.NO_API);
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

    static {
        EventManager.addListener(VulkanWindow.class);
    }
}
