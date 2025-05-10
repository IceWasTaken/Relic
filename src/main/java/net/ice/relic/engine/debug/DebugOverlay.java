//package net.ice.relic.engine.debug;
//
//import net.ice.relic.engine.common.Clock;
//import net.ice.relic.engine.opengl.Camera;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//import org.lwjgl.BufferUtils;
//import org.lwjgl.stb.STBEasyFont;
//
//import java.nio.ByteBuffer;
//
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL20.glUseProgram;
//
//@Deprecated
//public class DebugOverlay {
//    private boolean visible = true;
//    private final ByteBuffer charBuffer;
//
//    public DebugOverlay() {
//        charBuffer = BufferUtils.createByteBuffer(1024 * 8);
//    }
//
//    public void toggle() {
//        visible = !visible;
//    }
//
//    public void render(Camera camera, Clock clock, int windowWidth, int windowHeight, boolean resetToWireframe) {
//        if (!visible) return;
//
//        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
//
//        glUseProgram(0);
//
//        glMatrixMode(GL_PROJECTION);
//        glPushMatrix();
//        glLoadIdentity();
//        glOrtho(0, windowWidth, windowHeight, 0, -1, 1);
//
//        glMatrixMode(GL_MODELVIEW);
//        glPushMatrix();
//        glLoadIdentity();
//
//        glDisable(GL_DEPTH_TEST);
//        glDisable(GL_LIGHTING);
//        glDisable(GL_TEXTURE_2D);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//        float scale = 2.0f;
//
//        // Background panel dimensions must be scaled accordingly
//        float panelX = 10;
//        float panelY = 10;
//        float panelW = 180 * scale;
//        float panelH = 60 * scale;
//        drawBackgroundPanel(panelX, panelY, panelW, panelH);
//
//        // Apply scaling for text
//        glPushMatrix();
//        glScalef(scale, scale, 1.0f);
//
//        float x = panelX / scale + 4;
//        float y = panelY / scale + 5;
//
//        drawText(String.format("FPS: %.1f", 1.0f / clock.deltaTime), x, y);
//        drawText(String.format("Delta Time: %.6f", clock.deltaTime), x, y + 10);
//        drawText(String.format("Avg FPS (5s): %.1f", clock.getAverageFrameTimes()), x, y + 20);
//        Vector3f pos = camera.getPosition();
//        drawText(String.format("Position: [%.2f, %.2f, %.2f]", pos.x, pos.y, pos.z), x, y + 30);
//        Quaternionf rot = camera.getOrientation();
//        drawText(String.format("Rotation: [%.2f, %.2f, %.2f, %.2f]", rot.x, rot.y, rot.z, rot.w), x, y + 40);
//
//
//        glPopMatrix();
//
//        glEnable(GL_DEPTH_TEST);
//        glDisable(GL_BLEND);
//
//        glMatrixMode(GL_MODELVIEW);
//        glPopMatrix();
//        glMatrixMode(GL_PROJECTION);
//        glPopMatrix();
//
//        if(resetToWireframe) {
//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
//        }
//    }
//
//    private void drawBackgroundPanel(float x, float y, float width, float height) {
//        glColor4f(0f, 0f, 0f, 0.6f);
//        glBegin(GL_QUADS);
//        glVertex2f(x, y);
//        glVertex2f(x + width, y);
//        glVertex2f(x + width, y + height);
//        glVertex2f(x, y + height);
//        glEnd();
//    }
//
//    private void drawText(String text, float x, float y) {
//        charBuffer.clear();
//        int quads = STBEasyFont.stb_easy_font_print(x, y, text, null, charBuffer);
//
//        glColor3f(1f, 1f, 1f);
//        glEnableClientState(GL_VERTEX_ARRAY);
//        glVertexPointer(2, GL_FLOAT, 16, charBuffer);
//        glDrawArrays(GL_QUADS, 0, quads * 4);
//        glDisableClientState(GL_VERTEX_ARRAY);
//    }
//}
