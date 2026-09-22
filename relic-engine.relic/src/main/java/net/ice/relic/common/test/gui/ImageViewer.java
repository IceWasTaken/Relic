package net.ice.relic.common.test.gui;

import imgui.ImGuiTextFilter;
import imgui.ImVec2;
import imgui.type.ImString;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.relic.RelicApplication;
import net.ice.relic.core.gui.GuiContext;
import net.ice.relic.core.gui.drawable.GuiWindow;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiWindowFlags.*;

public class ImageViewer extends GuiWindow {

    private ImString buffer = new ImString(256);

    private GLTexture selectedTexture;

    private int selectedIndex;

    public ImageViewer(RelicApplication relicApplication) {
	    super(new GuiInfo(
                "Image Viewer",
                NoResize | NoScrollbar | AlwaysAutoResize
        ), relicApplication);
    }


    @Override
    protected void draw(GuiContext ctx) {
        ImVec2 size = new ImVec2(20f, 20f);

        size.plus(combo(ctx));

        size.plus(showImage());

        setWindowSize(size.x, size.y);
    }

    private ImVec2 combo(GuiContext ctx) {
        ImVec2 size = new ImVec2(0, 0);

        List<Texture> textures = new ArrayList<>(ctx.getApplication().getTextureCache().getTextureMaps());

        if(beginCombo("Textures", selectedIndex + ":" + textures.get(selectedIndex).getImage().getResource().getAsPath())) {
            ImGuiTextFilter filter = new ImGuiTextFilter();
            if(isWindowAppearing()) {
                setKeyboardFocusHere();
                filter.clear();
            }
            filter.draw("##Filter", 300);
            for (int i = 0; i < textures.size(); i++) {
                boolean selected = selectedIndex == i;
                if(filter.passFilter(String.valueOf(textures.get(i).getImage().getResource().getAsPath()))) {
                    if(selectable(String.valueOf(textures.get(i).getImage().getResource().getAsPath()), selected)) {
                        selectedIndex = i;
                        selectedTexture = (GLTexture) textures.get(i);
                    }
                }
            }
            size = getItemRectSize();

            endCombo();
        }

        if(selectedTexture == null) {
            selectedTexture = (GLTexture) textures.getFirst();
        }

        return size;
    }

    private ImVec2 showImage() {
        float scale = 0.5f;
        int width = selectedTexture.getImage().getWidth();
        int height = selectedTexture.getImage().getHeight();

        int scaledWidth = Math.max((int) (width * scale), 512);
        int scaledHeight = Math.max((int) (height * scale), 512);

        width = scaledWidth;
        height = scaledHeight;

        text("Selected Texture Handle: " + selectedTexture.getHandle());
        text("Selected Texture ID: " + selectedTexture.getTextureHandle());
        text("Selected Texture Resource: " + selectedTexture.getImage().getResource().getAsPath());
        text(String.format("Size = %d x %d", 2048, 2048));

        image(selectedTexture.getTextureHandle(), scaledWidth, scaledHeight);
        return new ImVec2(width, height);
    }
}


