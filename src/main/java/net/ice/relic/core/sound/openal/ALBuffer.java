package net.ice.relic.core.sound.openal;

import net.ice.relic.core.interfaces.Cleanable;

import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class ALBuffer implements Cleanable {

    private final int bufferHandle;

    public ALBuffer() {
        bufferHandle = alGenBuffers();
    }

    public void bufferData(int format, ShortBuffer data, int sampleRate) {
        alBufferData(bufferHandle, format, data, sampleRate);
    }

    @Override
    public void cleanup() {
        alDeleteBuffers(bufferHandle);
    }
}
