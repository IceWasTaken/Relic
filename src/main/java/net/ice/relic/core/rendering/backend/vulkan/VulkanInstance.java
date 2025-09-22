package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.relic.core.Version;
import net.ice.relic.core.util.OSUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.KHRPortabilityEnumeration.VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK12.VK_API_VERSION_1_2;

public class VulkanInstance {

    public static final int MESSAGE_SEVERITY_BITMASK = VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
    public static final int MESSAGE_TYPE_BITMASK = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;

    private final VkInstance instance;

    private VkDebugUtilsMessengerCreateInfoEXT debugUtils;
    private long debugHandle;

    public VulkanInstance(boolean validate, Version version, String appName) {
        Logger.info("Vulkan: Initializing Vulkan instance.");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer applicationName = stack.UTF8(appName);
            VkApplicationInfo applicationInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(applicationName)
                    .applicationVersion(version.makeVulkanVersion())
                    .pEngineName(stack.UTF8("Vulkan Relic Engine"))
                    .engineVersion(VK_MAKE_VERSION(0, 3, 0))
                    .apiVersion(VK_API_VERSION_1_2);

            List<String> validationLayers = getSupportedValidationLayers();
            int layerCount = validationLayers.size();
            boolean supportsValidation = validate;
            if(validate && layerCount == 0) {
                supportsValidation = false;
                Logger.warn("Vulkan: No validation layers found. Validation will be disabled.");
            }
            Logger.info("Vulkan: Found {} validation layers.", layerCount);

            PointerBuffer requiredExtensions = null;
            if(supportsValidation) {
                requiredExtensions = stack.mallocPointer(layerCount);
                for(int i = 0; i < layerCount; i++) {
                    Logger.debug("Vulkan: Using validation layer [{}]", validationLayers.get(i));
                    requiredExtensions.put(i, stack.ASCII(validationLayers.get(i)));
                }
            }

            Set<String> extensions = getInstanceExtensions();
            PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
            if(glfwExtensions == null) {
                throw new RuntimeException("Failed to find GLFW extensions.");
            }

            PointerBuffer requiredInstanceExtensions;

            boolean usePortability = extensions.contains("VK_KHR_portability_enumeration") && OSUtil.getOSType() == OSUtil.OSType.MAC;
            if(supportsValidation) {
                ByteBuffer vkDebugUtilsExtension = stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
                int extensionCount = usePortability ? glfwExtensions.remaining() + 2 : glfwExtensions.remaining() + 1;
                requiredInstanceExtensions = stack.mallocPointer(extensionCount);
                requiredInstanceExtensions.put(glfwExtensions).put(vkDebugUtilsExtension);
                if(usePortability) {
                    requiredInstanceExtensions.put(stack.UTF8("VK_KHR_portability_enumeration"));
                }
            } else {
                int extensionCount = usePortability ? glfwExtensions.remaining() + 1 : glfwExtensions.remaining();
                requiredInstanceExtensions = stack.mallocPointer(extensionCount);
                requiredInstanceExtensions.put(glfwExtensions);
                if(usePortability) {
                    requiredInstanceExtensions.put(stack.UTF8(KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME));
                }
            }
            requiredInstanceExtensions.flip();

            long extension = MemoryUtil.NULL;
            if(supportsValidation) {
                debugUtils = createCallBack();
                extension = debugUtils.address();
            }

            VkInstanceCreateInfo instanceInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(extension)
                    .pApplicationInfo(applicationInfo)
                    .ppEnabledLayerNames(requiredExtensions)
                    .ppEnabledExtensionNames(requiredInstanceExtensions);
            if(usePortability) {
                instanceInfo.flags(VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR);
            }

            PointerBuffer pInstance = stack.mallocPointer(1);
            checkVulkan(vkCreateInstance(instanceInfo, null, pInstance), "Vulkan: Error creating instance");
            instance = new VkInstance(pInstance.get(0), instanceInfo);

            debugHandle = VK_NULL_HANDLE;
            if (supportsValidation) {
                LongBuffer longBuff = stack.mallocLong(1);
                checkVulkan(vkCreateDebugUtilsMessengerEXT(instance, debugUtils, null, longBuff), "Error creating debug utils");
                debugHandle = longBuff.get(0);
            }
        }
    }

    private static VkDebugUtilsMessengerCreateInfoEXT createCallBack() {
        return VkDebugUtilsMessengerCreateInfoEXT
                .calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
                .messageSeverity(MESSAGE_SEVERITY_BITMASK)
                .messageType(MESSAGE_TYPE_BITMASK)
                .pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                    VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                    if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
                        Logger.info("Vulkan: VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
                        Logger.warn("Vulkan: VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
                        Logger.error("Vulkan: VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    } else {
                        Logger.debug("Vulkan: VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    }
                    return VK_FALSE;
                });
    }

    private Set<String> getInstanceExtensions() {
        Set<String> instanceExtensions = new HashSet<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer numExtensionsBuf = stack.callocInt(1);
            vkEnumerateInstanceExtensionProperties((String) null, numExtensionsBuf, null);
            int numExtensions = numExtensionsBuf.get(0);
            Logger.info("Vulkan: Instance supports [{}] extensions", numExtensions);

            VkExtensionProperties.Buffer instanceExtensionsProps = VkExtensionProperties.calloc(numExtensions, stack);
            vkEnumerateInstanceExtensionProperties((String) null, numExtensionsBuf, instanceExtensionsProps);
            for (int i = 0; i < numExtensions; i++) {
                VkExtensionProperties props = instanceExtensionsProps.get(i);
                String extensionName = props.extensionNameString();
                instanceExtensions.add(extensionName);
                Logger.info("Vulkan: Supported instance extension [{}]", extensionName);
            }
        }
        return instanceExtensions;
    }

    private List<String> getSupportedValidationLayers() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer layerCountBuffer = stack.callocInt(1);
            vkEnumerateInstanceLayerProperties(layerCountBuffer, null);
            int layerCount = layerCountBuffer.get(0);
            Logger.debug("Vulkan: Found {} validation layers.", layerCount);

            VkLayerProperties.Buffer layerProperties = VkLayerProperties.calloc(layerCount, stack);
            vkEnumerateInstanceLayerProperties(layerCountBuffer, layerProperties);

            List<String> supportedValidationLayers = new ArrayList<>();

            for(int i = 0; i < layerCount; i++) {
                VkLayerProperties properties = layerProperties.get(i);
                String name  = properties.layerNameString();
                supportedValidationLayers.add(name);
                Logger.debug("Vulkan: Found validation layer: {}", name);
            }

            List<String> layersToUse = new ArrayList<>();

            if(supportedValidationLayers.contains("VK_LAYER_KHRONOS_validation")) {
                layersToUse.add("VK_LAYER_KHRONOS_validation");
                return layersToUse;
            }

            if(supportedValidationLayers.contains("VK_LAYER_LUNARG_standard_validation")) {
                layersToUse.add("VK_LAYER_LUNARG_standard_validation");
                return layersToUse;
            }

            List<String> requestedLayers = new ArrayList<>();
            requestedLayers.add("VK_LAYER_GOOGLE_threading");
            requestedLayers.add("VK_LAYER_LUNARG_parameter_validation");
            requestedLayers.add("VK_LAYER_LUNARG_object_tracker");
            requestedLayers.add("VK_LAYER_LUNARG_core_validation");
            requestedLayers.add("VK_LAYER_GOOGLE_unique_objects");

            return requestedLayers.stream().filter(supportedValidationLayers::contains).toList();
        }
    }

    public VkInstance getInstance() {
        return instance;
    }
}
