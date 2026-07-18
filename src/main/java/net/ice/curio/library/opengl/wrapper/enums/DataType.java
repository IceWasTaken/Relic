package net.ice.curio.library.opengl.wrapper.enums;

public enum DataType {

    BOOL(4, 4),

    INT32(4, 4),
    UINT32(4, 4),

    INT64(8, 8),
    UINT64(8, 8),

    FLOAT32(4, 4),
    DOUBLE64(8, 8),

    VEC2(8, 8),
    VEC3(12, 16),
    VEC4(16, 16),

    MAT2(4 * (2 * 2), 16),
    MAT3(4 * (3 * 3), 16),
    MAT4(4 * (4 * 4), 16);

    private final int size;
    private final int alignment;

    DataType(int size, int alignment) {
        this.size = size;
        this.alignment = alignment;
    }

    public int getSize() {
        return size;
    }

    public int getAlignment() {
        return alignment;
    }
}
