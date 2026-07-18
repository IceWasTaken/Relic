package net.ice.relic.common.test;

import net.ice.heirloom.ApplicationProperties;
import net.ice.relic.application.RelicApplication;
import net.ice.heirloom.Version;

public class RelicTest extends RelicApplication {

    protected RelicTest() {
        super(new ApplicationProperties(
                "Relic Application Test",
                new Version(0, 0, 1),
                true
        ));
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
        new RelicTest().run();
    }


}
