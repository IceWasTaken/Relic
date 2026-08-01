package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;

import java.util.List;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class MaterialMapBuffer {

	private GLBuffer materialBuffer;
	private Struct materialBufferStruct;

	private GLBuffer mapBuffer;
	private Struct mapBufferStruct;

	private Fence fence;

	public void createMaterialMapBuffer() {
		if(materialBuffer != null) {
			materialBuffer.destroy();
		}

		if(mapBuffer != null) {
			mapBuffer.destroy();
		}

		this.fence = new GLFence();

		this.materialBufferStruct = new Material.MaterialStruct(StructType.STD430);
		this.mapBufferStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return MapRecord.class;
			}
		};

		this.materialBuffer = new GLBuffer(44 * 200, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.mapBuffer = new GLBuffer(8 * 3 * 200, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

		materialBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 7);
		mapBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 8);
	}

	public void updateMaterialMapBuffer(GLRenderer renderer) {
		int index = 0;

		fence.waitSync();

		for (Material material : renderer.getApplication().getMaterialCache().getMaterialsList()) {
			int base = materialBufferStruct.getStride() * index;

			materialBuffer.putVec4f(materialBufferStruct.getOffset(0) + base, material.getDiffuseColor().div().vec4f());
			materialBuffer.putVec4f(materialBufferStruct.getOffset(1) + base, material.getSpecularColor().div().vec4f());
			materialBuffer.putFloat(materialBufferStruct.getOffset(2) + base, material.getReflectance());
			materialBuffer.putFloat(materialBufferStruct.getOffset(3) + base, material.getRoughnessFactor());
			materialBuffer.putFloat(materialBufferStruct.getOffset(4) + base, material.getMetallicFactor());

			base = mapBufferStruct.getStride() * index;

			mapBuffer.putLong(mapBufferStruct.getOffset(0) + base, material.getTextureHandle());
			mapBuffer.putLong(mapBufferStruct.getOffset(1) + base, material.getNormalHandle());
			mapBuffer.putLong(mapBufferStruct.getOffset(2) + base, material.getRoughnessHandle());

			index++;
		}
	}

	public void sync() {
		fence.sync();
	}

	public record MapRecord(
			long albedoMaps,
			long normalMaps,
			long pbrMaps
	) {}
}
