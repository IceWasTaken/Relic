package net.ice.talisman.gui.builder.objs;

public enum GuiOBJTypes {
    BUTTON("button"),
    LABEL("label"),
    EDIT("edit"),
    SLIDER_I("slideri"),
    SLIDER_F("sliderf"),
    CHECKBOX("checkbox"),
    RADIO("radio"),
    TOGGLE("toggle"),

    NONE("");

    private String asString;

    GuiOBJTypes(String asString) {

    }

    public String getAsString() {
        return asString;
    }
}
