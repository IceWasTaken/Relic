package net.ice.curio.graphics.object.pipeline.shader;

import net.ice.curio.graphics.context.GraphicsContext;

public abstract class Shader {

	protected final ShaderType type;

	protected final GraphicsContext context;

	protected Shader(GraphicsContext context, ShaderType type) {
		this.context = context;
		this.type = type;
	}

	protected static ShaderType getShaderTypeFromFileExtension(String fileName) {
		if(fileName.endsWith("vert")) {
			return ShaderType.VERTEX;
		} else if(fileName.endsWith("geom")) {
			return ShaderType.GEOMETRY;
		} else if(fileName.endsWith("frag")) {
			return ShaderType.FRAGMENT;
		} else if(fileName.endsWith("comp")) {
			return ShaderType.COMPUTE;
		}

		throw new RuntimeException("gfdghfdshsdfg");
	}

	public ShaderType getType() {
		return type;
	}
}
