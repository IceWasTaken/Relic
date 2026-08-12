package net.ice.curio.library.opengl.object.resource;

import net.ice.heirloom.Lifecycle;
import org.tinylog.Logger;

import static org.lwjgl.opengl.ARBBindlessTexture.*;

public class BindlessTexture extends GLTexture implements Lifecycle {

    private final long bindlessHandle;

    public BindlessTexture(GLTexture glTexture) {
        super(glTexture);

        this.bindlessHandle = glGetTextureHandleARB(textureHandle);
        if (bindlessHandle == 0L) {
            Logger.error("Failed to get bindless handle for texture.");
            return;
        }

        glMakeTextureHandleResidentARB(bindlessHandle);

        if(!glIsTextureHandleResidentARB(bindlessHandle)) {
            Logger.error("Texture handle not resident: {}");
        }
    }

    @Override
    public void cleanup() {
        if (bindlessHandle != 0L) {
            glMakeTextureHandleNonResidentARB(bindlessHandle);
        }
        super.cleanup();
    }

    public boolean isResident() {
        return bindlessHandle != 0L;
    }

    @Override
    public long getHandle() {
        return bindlessHandle;
    }
}
