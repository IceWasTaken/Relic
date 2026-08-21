package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class GLAnimationRenderer implements Lifecycle {

	private GLShaderProgram shaderProgram;
	private GLBuffer drawParameterBuffer;
	private Uniforms uniforms;
	private Viewport viewport;

	private final GLRenderer renderer;

	private final Struct drawParameterStruct;
	private final Struct floatStruct;
	private final Struct matrix4fStruct;

	public GLAnimationRenderer(GLRenderer renderer) {
		this.renderer = renderer;
//		this.viewport = new GLViewport(
//				renderer.getApplication().getWindow().getWidth(),
//				renderer.getApplication().getWindow().getHeight()
//		);
		this.drawParameterStruct = new DrawParameterStruct(StructType.STD430);
		this.floatStruct = new Struct.GenericFloatStruct(StructType.STD430);
		this.matrix4fStruct = new Struct.GenericMatrix4fStruct(StructType.STD430);
	}

	@Override
	public void init() {
		this.shaderProgram = new GLShaderProgram("animation");

		this.uniforms = new Uniforms(shaderProgram.getProgramID());

		drawParameterBuffer = new GLBuffer(
				drawParameterStruct.getStride() * 1000L,
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		).bind(GL_SHADER_STORAGE_BUFFER, 4);
	}

	@Override
	public void render() {
		shaderProgram.bind();

//		int dstOffset = 0;
//		for(Model model : ) {
//			if(model.isAnimated()) {
//				for(MeshData meshData : model.getMeshData()) {
//
//				}
//
//			}
//		}
	}

	public static class DrawParameterStruct extends Struct {

		public DrawParameterStruct(StructType structType) {
			super(structType);
		}

		@Override
		public Class<?> getRecord() {
			return DrawParameterRecord.class;
		}

		public record DrawParameterRecord(
				int srcOffset,
				int srcSize,
				int weightsOffset,
				int bonesMatricesOffset,
				int dstOffset
		){}
	}

}
