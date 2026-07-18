package net.ice.curio.library.opengl.object.buffer;

import net.ice.curio.library.opengl.wrapper.enums.BufferTarget;

public class DrawIndirectBuffer extends GLBuffer {

    public DrawIndirectBuffer() {
        super(BufferTarget.DRAW_INDIRECT);
    }

}
