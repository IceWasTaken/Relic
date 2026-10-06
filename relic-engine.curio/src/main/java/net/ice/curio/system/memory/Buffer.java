package net.ice.curio.system.memory;

import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Buffer<T> implements AutoCloseable {

	protected final ByteBuffer buffer;

	protected abstract T readNew();
	protected abstract T readInto(T existing);

	public Buffer(Size bufferSize) {
		this.buffer = MemoryUtil.memAlloc(bufferSize.getSize()).order(ByteOrder.nativeOrder());
	}

	public T get() {
		buffer.position(0);
		return readNew();
	}

	public T get(T existing) {
		buffer.position(0);
		return readInto(existing);
	}

	public void putInt(int pos, int val) {
		buffer.putInt(pos, val);
	}

	public void putFloat(int pos, float val) {
		buffer.putFloat(pos, val);
	}

	public void putLong(int pos, long val) {
		buffer.putLong(pos, val);
	}

	public void putShort(int pos, short val) {
		buffer.putShort(pos, val);
	}

	public void putDouble(int pos, double val) {
		buffer.putDouble(pos, val);
	}

	public void putBoolean(int pos, boolean val) {
		buffer.putInt(pos, val ? 1 : 0);
	}

	public void checkSafe(int size) {
		Checks.checkSafe(buffer, size);
	}

	public long memAddress(int offset) {
		return buffer == null ? 0L : MemoryUtil.memAddress(buffer, offset);
	}

	public long memAddress() {
		return MemoryUtil.memAddressSafe(buffer);
	}

	//etc

	@Override
	public void close() {
		MemoryUtil.memFree(buffer);
	}

	public static class Size {
		private int size;

		public Size() {}

		public Size addFloat(int count) {
			size += Float.BYTES * count;
			return this;
		}

		public Size addInt(int count) {
			size += Integer.BYTES * count;
			return this;
		}

		public Size addLong(int count) {
			size += Long.BYTES * count;
			return this;
		}

		public Size addShort(int count) {
			size += Short.BYTES * count;
			return this;
		}

		public Size addDouble(int count) {
			size += Double.BYTES * count;
			return this;
		}

		public Size addBoolean(int count) {
			size += Integer.BYTES * count;
			return this;
		}

		public Size addByte(int count) {
			size += Byte.BYTES * count;
			return this;
		}

		public Size arrayOf(int arrSize) {
			size *= arrSize;
			return this;
		}

		public int getSize() {
			return size;
		}
	}
}
