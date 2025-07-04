package net.ice.relic.engine.test;

import net.ice.relic.engine.Window;
import net.ice.relic.engine.opengl.scene.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glColor3f;

public class DebugOverlayNew extends UIElement {

    public DebugOverlayNew(Window window) {
        super(window);
    }

    @Override
    protected void elementDetails(Window window, Camera camera, float x, float y) {
        Quaternionf rot = camera.getOrientation();
        Vector3f pos = camera.getPosition();

        glColor3f(1f, 1f, 1f);
//        drawText(String.format("FPS: %.1f", 1.0f / window.getClock().getDeltaTime()), x, y);
//        drawText(String.format("Delta Time: %.6f", window.getClock().getDeltaTime()), x, y + 10);
//        drawText(String.format("Avg FPS (5s): %.1f", window.getClock().getAverageFrameTimes()), x, y + 20);
        drawText(String.format("Position: [%.2f, %.2f, %.2f]", pos.x, pos.y, pos.z), x, y + 30);

        drawText(String.format("Rotation: [%.2f, %.2f, %.2f, %.2f]", rot.x, rot.y, rot.z, rot.w), x, y + 40);
    }
}
