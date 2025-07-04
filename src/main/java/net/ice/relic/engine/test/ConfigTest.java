package net.ice.relic.engine.test;

import net.ice.relic.engine.config.Config;
import net.ice.relic.engine.config.configs.WindowConfig;

public class ConfigTest {

    private final WindowConfig windowConfig;

    public ConfigTest() {
        Config config = new Config();
        this.windowConfig = config.getWindowConfig();
    }

    public WindowConfig getWindowConfig() {
        return windowConfig;
    }

    public static void main(String[] args) {
        ConfigTest test = new ConfigTest();

        System.out.println(test.getWindowConfig().getHeight());

        test.getWindowConfig().setHeight(7564);
//        test.getWindowConfig().writeConfigToFileAndSave();
    }
}
