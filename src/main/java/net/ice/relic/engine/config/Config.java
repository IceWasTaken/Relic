package net.ice.relic.engine.config;

import net.ice.relic.engine.config.configs.RendererConfig;
import net.ice.relic.engine.config.configs.WindowConfig;

public class Config {

    private final WindowConfig windowConfig;
    private final RendererConfig rendererConfig;

    public Config() {
        this.windowConfig = new WindowConfig().loadConfig();
        this.rendererConfig = new RendererConfig().loadConfig();
    }

    public WindowConfig getWindowConfig() {
        return windowConfig;
    }

    public RendererConfig getRendererConfig() {
        return rendererConfig;
    }
}
