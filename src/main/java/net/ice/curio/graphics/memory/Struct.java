package net.ice.curio.graphics.memory;

import net.ice.heirloom.sort.SortingUtil;
import org.joml.Matrix4f;
import org.tinylog.Logger;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

public abstract class Struct {

	private final StructType structType;

	private final List<StructEntry> fields;

	private final int[] offsets;

	private final int size;
	private final int stride;

	public abstract Class<?> getRecord();

	public Struct(StructType structType) {
		this.structType = structType;

		RecordComponent[] components = getRecord().getRecordComponents();
		List<StructEntry> entries = new ArrayList<>();

		for(RecordComponent component : components) {
			entries.add(StructEntry.fromRecordComponent(component));

		}

		if(structType == StructType.RAW) {
			this.fields = entries;
			this.size = calculateSize();
			this.stride = calculateStride();
			this.offsets = generateOffsets();
			Logger.debug("[Struct]: Created new struct: {}", toString());
			return;
		}

		//sort by largest to smallest alignment size
		SortingUtil.reverseInsertionSort(entries);

		this.fields = entries;

		int pos = 0;

		for(int i = 0; i < fields.size() - 1; i++) {
			StructEntry entry = fields.get(i);
			pos += entry.type().getSize();

			StructEntry nextEntry = fields.get(i + 1);

			//if next value would not be aligned
			if(!isMultiple(pos, nextEntry.type().getAlignment())) {
				int remainder = pos % nextEntry.type().getAlignment();
				int padding = remainder == 0 ? 0 : nextEntry.type().getAlignment() - remainder;

				StructEntry padEntry = findOrPad(padding, i);
				if(padEntry.isPadding()) {
					fields.add(i + 1, padEntry);
				} else {
					fields.remove(padEntry);
					fields.add(i + 1, padEntry);
				}
			}
		}

		this.size = calculateSize();
		int stride = calculateStride();

		if(structType == StructType.STD140) {
			//bitwise, checks if lowest 4 bits are correct
			int off = (stride & 15);
			if(off != 0) {
				int count = off / 4;
				fields.add(new StructEntry("std140Padding", getTypeFromSize(count), true));
			}
		} else {
			//must be padded to size of largest alignment
			int largest = 0;
			for (StructEntry field : fields) {
				largest = Math.max(largest, field.type().getAlignment());
			}

			int off = (stride % largest);
			if(off != 0) {
				fields.add(new StructEntry("std430Padding", getTypeFromSize(largest - off), true));
			}
		}

		this.stride = calculateStride();
		this.offsets = generateOffsets();

		Logger.debug("[Struct]: Created new struct: {}", toString());
	}

	private boolean isMultiple(int multiple, int of) {
		return multiple % of == 0;
	}

	private int[] generateOffsets() {
		int[] offsets = new int[fields.size()];
		int offset = 0;

		for(int i = 0; i < fields.size(); i++) {
			offsets[i] = offset;
			offset += fields.get(i).type().getSize();
		}

		return offsets;
	}

	private int calculateSize() {
		int size = 0;
		for(StructEntry entry : fields) {
			if(!entry.isPadding()) {
				size += entry.type().getSize();
			}
		}
		return size;
	}

	private int calculateStride() {
		int stride = 0;
		for(StructEntry entry : fields) {
			stride += entry.type().getSize();
		}
		return stride;
	}

	private StructEntry findOrPad(int desiredSize, int index) {
		int paddingCount = 0;

		for (int i = index + 1; i < fields.size(); i++) {
			StructEntry entry = fields.get(i);
			if (!entry.isPadding()) {
				if (entry.type().getSize() == desiredSize && fields.indexOf(entry) > index) {
					return entry;
				}
			} else {
				paddingCount++;
			}
		}

		return new StructEntry("padding_" + paddingCount, getTypeFromSize(desiredSize), true);
	}

	private StructEntry.Type getTypeFromSize(int size) {
		return switch (size) {
			case 4 -> StructEntry.Type.FLOAT;
			case 8 -> StructEntry.Type.VEC2;
			case 12 -> StructEntry.Type.VEC3;
			case 16 -> StructEntry.Type.VEC4;
			default -> throw new IllegalStateException("Unexpected value: " + size);
		};
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder().append("\n\n");
		builder.append(String.format("Size: %1$s, Stride: %2$s", size, stride)).append("\n");
		for(StructEntry entry : fields) {
			builder.append(entry.toString()).append("\n");
		}
		return builder.toString();
	}

	public int[] getOffsets() {
		return offsets;
	}

	public int getOffset(int index) {
		return offsets[index];
	}


	public int getSize() {
		return size;
	}

	public int getStride() {
		return stride;
	}

	public static class GenericFloatStruct extends Struct {

		public GenericFloatStruct(StructType structType) {
			super(structType);
		}

		@Override
		public Class<?> getRecord() {
			return FloatStruct.class;
		}

		public record FloatStruct(float val){}
	}

	public static class GenericMatrix4fStruct extends Struct {

		public GenericMatrix4fStruct(StructType structType) {
			super(structType);
		}

		@Override
		public Class<?> getRecord() {
			return Matrix4fStruct.class;
		}

		public record Matrix4fStruct(Matrix4f val){}
	}
}
