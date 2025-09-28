package net.ice.relic.common.test;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Version;
import net.ice.relic.core.config.Config;

public class RelicTest extends RelicApplication {

    protected RelicTest(Config config, Version applicationVersion) {
        super(config, applicationVersion);
    }

    @Override
    protected void init(RelicApplication application) {
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

    public static void main(String[] args) {
        RelicTest relicTest = new RelicTest(new Config(), new Version(0,0,1));
        relicTest.run();
    }
}
