package net.ice.talisman.gui.builder.objs;

import imgui.ImVec2;
import net.ice.talisman.gui.builder.GuiBuilderClasses;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Form {

    private int id = 0;
    private boolean changePos = false;
    private boolean hover = false;
    private boolean shouldDelete = false;

    private String name = "form";
    private Vector2f size = new Vector2f();
    private Vector2f pos = new Vector2f();
    private List<GuiBuilderClasses.Child> children = new ArrayList<>();

    public Form() {

    }

    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }

    public boolean shouldChangePos() {
        return changePos;
    }
    public boolean isHovered() {
        return hover;
    }

    public boolean shouldDelete() {
        return shouldDelete;
    }
    public void setShouldDelete(boolean shouldDelete) {
        this.shouldDelete = shouldDelete;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Vector2f getSize() {
        return size;
    }
    public void setSize(Vector2f size) {
        this.size = size;
    }
    public void setSize(ImVec2 imVec2) {
        this.size = new Vector2f(imVec2.x, imVec2.y);
    }
    public void setSize(float x, float y) {
        this.size = new Vector2f(x, y);
    }

    public Vector2f getPos() {
        return pos;
    }
    public void setPos(Vector2f size) {
        this.pos = size;
    }
    public void setPos(ImVec2 imVec2) {
        this.pos = new Vector2f(imVec2.x, imVec2.y);
    }
    public void setPos(float x, float y) {
        this.pos = new Vector2f(x, y);
    }

    public List<GuiBuilderClasses.Child> getChildren() {
        return children;
    }
}
