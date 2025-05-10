package net.ice.relic.engine;

public interface Relic {

    void init(Window window);

    void initShaders();

    void loop();

    void close();
}
