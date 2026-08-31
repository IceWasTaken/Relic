package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.library.vulkan.utils.DebugUtils;
import net.ice.curio.window.backend.vulkan.VulkanWindow;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class Instance implements Lifecycle {

	private final VkInstance vkInstance;

    private DebugUtils debugUtils;

    public Instance(GraphicsContext graphicsContext) {
        Logger.info("VulkanInstance: Creating instance.");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo applicationInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8(graphicsContext.getCurio().getApplicationProperties().applicationName()))
                    .applicationVersion(1)
                    .pEngineName(stack.UTF8("Relic"))
                    .engineVersion(1)
                    .apiVersion(VK_API_VERSION_1_3);

            ValidationLayers validationLayers = new ValidationLayers(stack);

            PointerBuffer requiredLayers = validationLayers.getRequiredLayers(stack);
            PointerBuffer glfwExtensions = VulkanWindow.getExtensions();

            List<String> additionalExtensions = new ArrayList<>();

            if(validationLayers.canValidate()) {
                debugUtils = new DebugUtils();
                additionalExtensions.add(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
            }

            PointerBuffer requiredExtensions = getRequiredExtensions(stack, glfwExtensions, additionalExtensions);

            VkInstanceCreateInfo instanceCreateInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(validationLayers.canValidate() ? debugUtils.getExtensionAddress() : 0)
                    .pApplicationInfo(applicationInfo)
                    .ppEnabledLayerNames(requiredLayers)
                    .ppEnabledExtensionNames(requiredExtensions);

            PointerBuffer instance = stack.mallocPointer(1);
	        checkVulkan(vkCreateInstance(instanceCreateInfo, null, instance), "[VulkanInstance]: Error creating instance");
            vkInstance = new VkInstance(instance.get(0), instanceCreateInfo);

            if(validationLayers.canValidate()) {
                debugUtils.init(this);
            }
        }
    }

    private PointerBuffer getRequiredExtensions(MemoryStack stack, PointerBuffer GLFWExtensions, List<String> additionalExtensions) {
        PointerBuffer requiredExtensions = stack.mallocPointer(GLFWExtensions.remaining() + additionalExtensions.size());

        requiredExtensions.put(GLFWExtensions);
        additionalExtensions.forEach(s -> requiredExtensions.put(stack.UTF8(s)));

        return requiredExtensions.flip();
    }


    @Override
    public void cleanup() {
        Logger.info("VulkanInstance: Destroying Instance.");
        debugUtils.cleanup(this);

        vkDestroyInstance(vkInstance, null);
    }

    VkInstance getVkInstance() {
        return vkInstance;
    }

    public int createDebugUtilsMessenger(VkDebugUtilsMessengerCreateInfoEXT createInfo, LongBuffer longBuffer) {
        return vkCreateDebugUtilsMessengerEXT(vkInstance, createInfo, null, longBuffer);
    }
    public void destroyDebugUtilsMessenger(long handle) {
        vkDestroyDebugUtilsMessengerEXT(vkInstance, handle, null);
    }

}
