package net.ice.curio.library.opengl.object.buffer;

import net.ice.curio.library.opengl.wrapper.enums.BufferTarget;

public class IndexBufferObject extends GLBuffer {

    public IndexBufferObject() {
        super(BufferTarget.ELEMENT_ARRAY);
    }
}
