package net.ice.relic.common.test;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.test.scene.TestingScene;
import net.ice.relic.core.Version;
import net.ice.relic.core.config.Config;

public class VulkanTest extends RelicApplication {

    protected VulkanTest(Config config, Version applicationVersion) {
        super(config, applicationVersion);
    }

    public static void main(String[] args) {
        VulkanTest relicTest = new VulkanTest(new Config(), new Version(0,0,1));
        relicTest.run();
    }

    @Override
    protected void init(RelicApplication application) {
        application.loadScene(new TestingScene("Testing", this));

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
