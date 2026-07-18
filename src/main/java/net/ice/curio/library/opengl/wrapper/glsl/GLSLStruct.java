package net.ice.curio.library.opengl.wrapper.glsl;

import net.ice.curio.library.opengl.wrapper.enums.DataType;

import java.util.List;

public class GLSLStruct {

    private final List<DataType> fields;
    private final int[] offsets;

    private final int rawSize;
    private final int alignment;
    private final int stride;

    public GLSLStruct(List<DataType> fields) {
        this.fields = List.copyOf(fields);
        this.offsets = new int[fields.size()];

        int offset = 0;
        int maxAlignment = 0;

        for (int i = 0; i < fields.size(); i++) {
            DataType type = fields.get(i);

            offset = align(offset, type.getAlignment());
            offsets[i] = offset;

            offset += type.getSize();
            maxAlignment = Math.max(maxAlignment, type.getAlignment());
        }

        this.rawSize = offset;
        this.alignment = maxAlignment;
        this.stride = align(offset, maxAlignment);
    }

    private int align(int value, int alignment) {
        return (value + alignment - 1) & -alignment;
    }

    public int[] getOffsets() {
        return offsets;
    }

    public int getOffset(int index) {
        return offsets[index];
    }

    public int getSize() {
        return rawSize;
    }

    public int getStride() {
        return stride;
    }

    public int getAlignment() {
        return alignment;
    }
}
