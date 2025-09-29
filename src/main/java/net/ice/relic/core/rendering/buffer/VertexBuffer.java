package net.ice.relic.core.rendering.buffer;

public interface VertexBuffer {

    void bind(int type);
    void unbind();
    void bufferData();
    void delete();
}
