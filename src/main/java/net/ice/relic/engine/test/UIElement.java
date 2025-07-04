package net.ice.relic.engine.test;

import net.ice.relic.engine.Window;
import net.ice.relic.engine.opengl.scene.Camera;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBEasyFont;

import java.nio.ByteBuffer;

import static net.ice.relic.engine.util.ColorUtil.ColorDefaults.BLACK;
import static net.ice.relic.engine.util.ColorUtil.glSetColor4f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class UIElement {

    private boolean visible = true;
    private final ByteBuffer characterBuffer;

    protected Window window;

    protected UIElement(Window window) {
        this.window = window;
        this.characterBuffer = BufferUtils.createByteBuffer(1024 * 8);
    }

    protected abstract void elementDetails(Window window, Camera camera, float x, float y);

    public void render(Window window, Camera camera) {
        if(visible) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

            glUseProgram(0);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            //glOrtho(0, window.getOptions().width(), window.getOptions().height(), 0, -1, 1);

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            glDisable(GL_DEPTH_TEST);
            glDisable(GL_LIGHTING);
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            float scale = 2.0f;

            float panelX = 10;
            float panelY = 10;
            float panelW = 180 * scale;
            float panelH = 60 * scale;
            drawPanel(panelX, panelY, panelW, panelH);

            glPushMatrix();
            glScalef(scale, scale, 1.0f);

            float x = panelX / scale + 4;
            float y = panelY / scale + 5;

            elementDetails(window, camera, x, y);

            glPopMatrix();

            glEnable(GL_DEPTH_TEST);
            //glDisable(GL_BLEND);

            glMatrixMode(GL_MODELVIEW);
            glPopMatrix();
            glMatrixMode(GL_PROJECTION);
            glPopMatrix();

        }
    }

    protected void toggle() {
        visible = !visible;
    }

    protected void drawText(String text, float xPos, float yPos) {
        characterBuffer.clear();
        int quads = STBEasyFont.stb_easy_font_print(xPos, yPos, text, null, characterBuffer);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, characterBuffer);
        glDrawArrays(GL_QUADS, 0, quads * 4);
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    private void drawPanel(float x, float y, float width, float height) {
        glSetColor4f(BLACK.getColor(), 0.6f);

        float[] vertices = new float[]{
                x, y, 0.0f,
                x + width, y, 0.0f,
                x + width, y + height, 0.0f,
                x, y + height, 0.0f
        };

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_QUADS, 0, 4);

        glDisableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId); // clean up

        glFlush();
    }
}


