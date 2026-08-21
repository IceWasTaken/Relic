package net.ice.curio.graphics.object.resource;

import net.ice.curio.library.stb.Bitmap;
import net.ice.heirloom.Lifecycle;

public abstract class Texture implements Lifecycle {

    protected final Bitmap image;

    protected Texture(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public abstract long getHandle();

}
