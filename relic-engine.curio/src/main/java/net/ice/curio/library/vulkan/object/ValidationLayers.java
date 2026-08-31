package net.ice.curio.library.vulkan.object;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkLayerProperties;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;

public class ValidationLayers {

	private static final List<String> REQUESTED_LAYERS = List.of(
			"VK_LAYER_KHRONOS_validation"
	);

	private final Set<String> supportedLayers = new HashSet<>();

	public ValidationLayers(MemoryStack stack) {
		IntBuffer layerCountBuff = stack.callocInt(1);
		vkEnumerateInstanceLayerProperties(layerCountBuff, null);

		int layerCount = layerCountBuff.get(0);

		Logger.debug("[ValidationLayers]: Found {} supported layers", layerCount);

		VkLayerProperties.Buffer propertiesBuff = VkLayerProperties.calloc(layerCount, stack);
		vkEnumerateInstanceLayerProperties(layerCountBuff, propertiesBuff);

		for(VkLayerProperties properties : propertiesBuff) {
			String layerName = properties.layerNameString();
			Logger.debug("[ValidationLayers]: Found supported layer: {}", layerName);
			supportedLayers.add(layerName);
		}
	}

	public PointerBuffer getRequiredLayers(MemoryStack stack) {
		PointerBuffer requiredLayers;
		if(!canValidate()) {
			Logger.warn("[ValidationLayers]: Validation not supported.");
			requiredLayers = null;
		} else {
			requiredLayers = stack.mallocPointer(supportedLayers.size());
			supportedLayers.forEach((layer) -> {
				if(REQUESTED_LAYERS.contains(layer)) {
					Logger.debug("[ValidationLayers]: Using validation layer: {}", layer);
					requiredLayers.put(stack.ASCII(layer));
				}
			});
			requiredLayers.flip();
		}
		return requiredLayers;
	}

	public boolean canValidate() {
		return !supportedLayers.isEmpty();
	}
}
