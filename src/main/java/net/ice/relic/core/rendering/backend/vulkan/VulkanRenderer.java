package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.Renderer;

public class VulkanRenderer extends Renderer implements Lifecycle {

    public VulkanRenderer(RelicApplication relicApplication) {
        super(relicApplication);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void setupData() {

    }
}
