package net.ice.relic.common.test.gui;

import imgui.ImGuiTextFilter;
import imgui.ImVec2;
import imgui.type.ImString;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.texture.BindlessTexture;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;
import static imgui.ImGui.selectable;
import static imgui.flag.ImGuiWindowFlags.*;

public class ImageViewer {

    private ImString buffer = new ImString(256);
    private RelicApplication application;

    private BindlessTexture selectedTexture;

    private int selectedIndex;

    public ImageViewer(RelicApplication relicApplication) {
        this.application = relicApplication;
    }


    public void draw() {
        if(begin("Image Viewer", NoResize | NoScrollbar | AlwaysAutoResize)) {
            ImVec2 size = new ImVec2(20f, 20f);

            size.plus(combo());

            size.plus(showImage());

            setWindowSize(size.x, size.y);
            end();
        }

    }

    private ImVec2 combo() {
        ImVec2 size = new ImVec2(0, 0);

        List<Texture> textures = new ArrayList<>(application.getTextureCache().getTextureMaps());

        if(beginCombo("Textures", String.valueOf(textures.get(selectedIndex).getImage().getResource().getPath()))) {
            ImGuiTextFilter filter = new ImGuiTextFilter();
            if(isWindowAppearing()) {
                setKeyboardFocusHere();
                filter.clear();
            }
            filter.draw("##Filter", 300);
            for (int i = 0; i < textures.size(); i++) {
                boolean selected = selectedIndex == i;
                if(filter.passFilter(String.valueOf(textures.get(i).getImage().getResource().getPath()))) {
                    if(selectable(String.valueOf(textures.get(i).getImage().getResource().getPath()), selected)) {
                        selectedIndex = i;
                        selectedTexture = (BindlessTexture) textures.get(i);
                    }
                }
            }
            size = getItemRectSize();

            endCombo();
        }

        if(selectedTexture == null) {
            selectedTexture = (BindlessTexture) textures.getFirst();
        }

        return size;
    }

    private ImVec2 showImage() {
        float scale = 0.5f;
        int width = 2048;
        int height = 2048;

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


