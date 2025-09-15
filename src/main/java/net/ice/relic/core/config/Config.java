package net.ice.relic.core.config;

import net.ice.relic.core.config.configs.PlayerConfig;
import net.ice.relic.core.config.configs.RendererConfig;
import net.ice.relic.core.config.configs.WindowConfig;

public class Config {

    private final WindowConfig windowConfig;
    private final RendererConfig rendererConfig;
    private final PlayerConfig playerConfig;

    public Config() {
        this.windowConfig = new WindowConfig().loadConfig();
        this.rendererConfig = new RendererConfig().loadConfig();
        this.playerConfig = new PlayerConfig().loadConfig();
    }

    public WindowConfig getWindowConfig() {
        return windowConfig;
    }

    public RendererConfig getRendererConfig() {
        return rendererConfig;
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }
}
