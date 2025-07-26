package net.ice.relic.engine.opengl;

import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class ShaderStorageBufferObject {

    public ShaderStorageBufferObject() {
        glCreateBuffers();
    }
}
