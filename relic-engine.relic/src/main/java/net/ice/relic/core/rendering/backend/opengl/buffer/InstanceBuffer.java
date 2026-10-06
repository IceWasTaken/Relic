package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.material.Material;
import net.ice.relic.core.model.material.MaterialColor;
import net.ice.relic.core.model.material.MaterialFactor;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.BufferManager;
import org.joml.Matrix4f;
import org.tinylog.Logger;


import static org.lwjgl.opengl.ARBBufferStorage.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.ARBBufferStorage.GL_MAP_PERSISTENT_BIT;
import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;

public class InstanceBuffer {

	private GLBuffer instanceBuffer;
	private Struct instanceBufferStruct;

	private GLBuffer materialBuffer;
	private Struct materialBufferStruct;

	private GLBuffer mapBuffer;
	private Struct mapBufferStruct;

	private Fence fence;

	private final BufferManager bufferManager;

	public InstanceBuffer(BufferManager bufferManager) {
		this.bufferManager = bufferManager;

	}

	public void init() {
		this.materialBufferStruct = new Material.MaterialStruct(StructType.STD430);

		this.instanceBufferStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return InstanceStruct.class;
			}
		};

		this.mapBufferStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return MapRecord.class;
			}
		};

		this.materialBuffer = new GLBuffer(1000 * 200L, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT).bind(GL_SHADER_STORAGE_BUFFER, 5);
		this.mapBuffer = new GLBuffer(1000 * 200L, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT).bind(GL_SHADER_STORAGE_BUFFER, 6);
		this.instanceBuffer = new GLBuffer(instanceBufferStruct.getStride() * 5000L, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT).bind(GL_SHADER_STORAGE_BUFFER, 7);

		this.fence = new GLFence();

		Logger.debug("[InstanceBuffer]: Created and bound material buffer to index 5");
		Logger.debug("[InstanceBuffer]: Created and bound map buffer to index 6");
		Logger.debug("[InstanceBuffer]: Created and bound instance buffer to index 7");
	}

	public void update(MaterialCache materialCache) {
		int instanceIndex = 0;

		fence.waitSync();

		for(Model model : bufferManager.getLoadedModels()) {
			for(MeshData mesh : model.getMeshData()) {
				for(Entity entity : model.getEntities()) {
					int base = instanceBufferStruct.getStride() * instanceIndex;
					instanceBuffer.putMatrix4f(instanceBufferStruct.getOffset(0) + base, entity.getComponent(TransformComponent.class).getTransformationMatrix());
					instanceBuffer.putInt(instanceBufferStruct.getOffset(1) + base, mesh.getMaterialIndex());
					instanceIndex++;
				}


				Material material = materialCache.getMaterial(mesh.getMaterialIndex());
				int base = materialBufferStruct.getStride() * mesh.getMaterialIndex();

				materialBuffer.putVec4f(materialBufferStruct.getOffset(0) + base, material.getColor(MaterialColor.DIFFUSE).div().vec4f());
				materialBuffer.putVec4f(materialBufferStruct.getOffset(1) + base, material.getColor(MaterialColor.SPECULAR).div().vec4f());
				materialBuffer.putFloat(materialBufferStruct.getOffset(2) + base, material.getFactor(MaterialFactor.REFLECTANCE));
				materialBuffer.putFloat(materialBufferStruct.getOffset(3) + base, material.getFactor(MaterialFactor.ROUGHNESS_FACTOR));
				materialBuffer.putFloat(materialBufferStruct.getOffset(4) + base, material.getFactor(MaterialFactor.METALLIC_FACTOR));

				base = mapBufferStruct.getStride() * mesh.getMaterialIndex();

				mapBuffer.putLong(mapBufferStruct.getOffset(0) + base, material.getTextureHandle());
				mapBuffer.putLong(mapBufferStruct.getOffset(1) + base, material.getNormalHandle());
				mapBuffer.putLong(mapBufferStruct.getOffset(2) + base, material.getRoughnessHandle());
			}
		}
	}


	public void sync() {
		fence.sync();
	}

	public record InstanceStruct(Matrix4f transformIndex, int materialIndex) {}

	public record MapRecord(
			long albedoMaps,
			long normalMaps,
			long pbrMaps
	) {}
}
