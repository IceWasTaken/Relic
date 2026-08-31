package net.ice.curio.graphics.object.resource;

import net.ice.curio.library.stb.Bitmap;
import net.ice.heirloom.Lifecycle;

public abstract class Texture implements Lifecycle {

    protected final int width;
    protected final int height;

    protected final Bitmap image;

    public abstract long getHandle();

    protected Texture(Bitmap image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public Bitmap getImage() {
        return image;
    }





}
