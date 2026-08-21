package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.scene.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.tinylog.Logger;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class SceneInfoBuffer implements Lifecycle {

	private final Struct sceneInfoStruct;

	private GLBuffer sceneInfoBuffer;
	private Fence fence;

	public SceneInfoBuffer() {
		this.sceneInfoStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return SceneInfoStruct.class;
			}
		};
	}

	@Override
	public void init() {
		if(sceneInfoBuffer != null) {
			sceneInfoBuffer.cleanup();
		}

		this.fence = new GLFence();

		this.sceneInfoBuffer = new GLBuffer(
				sceneInfoStruct.getStride(),
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		).bind(GL_SHADER_STORAGE_BUFFER, 8);

		Logger.debug("[SceneInfoBuffer]: Created and bound SceneInfoBuffer to index 8");
	}

	public void update(Scene scene) {
		fence.waitSync();

		sceneInfoBuffer.putMatrix4f(sceneInfoStruct.getOffset(0), scene.getMatrix().getProjMatrix());
		sceneInfoBuffer.putMatrix4f(sceneInfoStruct.getOffset(1), scene.getCamera().getViewMatrix());
		sceneInfoBuffer.putVec3f(sceneInfoStruct.getOffset(2), scene.getCamera().getPosition());
		sceneInfoBuffer.putInt(sceneInfoStruct.getOffset(3), scene.getLightCount());
		sceneInfoBuffer.putVec3f(sceneInfoStruct.getOffset(4), scene.getAmbientLight().getColor().div().vec3f());
		sceneInfoBuffer.putFloat(sceneInfoStruct.getOffset(5), scene.getAmbientLight().getIntensity());
	}

	public void sync() {
		fence.sync();
	}

	record SceneInfoStruct(
			Matrix4f projectionMatrix,
			Matrix4f viewMatrix,

			Vector3f cameraPos,
			int lightCount,
			Vector3f ambientLightColor,
			float ambientLightStrength
	) {}


}
