package net.ice.relic.core.rendering.backend.opengl.renderers;

import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImInt;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.curio.library.opengl.wrapper.enums.texture.ImageFormat;
import net.ice.curio.library.opengl.wrapper.enums.texture.TextureType;
import net.ice.curio.library.opengl.wrapper.enums.texture.parameter.FilteringParameter;
import net.ice.curio.library.stb.Bitmap;
import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.event.EventManager;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.mesh.GuiMesh;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

import static imgui.flag.ImGuiBackendFlags.HasMouseCursors;
import static imgui.flag.ImGuiConfigFlags.NavEnableKeyboard;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

public class GLGuiRenderer implements Lifecycle {

    private GLShaderProgram shaderProgram;
    private GuiMesh guiMesh;
    private Vector2f scale;
    private GLTexture texture;
    private Uniforms uniforms;

    private final GLRenderer glRenderer;

    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();


    public GLGuiRenderer(GLRenderer renderer) {
        this.glRenderer = renderer;
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram("gui");

        this.uniforms = new Uniforms(shaderProgram);
        this.scale = new Vector2f();

        uniforms.createUniform("scale");
        uniforms.createUniform("imageSampler");

        EventManager.addListener(this);

        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();

        imGuiIO.setIniFilename(null);
        imGuiIO.setConfigFlags(NavEnableKeyboard);
        imGuiIO.setBackendFlags(HasMouseCursors);
        imGuiIO.setBackendPlatformName("imgui_java_impl_glfw");
        imGuiIO.setConfigWindowsMoveFromTitleBarOnly(true);

        imGuiIO.setDisplaySize(glRenderer.getApplication().getWindow().getWidth(), glRenderer.getApplication().getWindow().getHeight());

        buildFontAtlas();

        imGuiGl3.init("version 330 core");
    }

    @Override
    public void render() {
        Gui guiInstance = glRenderer.getApplication().getCurrentScene().getGUI();
        if(guiInstance == null) {
            return;
        }
        guiInstance.draw();

        shaderProgram.bind();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDisable(GL_FRAMEBUFFER_SRGB);

        guiMesh.getVAO().bind();

        ImGuiIO io = ImGui.getIO();
        scale.x = 2.0f / io.getDisplaySizeX();
        scale.y = -2.0f / io.getDisplaySizeY();
        uniforms.setUniform("scale", scale);


        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for (int i = 0; i < numLists; i++) {
            guiMesh.updateBuffers(i);

            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for (int j = 0; j < numCmds; j++) {
                final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = idxBufferOffset * ImDrawData.sizeOfImDrawIdx();
                final int textureID = drawData.getCmdListCmdBufferTextureId(i, j);

                if(textureID != 0) {
                    glBindTextureUnit(0, textureID);
                } else {
                    texture.bind(0);
                }

                uniforms.setUniform("imageSampler", 0);
                glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
            }
            guiMesh.getFence().sync();
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_FRAMEBUFFER_SRGB);
        glDisable(GL_BLEND);

        shaderProgram.unbind();
    }

    private void buildFontAtlas() {
        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
        texture = new GLTexture.TextureBuilder()
                .minificationFilter(FilteringParameter.NEAREST)
                .magnificationFiler(FilteringParameter.NEAREST)
                .textureType(TextureType.TEXTURE_2D)
                .imageFormat(ImageFormat.RGBA8)
                .buildWithDataAndMipmaps(new Bitmap(width.get(), height.get(), 4,  buf));
        guiMesh = new GuiMesh();
    }



    public void onResize(int width, int height) {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(width, height);
        buildFontAtlas();
    }

}