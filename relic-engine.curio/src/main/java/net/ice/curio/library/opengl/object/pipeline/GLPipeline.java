package net.ice.curio.library.opengl.object.pipeline;

import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.pipeline.Pipeline;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.CompareFunction;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.framebuffer.GLFramebuffer;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.library.opengl.object.Uniforms;
import org.tinylog.Logger;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.EXTDepthBoundsTest.GL_DEPTH_BOUNDS_TEST_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class GLPipeline extends Pipeline {

	public static GLPipeline currentPipeline;

	private final int shaderProgram;

	private final int mode;
	private final int polygonMode;
	private final int cullMode;
	private final int frontFace;
	private final boolean depthClamp;
	private final float lineWidth;

	private final boolean depthTest;
	private final boolean depthWrite;
	private final int depthCompareFunction;
	private final boolean depthBoundTest;
	private final boolean stencilTest;

	private final Viewport viewport;
	private final Uniforms uniforms;

	private final Optional<GLFramebuffer> framebuffer;

	public GLPipeline(
			String programPath,
			GLFramebuffer framebuffer,
			PrimitiveType primitiveType,
			RasterizationState rasterizationState,
			DepthState depthState,
			Viewport viewport
	){
		super(primitiveType, rasterizationState, depthState);

		Logger.debug("[Pipeline]: Creating new graphics pipeline");

		this.shaderProgram = glCreateProgram();
		this.uniforms = new Uniforms(shaderProgram);

		this.framebuffer = Optional.ofNullable(framebuffer);

		List<GLShader> shaders = GLShader.loadProgram(programPath);
		for(GLShader shader : shaders) {
			if(shader.getShaderHandle() == 0) {
				Logger.error("[Pipeline] Invalid shader passed to pipeline");
			}

			glAttachShader(shaderProgram, shader.getShaderHandle());
		}

		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);


		shaders.forEach(shader -> glDetachShader(shaderProgram, shader.getShaderHandle()));
		shaders.forEach(shader -> glDeleteShader(shader.getShaderHandle()));

		this.viewport = viewport;

		this.mode = getModeFromPrimitiveType();

		this.polygonMode = getPolygonMode();
		this.cullMode = getCullMode();
		this.frontFace = getFrontFace();
		this.depthClamp = rasterizationState.clampDepth();
		this.lineWidth = rasterizationState.lineWidth();

		this.depthTest = depthState.enableDepthTest();
		this.depthWrite = depthState.enableDepthWrite();
		this.depthCompareFunction = getCompareFunction(depthState.compareFunction());
		this.depthBoundTest = depthState.enableDepthBoundTest();
		this.stencilTest = depthState.enableStencilTest();
	}

	@Override
	public void bindPipeline() {
		currentPipeline = this;

		glUseProgram(shaderProgram);
		viewport.bind();
		framebuffer.ifPresent(GLFramebuffer::bind);
		framebuffer.ifPresent(GLFramebuffer::clear);

		//VkPipelineRasterizationStateCreateInfo
		glPolygonMode(cullMode, polygonMode);
		glCullFace(cullMode);
		glFrontFace(frontFace);
		glEnableDisable(depthClamp, GL_DEPTH_CLAMP);
		glLineWidth(lineWidth);

		//VkPipelineDepthStencilStateCreateInfo
		glEnableDisable(depthTest, GL_DEPTH_TEST);
		glDepthMask(depthWrite);
		glDepthFunc(depthCompareFunction);
		glEnableDisable(depthBoundTest, GL_DEPTH_BOUNDS_TEST_EXT);
		glEnableDisable(stencilTest, GL_STENCIL_TEST);
	}

	public Uniforms getUniforms() {
		return uniforms;
	}

	public Optional<GLFramebuffer> getFramebuffer() {
		return framebuffer;
	}

	public void resize(int width, int height) {
		this.framebuffer.ifPresent((fb) -> fb.resize(width, height));
		this.viewport.resize(width, height);
	}

	public int getMode() {
		return mode;
	}

	private void glEnableDisable(boolean enable, int val) {
		if(enable) {
			glEnable(val);
		} else {
			glDisable(val);
		}
	}

	private int getModeFromPrimitiveType() {
		return switch(primitiveType) {
			case POINT -> GL_POINTS;
			case LINE -> GL_LINES;
			case LINE_LOOP -> GL_LINE_LOOP;
			case LINE_STRIP -> GL_LINE_STRIP;
			case TRIANGLE -> GL_TRIANGLES;
			case TRIANGLE_STRIP -> GL_TRIANGLE_STRIP;
			case TRIANGLE_FAN -> GL_TRIANGLE_FAN;
		};
	}

	private int getPolygonMode() {
		return switch(rasterizationState.polygonMode()) {
			case FILL -> GL_FILL;
			case LINE -> GL_LINE;
			case POINT -> GL_POINT;
		};
	}

	private int getCullMode() {
		return switch(rasterizationState.cullMode()) {
			case NONE -> GL_NONE;
			case FRONT -> GL_FRONT;
			case BACK -> GL_BACK;
			case FRONT_AND_BACK -> GL_FRONT_AND_BACK;
		};
	}

	private int getFrontFace() {
		return switch(rasterizationState.frontFace()) {
			case CLOCKWISE -> GL_CW;
			case COUNTER_CLOCKWISE -> GL_CCW;
		};
	}

	private int getCompareFunction(CompareFunction compareFunction) {
		return switch(compareFunction) {
			case NEVER -> GL_NEVER;
			case LESS -> GL_LESS;
			case EQUAL -> GL_EQUAL;
			case LESS_OR_EQUAL -> GL_LEQUAL;
			case GREATER -> GL_GREATER;
			case NOT_EQUAL -> GL_NOTEQUAL;
			case GREATER_OR_EQUAL -> GL_GEQUAL;
			case ALWAYS -> GL_ALWAYS;
		};
	}
}
