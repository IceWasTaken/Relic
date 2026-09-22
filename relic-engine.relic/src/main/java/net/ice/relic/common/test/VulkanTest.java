package net.ice.relic.common.test;

import net.ice.curio.config.RendererConfig;
import net.ice.curio.config.enums.BackendType;
import net.ice.heirloom.application.Application;
import net.ice.heirloom.application.ApplicationProperties;
import net.ice.heirloom.Version;
import net.ice.relic.RelicApplication;
import net.ice.relic.common.test.scene.VulkanScene;
import net.ice.relic.core.scene.light.Light;

public class VulkanTest extends RelicApplication {

    private Light dirLight;
    private float angleInc;
    private float lightAngle = 270;

    protected VulkanTest(String[] arguments) {
        RendererConfig.setBackendType(BackendType.VULKAN);

        super(new ApplicationProperties(
                "Relic Application Test",
                new Version(0, 0, 1),
                new Version(0, 6, 1),
                arguments
        ));
    }

    static void main(String[] args) {
        new VulkanTest(args).run();
    }

    @Override
    protected void init(RelicApplication application) {
        VulkanScene testingScene = new VulkanScene("Testing", this);
        this.dirLight = testingScene.getLights().getFirst();
        application.loadScene(testingScene);
    }

    @Override
    protected void update(RelicApplication application) {

    }

    @Override
    protected void render(RelicApplication application) {

    }

    @Override
    protected void cleanup(RelicApplication application) {

    }
}
