package net.ice.curio.graphics.object.resource;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.library.stb.Bitmap;

public abstract class Texture {

    protected final int width;
    protected final int height;

    protected final Bitmap image;
    protected final GraphicsContext context;

    public abstract void cleanup();
    public abstract long getHandle();

    protected Texture(GraphicsContext context, Bitmap image) {
        this.context = context;
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
