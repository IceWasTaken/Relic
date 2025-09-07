package net.ice.talisman.gui.builder;


import imgui.ImVec2;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class GuiBuilderClasses {

    public enum TypeOBJ {
        BUTTON,
        LABEL,
        SLIDER_I,
        SLIDER_F,
        CHECKBOX,
        RADIO,
        TOGGLE
    }

    public enum ResizeOptions {
        OFF,
        BOTTOM_RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public static class BasicOBJ {
        public int id = 0;
        public int form = 0;
        public int child = -1;
        public ImString name = new ImString("obj");
        public int myType = 0;

        public ImVec2 size = new ImVec2();
        public ImVec2 pos = new ImVec2(10, 10);
        public ImVec2 sizeObj = new ImVec2();

        public boolean changePos = false;
        public boolean hover = false;
        public boolean deleteMe = false;
        public boolean selected = false;
        public boolean locked = false;
    }

    public static class Child {
        public int id = 0;
        public String name = "form";
        public int father = 0;
        public boolean border = true;

        public ImVec2 size = new ImVec2();
        public ImVec2 pos = new ImVec2();

        public boolean changePos = false;
        public boolean hover = false;
        public boolean deleteMe = false;
        public boolean selected = false;
        public boolean locked = false;

    }


}

