package net.ice.relic.engine.opengl.model.texture;

import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {

    private int textureID;
    private final String texturePath;

    public Texture(int width, int height, ByteBuffer data) {
        this.texturePath = "";
        createTexture(width, height, data);
    }

    public Texture(String texturePath) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.texturePath = texturePath;
            IntBuffer widt = stack.mallocInt(1);
            IntBuffer heigh = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buffer = stbi_load(texturePath, widt, heigh, channels, 4);
            if(buffer == null) {
                throw new RuntimeException("Failed to load a texture file: " + texturePath);
            }


            int width = widt.get();
            int height = heigh.get();

            createTexture(width, height, buffer);


            Logger.debug("Loaded texture: " + texturePath + " with dimensions: " + width + "x" + height + " and channels: " + channels.get() + "");
            stbi_image_free(buffer);
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void cleanup() {
        glDeleteTextures(textureID);
    }

    private void createTexture(int width, int height, ByteBuffer data) {
        textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
    
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public String getTexturePath() {
        return texturePath;
    }

}