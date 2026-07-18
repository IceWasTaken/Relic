package net.ice.relic.common.test.gui;

import net.ice.relic.core.scene.Scene;
import org.joml.Vector3f;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiWindowFlags.*;

public class SceneInfoGui {

    public void draw(Scene scene) {
        if(begin("Scene Info", NoResize | NoScrollbar | AlwaysAutoResize)) {
            info(scene);
            end();
        }
    }

    private void info(Scene scene) {
        Vector3f pos = scene.getCamera().getPosition();

        text(String.format("Camera Pos: %f, %f, %f", pos.x, pos.y, pos.z));
    }

}
