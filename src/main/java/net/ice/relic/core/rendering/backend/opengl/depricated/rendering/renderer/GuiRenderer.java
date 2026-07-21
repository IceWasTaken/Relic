package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.renderer;

import imgui.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImInt;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.curio.window.Window;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.depricated.AbstractGLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.rendering.backend.opengl.depricated.rendering.GuiMesh;
import net.ice.relic.core.rendering.shader.ShaderType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.ByteBuffer;

import static imgui.flag.ImGuiBackendFlags.HasMouseCursors;
import static imgui.flag.ImGuiConfigFlags.NavEnableKeyboard;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL15.*;

@Deprecated
public class GuiRenderer extends AbstractGLRenderer {

    private GLFWKeyCallback prevKeyCallBack;
    private Window window;
    private Vector2f scale;
    private GuiMesh guiMesh;
    private TextureCache textureCache;

    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private GLTexture texture;

    public GuiRenderer(GLManager glManager) {
        super(glManager);
        this.window = glManager.getApplication().getWindow();
        this.textureCache = glManager.getApplication().getTextureCache();
    }

    @Override
    public void init() {
        super.init();
        createUIResources();
    }

    @Override
    protected void initShaders() {
        loadShader("gui.vert", ShaderType.VERTEX);
        loadShader("gui.frag", ShaderType.FRAGMENT);
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("scale");
        uniforms.createUniform("textureHandle");
        scale = new Vector2f();
    }

    @Override
    public void render() {
        Gui guiInstance = manager.getApplication().getCurrentScene().getGUI();
        if (guiInstance == null) {
            return;
        }
        guiInstance.draw();
        shaderProgram.bind();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        //glDisable(GL_FRAMEBUFFER_SRGB);

        guiMesh.getVAO().bind();

//        guiMesh.getVerticesVBO().bind();
//        guiMesh.getIndicesVBO().bind();

        ImGuiIO io = ImGui.getIO();
        scale.x = 2.0f / io.getDisplaySizeX();
        scale.y = -2.0f / io.getDisplaySizeY();
        uniforms.setUniform("scale", scale);

        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for (int i = 0; i < numLists; i++) {
            glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);

            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for (int j = 0; j < numCmds; j++) {
                final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = idxBufferOffset * ImDrawData.sizeOfImDrawIdx();
                final int textureID = drawData.getCmdListCmdBufferTextureId(i, j);

                //uniforms.setUniform("textureHandle", determineTextureHandle(textureID));
                glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
            }
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        //glEnable(GL_FRAMEBUFFER_SRGB);
    }

    @Override
    protected void setupData() {

    }

//    private long determineTextureHandle(int textureID) {
//        for(BindlessTexture glTexture : textureCache.getTextureMaps()) {
//            if(textureID == glTexture.getTextureHandle()) {
//                return glTexture.getBindlessHandle();
//            }
//        }
//        return texture.getTextureHandle();
//    }

    private void createUIResources() {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();

        imGuiIO.setIniFilename(null);
        imGuiIO.setConfigFlags(NavEnableKeyboard);
        imGuiIO.setBackendFlags(HasMouseCursors);
        imGuiIO.setBackendPlatformName("imgui_java_impl_glfw");

        imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
        //texture = new GLTexture(new Image(width.get(), height.get(), buf));
        guiMesh = new GuiMesh();

        imGuiGl3.init("version 330 core");
    }

    public void resize() {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());
    }

}
