package net.ice.talisman.gui.builder;

import imgui.*;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImString;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCol.COUNT;
import static imgui.flag.ImGuiInputTextFlags.CallbackResize;

public class GuiBuilderAdditional {

//    public static boolean inputTextEx(String label, ImString str, int flags) {
//        flags |= CallbackResize;
//        return ImGui.inputText(label, str, flags, resizeCallback);
//    }



    public static void toggleButton(String strId, boolean[] v) {
        ImVec2 p = getCursorScreenPos();
        ImDrawList drawList = getWindowDrawList();

        float height = getFrameHeight();
        float width = height * 1.55f;
        float radius = height * 0.5f;

        if (invisibleButton(strId, new ImVec2(width, height))) {
            v[0] = !v[0]; // boolean array used as mutable reference
        }

        int colBg;
        if (isItemHovered()) {
            colBg = v[0] ? getColorU32(145 + 20, 211, 68 + 20, 255)
                    : getColorU32(218 - 20, 218 - 20, 218 - 20, 255);
        } else {
            colBg = v[0] ? getColorU32(145, 211, 68, 255)
                    : getColorU32(218, 218, 218, 255);
        }

        drawList.addRectFilled(p, new ImVec2(p.x + width, p.y + height), colBg, height * 0.5f);
        drawList.addCircleFilled(
                new ImVec2(v[0] ? (p.x + width - radius) : (p.x + radius), p.y + radius),
                radius - 1.5f,
                getColorU32(255, 255, 255, 255)
        );
    }


    public static void pushAllColorsDark(ImGuiStyle dark) {
        ImGuiStyle style = getStyle();

        for (int i = 0; i < COUNT; i++) {
            pushStyleColor(i, dark.getColors()[i]);
        }

        style.setWindowPadding(dark.getWindowPadding());
        style.setFramePadding(dark.getFramePadding());
        style.setCellPadding(dark.getCellPadding());
        style.setItemSpacing(dark.getItemSpacing());
        style.setItemInnerSpacing(dark.getItemInnerSpacing());
        style.setTouchExtraPadding(dark.getTouchExtraPadding());
        style.setIndentSpacing(dark.getIndentSpacing());
        style.setScrollbarSize(dark.getScrollbarSize());
        style.setGrabMinSize(dark.getGrabMinSize());
        style.setWindowBorderSize(dark.getWindowBorderSize());
        style.setChildBorderSize(dark.getChildBorderSize());
        style.setPopupBorderSize(dark.getPopupBorderSize());
        style.setFrameBorderSize(dark.getFrameBorderSize());
        style.setTabBorderSize(dark.getTabBorderSize());

        style.setWindowRounding(dark.getWindowRounding());
        style.setChildRounding(dark.getChildRounding());
        style.setFrameRounding(dark.getFrameRounding());
        style.setPopupRounding(dark.getPopupRounding());
        style.setScrollbarRounding(dark.getScrollbarRounding());
        style.setGrabRounding(dark.getGrabRounding());
        style.setLogSliderDeadzone(dark.getLogSliderDeadzone());
        style.setTabRounding(dark.getTabRounding());

        style.setWindowTitleAlign(dark.getWindowTitleAlign());
        style.setWindowMenuButtonPosition(dark.getWindowMenuButtonPosition());
        style.setColorButtonPosition(dark.getColorButtonPosition());
        style.setButtonTextAlign(dark.getButtonTextAlign());
        style.setSelectableTextAlign(dark.getSelectableTextAlign());

        style.setDisplaySafeAreaPadding(dark.getDisplaySafeAreaPadding());
    }

    public static void pushAllColorsCustom( ImGuiStyle guiStyle) {
        ImGuiStyle style = getStyle();

        for (int i = 0; i < COUNT; i++ )
        {
            pushStyleColor(i, guiStyle.getColors()[i]);
        }
        style.setWindowPadding(guiStyle.getWindowPadding());
        style.setFramePadding(guiStyle.getFramePadding());
        style.setCellPadding(guiStyle.getCellPadding());
        style.setItemSpacing(guiStyle.getItemSpacing());
        style.setItemInnerSpacing(guiStyle.getItemInnerSpacing());
        style.setTouchExtraPadding(guiStyle.getTouchExtraPadding());
        style.setIndentSpacing(guiStyle.getIndentSpacing());
        style.setScrollbarSize(guiStyle.getScrollbarSize());
        style.setGrabMinSize(guiStyle.getGrabMinSize());
        style.setWindowBorderSize(guiStyle.getWindowBorderSize());
        style.setChildBorderSize(guiStyle.getChildBorderSize());
        style.setPopupBorderSize(guiStyle.getPopupBorderSize());
        style.setFrameBorderSize(guiStyle.getFrameBorderSize());
        style.setTabBorderSize(guiStyle.getTabBorderSize());

        style.setWindowRounding(guiStyle.getWindowRounding());
        style.setChildRounding(guiStyle.getChildRounding());
        style.setFrameRounding(guiStyle.getFrameRounding());
        style.setPopupRounding(guiStyle.getPopupRounding());
        style.setScrollbarRounding(guiStyle.getScrollbarRounding());
        style.setGrabRounding(guiStyle.getGrabRounding());
        style.setLogSliderDeadzone(guiStyle.getLogSliderDeadzone());
        style.setTabRounding(guiStyle.getTabRounding());

        style.setWindowTitleAlign(guiStyle.getWindowTitleAlign());
        style.setWindowMenuButtonPosition(guiStyle.getWindowMenuButtonPosition());
        style.setColorButtonPosition(guiStyle.getColorButtonPosition());
        style.setButtonTextAlign(guiStyle.getButtonTextAlign());
        style.setSelectableTextAlign(guiStyle.getSelectableTextAlign());

        style.setDisplaySafeAreaPadding(guiStyle.getDisplaySafeAreaPadding());
    }

    public static void popAllColorsCustom() {
        popStyleColor(COUNT);
    }

    public static void drawObjBorder(GuiBuilderClasses.BasicOBJ obj) {
        drawObjBorder(obj.pos, obj.size, 5, 0xFF00FFFF);
    }

    public static void drawObjBorder(ImVec2 pos, ImVec2 size) {
        drawObjBorder(pos, size, 5, 0xFF00FFFF);
    }

    public static void drawObjBorder(GuiBuilderClasses.BasicOBJ obj, float distanceThickness, int col) {
        drawObjBorder(obj.pos, obj.sizeObj, distanceThickness, col);
    }

    public static void drawObjBorder(ImVec2 objPos, ImVec2 objSize, float distanceThickness, int col) {
        ImVec2 windowPos = getWindowPos();
        ImVec2 vMin = new ImVec2(
                windowPos.x + objPos.x - distanceThickness,
                windowPos.y + objPos.y - distanceThickness
        );
        ImVec2 vMax = new ImVec2(
                vMin.x + objSize.x + (distanceThickness * 2f),
                vMin.y + objSize.y + (distanceThickness * 2f)
        );

        getWindowDrawList().addRect(vMin.x, vMin.y, vMax.x, vMax.y, col, 0f, 15, 0.01f);
    }
}
