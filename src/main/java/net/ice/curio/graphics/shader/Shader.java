package net.ice.curio.graphics.shader;

import net.ice.curio.graphics.enums.ShaderStage;
import net.ice.heirloom.io.exception.AssetLoadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Shader {

	protected final ShaderStage stage;

	public abstract String getFilePrefix();

	protected Shader(ShaderStage stage) {
		this.stage = stage;
	}

	protected String loadFile(String fileName) {
		StringBuilder stringBuilder = new StringBuilder();

		try(InputStream stream = this.getClass().getResourceAsStream(getFilePrefix() + fileName)) {
			if(stream != null) {
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
					String line;
					while((line = reader.readLine()) != null) {
						stringBuilder.append(line).append("\n");
					}
				}
			}
		} catch (IOException ioException) {
			throw new AssetLoadException("Failed to load shader: " + fileName);
		}

		return stringBuilder.toString();
	}


}
