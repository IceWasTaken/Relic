package net.ice.curio.graphics.memory;

import net.ice.heirloom.sort.Sortable;
import org.joml.*;

import java.lang.reflect.RecordComponent;

public record StructEntry(String name, Type type, boolean isPadding) implements Sortable {

	public static StructEntry fromRecordComponent(RecordComponent recordComponent) {
		return new StructEntry(recordComponent.getName(), Type.fromRecordComponent(recordComponent), false);
	}

	@Override
	public String toString() {
		return name + " " + type.toString() + " " + isPadding;
	}

	@Override
	public int sortValue() {
		return type.size;
	}

	public enum Type {

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

		MAT2(4 * VEC2.size, 16),
		MAT3(4 * VEC3.size, 16),
		MAT4(4 * VEC4.size, 16),

		PAD(0, 0);

		private final int size;
		private final int alignment;

		Type(int size, int alignment) {
			this.size = size;
			this.alignment = alignment;
		}

		public int getSize() {
			return size;
		}

		public int getAlignment() {
			return alignment;
		}

		//not sure if it's possible to return the unsigned versions
		//since this is really just for types and not the underlying data, it would probably just make sense to make classes that represent unsigned ints and longs
		public static Type fromRecordComponent(RecordComponent component) {
			return switch(component.getType()) {
				case Class<?> cls when cls == boolean.class -> BOOL;
				case Class<?> cls when cls == int.class -> INT32;
				case Class<?> cls when cls == long.class -> INT64;
				case Class<?> cls when cls == float.class -> FLOAT32;
				case Class<?> cls when cls == double.class -> DOUBLE64;

				case Class<?> cls when cls == Vector2f.class -> VEC2;
				case Class<?> cls when cls == Vector3f.class -> VEC3;
				case Class<?> cls when cls == Vector4f.class -> VEC4;

				case Class<?> cls when cls == Matrix2f.class -> MAT2;
				case Class<?> cls when cls == Matrix3f.class -> MAT3;
				case Class<?> cls when cls == Matrix4f.class -> MAT4;
				default -> throw new IllegalStateException("Unexpected value: " + component.getType());
			};
		}
	}
}
