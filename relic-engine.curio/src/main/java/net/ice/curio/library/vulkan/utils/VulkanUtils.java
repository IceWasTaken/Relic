package net.ice.curio.library.vulkan.utils;

import net.ice.curio.config.RendererConfig;
import net.ice.curio.config.enums.BackendType;
import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageType;
import net.ice.curio.graphics.exception.GraphicsCallResultException;
import net.ice.curio.graphics.exception.OutOfGraphicsMemoryException;

import static net.ice.curio.graphics.enums.image.ImageFormat.RGBA8;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanUtils {

    public static int getFormat(ImageFormat format) {
        return switch(format) {
            case RGBA8 -> VK_FORMAT_R8G8B8A8_SRGB;
			case BGRA8 -> VK_FORMAT_B8G8R8A8_SRGB;
        };
    }

    public static int getImageType(ImageType imageType) {
        return switch(imageType) {
            case IMAGE_1D -> VK_IMAGE_TYPE_1D;
            case IMAGE_2D -> VK_IMAGE_TYPE_2D;
            case IMAGE_3D -> VK_IMAGE_TYPE_3D;
        };
    }

    public static int getImageViewType(ImageType imageType) {
        return switch(imageType) {
            case IMAGE_1D -> VK_IMAGE_VIEW_TYPE_1D;
            case IMAGE_2D -> VK_IMAGE_VIEW_TYPE_2D;
            case IMAGE_3D -> VK_IMAGE_VIEW_TYPE_3D;
        };
    }

    public static void assertVulkan() {
        if(RendererConfig.getBackendType() != BackendType.VULKAN) {
            throw new RuntimeException("Rendering type not Vulkan");
        }
    }

    public static void checkVulkan(int vkResult, String message) {
        if(vkResult != 0) {
            throw switch(vkResult) {
                case VK_NOT_READY -> new GraphicsCallResultException(format(message, "Not ready"));
                case VK_TIMEOUT -> new GraphicsCallResultException(format(message, "Timeout"));
                case VK_EVENT_SET -> new GraphicsCallResultException(format(message, "Event set"));
                case VK_EVENT_RESET -> new GraphicsCallResultException(format(message, "Event reset"));
                case VK_INCOMPLETE -> new GraphicsCallResultException(format(message, "Incomplete"));
                case VK_ERROR_OUT_OF_HOST_MEMORY -> new GraphicsCallResultException(format(message, "Ran out of memory"));
                case VK_ERROR_OUT_OF_DEVICE_MEMORY -> new OutOfGraphicsMemoryException(format(message, "Ran out of graphics memory"));
                case VK_ERROR_INITIALIZATION_FAILED -> new GraphicsCallResultException(format(message, "Initialization failed"));
                case VK_ERROR_DEVICE_LOST -> new GraphicsCallResultException(format(message, "Device lost"));
                case VK_ERROR_MEMORY_MAP_FAILED -> new GraphicsCallResultException(format(message, "Memory map failed"));
                case VK_ERROR_LAYER_NOT_PRESENT -> new GraphicsCallResultException(format(message, "Layer not present"));
                case VK_ERROR_EXTENSION_NOT_PRESENT -> new GraphicsCallResultException(format(message, "Extension not present"));
                case VK_ERROR_FEATURE_NOT_PRESENT -> new GraphicsCallResultException(format(message, "Feature not present"));
                case VK_ERROR_INCOMPATIBLE_DRIVER -> new GraphicsCallResultException(format(message, "Incompatible driver"));
                case VK_ERROR_TOO_MANY_OBJECTS -> new GraphicsCallResultException(format(message, "Too many objects"));
                case VK_ERROR_FORMAT_NOT_SUPPORTED -> new GraphicsCallResultException(format(message, "Format not supported"));
                case VK_ERROR_FRAGMENTED_POOL -> new GraphicsCallResultException(format(message, "Fragmented pool"));
                case VK_ERROR_UNKNOWN -> new GraphicsCallResultException(format(message, "Unknown error"));
	            default -> throw new IllegalStateException("Unexpected value: " + vkResult);
            };
        }
    }

    private static String format(String message, String message2) {
        return message + " - " + message2;
    }
}
