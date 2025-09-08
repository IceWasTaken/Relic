package net.ice.talisman.gui.builder;

import imgui.*;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Input;
import net.ice.relic.core.gui.Gui;
import net.ice.talisman.gui.TextBox;
import net.ice.talisman.gui.builder.objs.Form;
import net.ice.talisman.gui.builder.objs.GuiOBJTypes;
import net.ice.talisman.gui.builder.registry.WinRegistry;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static imgui.ImGui.*;
import static imgui.extension.imguifiledialog.ImGuiFileDialog.*;
import static imgui.flag.ImGuiCol.COUNT;
import static imgui.flag.ImGuiColorEditFlags.*;
import static imgui.flag.ImGuiCond.Once;
import static imgui.flag.ImGuiWindowFlags.*;
import static imgui.flag.ImGuiWindowFlags.None;
import static imgui.internal.ImGui.calcItemSize;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static net.ice.relic.common.util.VectorUtil.vector2fToImVec2;
import static net.ice.talisman.gui.builder.GuiBuilderAdditional.*;
import static net.ice.talisman.gui.builder.GuiBuilderConfig.Color.*;
import static net.ice.talisman.gui.builder.GuiBuilderConfig.Controls.*;
import static net.ice.talisman.gui.builder.GuiBuilderConfig.WindowFlags.*;
import static net.ice.talisman.gui.builder.registry.WinRegistry.HKEY_CURRENT_USER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class GuiBuilder {

    private int id = -1;
    private int childID = -1;
    private int objID = -1;
    private int activeWindowID = 0;
    private int index = 0;
    private int family = 0;
    private int grandchild = -1;
    private int type = -1;
    private boolean myFormsActive = false;
    private boolean colorMenu = false;
    private boolean styleMenu = false;
    private boolean fontMenu = false;
    private String currentItem = "";
    private ImString name = new ImString("");
    private ImVec2 formPos = new ImVec2();
    private ImVec2 itemSize = new ImVec2();
    private ImVec2 posOBJ = new ImVec2();
    private ImVec2 oldPos = new ImVec2();
    private List<Form> forms = new ArrayList<>();
    private List<GuiBuilderClasses.BasicOBJ> objs = new ArrayList<>();
    private ImGuiStyle darkStyle = new ImGuiStyle();
    private ImGuiStyle customGUIStyle = new ImGuiStyle();
    private List<ImVec2> oldPosObjs = new ArrayList<>();

    private TextBox formTextBox = new TextBox();

    private GuiBuilderClasses.ResizeOptions resizeOption = GuiBuilderClasses.ResizeOptions.OFF;

    boolean	movingObj = false;
    boolean resizeObj = false;
    boolean noMove = false;
    int inResizeID = 0;
    long tickMove = 0;
    long tickResize = 0;

    private RelicApplication relicApplication;

    public GuiBuilder(RelicApplication relicApplication) {
        this.relicApplication = relicApplication;

        styleColorsDark(darkStyle);
        for (int i = 0; i < COUNT; i++ ) {
            customGUIStyle.getColors()[i] = darkStyle.getColors()[i];
        }

        forms.clear();
        objs.clear();
    }

    class MoveOBJ {
        int index = 0;
        ImVec2 pos = new ImVec2();
    }

    private GuiOBJTypes getNameType(int type)
    {
        return switch (type) {
            case 1 -> GuiOBJTypes.BUTTON;
            case 2 -> GuiOBJTypes.LABEL;
            case 3 -> GuiOBJTypes.EDIT;
            case 4 -> GuiOBJTypes.SLIDER_I;
            case 5 -> GuiOBJTypes.SLIDER_F;
            case 6 -> GuiOBJTypes.CHECKBOX;
            case 7 -> GuiOBJTypes.RADIO;
            case 8 -> GuiOBJTypes.TOGGLE;
            default -> GuiOBJTypes.NONE;
        };
    }

    private GuiBuilderClasses.ResizeOptions limitBorderingControl(ImVec2 objectPos, ImVec2 objectSize) {
       return limitBorderingControl(objectPos, objectSize, 3f);
    }

    private ImVec2 getRelativeCursorPos() {
        return new ImVec2(relicApplication.getInput().getMousePosition().x, flipY(relicApplication.getInput().getMousePosition().y));
    }

    private float flipY(float y) {
        return relicApplication.getWindow().getHeight() - y;
    }

    private GuiBuilderClasses.ResizeOptions limitBorderingControl(ImVec2 obj_pos, ImVec2 obj_size, float thickness) {
        var current_win_pos	= getWindowPos( );

        var control_win_pos	= new ImVec2(current_win_pos.x + obj_pos.x, current_win_pos.y + obj_pos.y);

        var pos	= getRelativeCursorPos();

        var top	= (pos.y >= (long)(control_win_pos.y - thickness) && pos.y <= (long)(control_win_pos.y));

        var bottom = (pos.y >= (long)(control_win_pos.y + obj_size.y) && pos.y <= (long)(control_win_pos.y + obj_size.y + thickness));

        var left = (pos.x >= (long)(control_win_pos.x - thickness) && pos.x <= (long)(control_win_pos.x));

        var right = (pos.x >= (long)(control_win_pos.x + obj_size.x) && pos.x <= (long)(control_win_pos.x + obj_size.x + thickness));

        if ((bottom && right) || (top && left))
        {
            return (bottom && right) ? GuiBuilderClasses.ResizeOptions.BOTTOM_RIGHT : GuiBuilderClasses.ResizeOptions.TOP_LEFT;
        }
        else
        if ((top && right) || (bottom && left))
        {
            return (top && right) ? GuiBuilderClasses.ResizeOptions.TOP_RIGHT : GuiBuilderClasses.ResizeOptions.BOTTOM_LEFT;
        }
        else
        {
            if (top || bottom)
                return (top) ? GuiBuilderClasses.ResizeOptions.TOP : GuiBuilderClasses.ResizeOptions.BOTTOM;
            else
            if (left || right)
                return (left) ? GuiBuilderClasses.ResizeOptions.LEFT : GuiBuilderClasses.ResizeOptions.RIGHT;

        }
        return GuiBuilderClasses.ResizeOptions.OFF;
    }

    private boolean isItemHovered(ImVec2 obj_pos, ImVec2 obj_size, float distance) {
        var current_win_pos	= getWindowPos();
        var control_win_pos	= new ImVec2(current_win_pos.x + ( obj_pos.x - distance ), current_win_pos.y + ( obj_pos.y - distance ) );
        var pos	= getRelativeCursorPos();
        return ( pos.y >= control_win_pos.y && pos.y <= ( control_win_pos.y + (obj_size.y  + ( distance * 2 ) ) ) && pos.x >= control_win_pos.x && pos.x <= ( control_win_pos.x + obj_size.x + ( distance * 2 ) ) );
    }

    private void saveConfigPath(String key, String value) {
        // Example using Properties file
        try {
            Properties props = new Properties();
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                try (FileInputStream in = new FileInputStream(configFile)) {
                    props.load(in);
                }
            }
            props.setProperty(key, value);
            try (FileOutputStream out = new FileOutputStream(configFile)) {
                props.store(out, "Saved paths for ImGui Builder");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawDialogsSaveOpen() {
        String[] dialogKeys = {
                "SaveProjectFileDlgKey",
                "OpenProjectFileDlgKey",
                "GenCodeProjectFileDlgKey",
                "SaveColorsDlgKey",
                "OpenColorsDlgKey",
                "SaveFlagsDlgKey",
                "OpenFlagsDlgKey"
        };

        for (int i = 0; i < dialogKeys.length; i++) {
            String key = dialogKeys[i];
            if (display(key, 32, 350.f, 300.f) && isOk()) {
                String fullFilePath = ImGuiFileDialog.getFilePathName();
                String fullPath = ImGuiFileDialog.getCurrentPath();

                // Save the path in config for all dialogs
                saveConfigPath("ImGuiBuilderPath", fullPath);

                switch (i) {
                    case 0:
                        if (saveControls(fullFilePath, forms, objs))
                            messageBoxA(null, "Project saved!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    case 1:
                        forms.clear();
                        objs.clear();
                        if (loadControls(fullFilePath, forms, objs, new int[]{id}))
                            messageBoxA(null, "Project loaded!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    case 2:
                        if (createCode(fullFilePath, forms, objs))
                            messageBoxA(null, "Code been generated!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    case 3:
                        if (saveColors(fullFilePath, customGUIStyle))
                            messageBoxA(null, "Colors saved!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    case 4:
                        if (loadColors(fullFilePath, customGUIStyle))
                            messageBoxA(null, "Colors loaded!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    case 5:
                        if (saveFlags(fullFilePath, customGUIStyle))
                            messageBoxA(null, "Flags saved!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    case 6:
                        if (loadFlags(fullFilePath, customGUIStyle))
                            messageBoxA(null, "Flags loaded!", "ImGui Builder", INFORMATION_MESSAGE);
                        break;
                    default:
                        break;
                }

                close();
            }
        }
    }

    private void messageBoxA(Component parent, String text, String caption, int type) {
        JOptionPane.showMessageDialog(parent, text, caption, type);
    }

    private String RegeditGetPath(String keyname) {
        try {
            return WinRegistry.readString(HKEY_CURRENT_USER, keyname, keyname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void formWindowFlag()
    {
        begin("Style Window Editor", new ImBoolean(styleMenu));
        if ( button( "Export" ) )
        {
            flagsToClipboard( customGUIStyle );
            messageBoxA(null, "Code exported to clipboard !", "ImGui Builder", INFORMATION_MESSAGE);
        }

        sameLine( );

        if (button("Load"))
        {
            openDialog("OpenFlagsDlgKey", "Open File", ".flags", RegeditGetPath("ImGuiBuilderPath"), "style_flags" );
        }

        sameLine();
        if (button("Save"))
        {
            openDialog("SaveFlagsDlgKey", "Save File", ".flags", RegeditGetPath("ImGuiBuilderPath"), "style_flags" );
        }

        text("First");
        if (sliderFloat( "FrameRounding", new float[]{customGUIStyle.getFrameRounding()}, 0.0f, 12.0f, "%.0f" )) {
            customGUIStyle.setGrabRounding(customGUIStyle.getFrameRounding());
        }
        {
            boolean border = (customGUIStyle.getWindowBorderSize() > 0.0f);
            if (checkbox("WindowBorder", border)) {
                customGUIStyle.setWindowBorderSize(border ? 1.0f : 0.0f);
            }
        }
        sameLine( );
        {
            boolean border = (customGUIStyle.getFrameBorderSize() > 0.0f);
            if (checkbox( "FrameBorder", border)) {
                customGUIStyle.setFrameBorderSize(border ? 1.0f : 0.0f);
            }
        }
        sameLine( );
        {
            boolean border = (customGUIStyle.getPopupBorderSize() > 0.0f);
            if (checkbox( "PopupBorder", border)) {
                customGUIStyle.setPopupBorderSize(border ? 1.0f : 0.0f);
            }
        }

        text("Main");
        sliderFloat2( "WindowPadding",  new float[]{customGUIStyle.getWindowPadding().x, customGUIStyle.getWindowPadding().y}, 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "FramePadding", new float[]{customGUIStyle.getFramePadding().x, customGUIStyle.getFramePadding().y}, 0.0f, 20.0f, "%.0f");
        sliderFloat2( "CellPadding", new float[]{customGUIStyle.getCellPadding().x, customGUIStyle.getCellPadding().y}, 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "ItemSpacing", new float[]{customGUIStyle.getItemSpacing().x, customGUIStyle.getItemSpacing().y}, 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "ItemInnerSpacing", new float[]{customGUIStyle.getItemInnerSpacing().x, customGUIStyle.getItemInnerSpacing().y}, 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "TouchExtraPadding", new float[]{customGUIStyle.getTouchExtraPadding().x, customGUIStyle.getTouchExtraPadding().y}, 0.0f, 10.0f, "%.0f" );
        sliderFloat( "IndentSpacing", new float[]{customGUIStyle.getIndentSpacing()}, 0.0f, 30.0f, "%.0f" );
        sliderFloat( "ScrollbarSize", new float[]{customGUIStyle.getScrollbarSize()}, 1.0f, 20.0f, "%.0f" );
        sliderFloat( "GrabMinSize",	new float[]{customGUIStyle.getGrabMinSize()}, 1.0f, 20.0f, "%.0f" );
        text("Borders");
        sliderFloat("WindowBorderSize", new float[]{customGUIStyle.getWindowBorderSize()}, 0.0f, 1.0f, "%.0f" );
        sliderFloat( "ChildBorderSize", new float[]{customGUIStyle.getChildBorderSize()}, 0.0f, 1.0f, "%.0f" );
        sliderFloat( "PopupBorderSize", new float[]{customGUIStyle.getPopupBorderSize()}, 0.0f, 1.0f, "%.0f" );
        sliderFloat( "FrameBorderSize", new float[]{customGUIStyle.getFrameBorderSize()},  0.0f, 1.0f, "%.0f" );
        sliderFloat( "TabBorderSize", new float[]{customGUIStyle.getTabBorderSize()}, 0.0f, 1.0f, "%.0f" );
        text("Rounding");
        sliderFloat("WindowRounding", new float[]{customGUIStyle.getWindowRounding()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("ChildRounding", new float[]{customGUIStyle.getChildRounding()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("FrameRounding", new float[]{customGUIStyle.getFrameRounding()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("PopupRounding", new float[]{customGUIStyle.getPopupRounding()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("ScrollbarRounding", new float[]{customGUIStyle.getScrollbarRounding()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("GrabRounding", new float[]{customGUIStyle.getGrabRounding()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("LogSliderDeadzone", new float[]{customGUIStyle.getLogSliderDeadzone()}, 0.0f, 12.0f, "%.0f" );
        sliderFloat("TabRounding",	new float[]{customGUIStyle.getTabRounding()}, 0.0f, 12.0f, "%.0f" );
        text("Alignment");
        sliderFloat2( "WindowTitleAlign", new float[]{customGUIStyle.getWindowTitleAlign().x, customGUIStyle.getWindowTitleAlign().y}, 0.0f, 1.0f, "%.2f" );
        var windowMenuButtonPosition = customGUIStyle.getWindowMenuButtonPosition() + 1;
        if (combo( "WindowMenuButtonPosition", new ImInt(windowMenuButtonPosition), "None\0Left\0Right\0" ) ) {
            customGUIStyle.setWindowMenuButtonPosition(windowMenuButtonPosition - 1);
        }
        combo( "ColorButtonPosition", new ImInt(customGUIStyle.getColorButtonPosition()), "Left\0Right\0" );
        sliderFloat2( "ButtonTextAlign", new float[]{customGUIStyle.getButtonTextAlign().x, customGUIStyle.getButtonTextAlign().y}, 0.0f, 1.0f, "%.2f");
        sliderFloat2( "SelectableTextAlign", new float[]{customGUIStyle.getSelectableTextAlign().x, customGUIStyle.getSelectableTextAlign().y}, 0.0f, 1.0f, "%.2f");
        text( "Safe Area Padding" );
        sliderFloat2("DisplaySafeAreaPadding", new float[]{customGUIStyle.getDisplaySafeAreaPaddingX(), customGUIStyle.getDisplaySafeAreaPaddingY()}, 0.0f, 30.0f, "%.0f" );

        end( );
    }

    private void formColorEditor( )
    {
        ImGuiStyle style = getStyle();

        setNextWindowSize(400.f, 500.f, Once);

        begin("Gui Builder color export/import ", new ImBoolean(colorMenu));

        if ( button( "Export" ) )
        {
            colorsToClipboard(customGUIStyle);
            messageBoxA(null, "Code exported to clipboard !", "ImGui Builder", INFORMATION_MESSAGE);
        }

        sameLine();

        if ( button( "Load" ) )
        {
            openDialog( "OpenColorsDlgKey", "Open File", ".colors", RegeditGetPath( "ImGuiBuilderPath" ), "style_colors" );
        }

        sameLine();
        if ( button( "Save" ) )
        {
            openDialog( "SaveColorsDlgKey", "Save File", ".colors", RegeditGetPath( "ImGuiBuilderPath" ), "style_colors" );
        }


        ImGuiTextFilter filter = new ImGuiTextFilter();
        filter.draw("Filter colors", getFontSize() * 16);

        int alpha_flags = 0;
        if ( radioButton( "Opaque", alpha_flags == None)) alpha_flags = None;


        sameLine();
        if ( radioButton( "Alpha", alpha_flags == AlphaPreview)) alpha_flags = AlphaPreview;


        sameLine();
        if ( radioButton( "Both", alpha_flags == AlphaPreviewHalf)) alpha_flags = AlphaPreviewHalf;


        beginChild( "##colors", new ImVec2(0, 0), true, AlwaysVerticalScrollbar | AlwaysHorizontalScrollbar | NavFlattened );

        pushItemWidth( -160 );
        for ( var i = 0; i < COUNT; i++ ) {
		    String name = getStyleColorName(i);

            if (!filter.passFilter(name)) {
                continue;
            }
            pushID( i );
            colorEdit4("##color", new float[]{customGUIStyle.getColors()[i].x, customGUIStyle.getColors()[i].y, customGUIStyle.getColors()[i].z, customGUIStyle.getColors()[i].w}, AlphaBar | alpha_flags );
            if (!customGUIStyle.getColors()[i].equals(getSavedColors()[i])) {
                sameLine(0.0f, style.getItemInnerSpacing().x);
                if (button( "Save")) {
                    getSavedColors()[i] = customGUIStyle.getColors()[i];
                }
                sameLine(0.0f, style.getItemInnerSpacing().x);
                if (button( "Revert" )) {
                    customGUIStyle.getColors()[i] = getSavedColors()[i];
                }
            }
            sameLine( 0.0f, style.getItemInnerSpacing().x );
            textUnformatted(name);
            popID( );
        }
        popItemWidth( );
        endChild( );

        end( );
    }




    void formFontEditor( )
    {
        begin( "Font Editor", new ImBoolean(fontMenu));

        if (button("Import font from file")) {

        }

        text("Current font: " + "123");

        end();
    }

    public void draw() {
        pushAllColorsDark(darkStyle);
        int width = 1280;

        drawDialogsSaveOpen();

        if (colorMenu)
            formColorEditor();

        if (styleMenu)
            formWindowFlag();

        if (fontMenu)
            formFontEditor();

        pasteOBJ();

        setNextWindowSize(( width - 16 ), 100);
        setNextWindowPos(0, 0);
        begin("BUILDER", null, NoBringToFrontOnFocus | MenuBar);
        myFormsActive = isWindowFocused();
        if (beginMenuBar()) {
            if (beginMenu( "Project")){
                if (menuItem("Save")) {
                    openDialog( "SaveProjectFileDlgKey", "Save File", ".builder", RegeditGetPath( "ImGuiBuilderPath" ), "project" );
                }

                if (menuItem( "Open")) {
                    openDialog( "OpenProjectFileDlgKey", "Open File", ".builder", RegeditGetPath( "ImGuiBuilderPath" ), "project" );
                }

                if (menuItem( "Generate Code")) {
                    openDialog( "GenCodeProjectFileDlgKey", "Open File", ".cpp,.h,.hpp", RegeditGetPath( "ImGuiBuilderPath" ), "imgui_builder" );
                }

                endMenu();
            }
            if (beginMenu( "Editor" )) {
                if (menuItem("Color")) {
                    colorMenu = !colorMenu;
                }

                if (menuItem("Style")) {
                    styleMenu = !styleMenu;
                }

                if (menuItem("Font")) {
                    fontMenu = !fontMenu;
                }
                endMenu();
            }
            endMenuBar();
        }

        if (button("New Form")) {
            createForm();
        }
        sameLine();
        if (button("New Child")) {
            createChild();
        }
        sameLine();
        if (button("New Button")) {
            createObject(1);
        }
        sameLine();
        if (button("New Label")) {
            createObject(2);
        }
        sameLine();
        if (button("New Text")) {
            createObject(3);
        }
        sameLine();
        if ( button( "New Slider Int" ) ) {
            createObject( 4 );
        }
        sameLine();
        if (button("New Slider float")) {
            createObject(5);
        }
        sameLine();
        if (button("New CheckBox")) {
            createObject(6);
        }
        sameLine();
        if (button("New radio")) {
            createObject(7);
        }
        sameLine();
        if (button("New toggle")) {
            createObject(8);
        }

        objectProperty();
        popAllColorsCustom();
        pushAllColorsCustom(customGUIStyle);
        showForm();
        popAllColorsCustom();
        end();
    }

    private void createForm() {
        id++;
        Form frm = new Form();
        frm.setID(id);
        frm.setName("form" + id);
        frm.setSize(50.f, 50.f);
        forms.addLast(frm);
    }

    void createChild() {
        if (forms.isEmpty()) {
            return;
        }

        childID = forms.get(id).getChildren().size();
        GuiBuilderClasses.Child child = new GuiBuilderClasses.Child();
        child.id = childID;
        child.name = "child" + childID;
        child.father = id;
        child.border = true;
        child.size = new ImVec2(50,50);
        child.pos = new ImVec2(15,15);
        forms.get(id).getChildren().addLast(child);
    }

    private int childID() {
        for (GuiBuilderClasses.Child child : forms.get(activeWindowID).getChildren()) {
            if (child.selected) {
                return child.id;
            }
        }
        return -1;
    }

    private void createObject(int type) {
        GuiBuilderClasses.BasicOBJ newObj = new GuiBuilderClasses.BasicOBJ();
        newObj.id = objID;
        newObj.form = activeWindowID;
        newObj.child = childID();
        newObj.name = name;
        newObj.myType = type;
        newObj.size = new ImVec2();
        newObj.pos = new ImVec2(30, 30);
        objs.add(newObj);
    }

    private boolean isNumber(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void pasteOBJ()
    {
        if (myFormsActive) return;
        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyDown(ImGuiKey.V)) {
            String clipboardText = ImGui.getClipboardText();
            String[] splitClipboard = clipboardText.split("\n");
            ImVec2 pos = new ImVec2(30,30);
            for (String text : splitClipboard)
            {
                String[] o = text.split(",");

                if (o.length != 7)
                    return;

                for (String tx : o) {
                    if (isNumber(tx));
                    return;
                }

                String name = getNameType(Integer.parseInt(o[0])).getAsString();

                if (Integer.parseInt(o[0]) == 10) {
                    int mChildId = forms.get(activeWindowID).getChildren().size();

                    GuiBuilderClasses.Child newChild = new GuiBuilderClasses.Child();
                    newChild.id = mChildId;
                    newChild.name = "child" + mChildId;
                    newChild.father = activeWindowID;
                    newChild.selected = Integer.parseInt(o[4]) != 0;
                    newChild.pos = new ImVec2(java.lang.Float.parseFloat(o[2]), java.lang.Float.parseFloat(o[3]));
                    newChild.size = new ImVec2(java.lang.Float.parseFloat(o[4]), java.lang.Float.parseFloat(o[5]));

                    forms.get(activeWindowID).getChildren().add(newChild);
                    System.out.println("child obj\n");
                }
			else if (!name.isEmpty()) {
                objID++;
                name += objID ;
                GuiBuilderClasses.BasicOBJ newOBJ = new GuiBuilderClasses.BasicOBJ();
                newOBJ.id = objID;
                newOBJ.form = activeWindowID;
                newOBJ.child = Integer.parseInt(o[1]);
                newOBJ.name = new ImString(name);
                newOBJ.myType = Integer.parseInt(o[0]);
                newOBJ.size = new ImVec2(java.lang.Float.parseFloat(o[3]), java.lang.Float.parseFloat(o[4]));
                newOBJ.pos = new ImVec2(java.lang.Float.parseFloat(o[5]), java.lang.Float.parseFloat(o[6]));

                objs.addLast(newOBJ);
                System.out.println("paste obj\n");
            }
                pos.x += 15;
                pos.y += 15;
            }
        }
    }

    private void copyOBJ(int type, int child, ImVec2 size, ImVec2 pos, boolean border, boolean selected, boolean pass_key_check ) {
        if ((!myFormsActive && ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyDown(ImGuiKey.V) || pass_key_check)) {
            String buffer = "";
            logToClipboard();
            if ( !selected ) {
                if (type == 10) {
                    buffer = type + ",0," + size.x + "," + size.y + "," + pos.x + "," + pos.y + "," + border;
                } else {
                    buffer = type + "," + child + ",0," + size.x + "," + size.y + "," + pos.x + "," + pos.y;
                }
            } else {
                for (GuiBuilderClasses.BasicOBJ copy : objs)
                {
                    if (copy.selected)
                    {
                        buffer += copy.myType + "," + copy.child + ",0," + copy.size.x + "," + copy.size.y + ", " + copy.pos.x + ", " + copy.pos.y + "\n";
                    }
                }
            }
            logText(buffer);
            logFinish();
            System.out.println("copy that " + buffer);
        }
    }

    private void showForm() {
        for (Form form : forms) {
            setNextWindowSize(vector2fToImVec2(form.getSize()));

            if (form.shouldDelete())
            {
                deleteForm(form.getID());
                break;
            }
            begin(form.getName(), null, movingObj ? (NoCollapse | NoMove) : (NoCollapse));

            form.setPos(getWindowPos());
            form.setSize(getWindowSize());

            if (isWindowHovered() && isMouseDoubleClicked(0))
            {
                name = new ImString(form.getName());
                currentItem = form.getName() + ":" + form.getID();
                family = form.getID();
                type = 0;
            }
            myFormsActive = !isWindowFocused();
            if ( isWindowFocused( ) || isWindowAppearing( ) || isWindowHovered( ) )
            {
                activeWindowID = form.getID();
            }

            for (GuiBuilderClasses.BasicOBJ obj : objs) {
                if ( obj.form == form.getID() && obj.child < 0 ) {
                    renderOBJ(obj, form.getID());
                }
            }
            for (GuiBuilderClasses.Child container : form.getChildren()) {
                setCursorPos(container.pos);

                if (container.deleteMe) {
                    for (GuiBuilderClasses.BasicOBJ obj : objs)
                    {
                        if ( obj.child == container.id )
                        {
                            obj.myType = -1;
                            obj.deleteMe = true;
                            obj.child = -1;
                        }
                    }

                    form.getChildren().remove(container.id);
                    childID = form.getChildren().size( ) - 1;

                    for (int newID = container.id - 1; newID < form.getChildren().size( ); ++newID ) {
                        form.getChildren().get(newID).name = ("child" + newID);
                        form.getChildren().get(newID).id = newID;
                    }
                    break;
                }

                boolean hover = false;

                var normal_select = (currentItem == ( container.name + ":" + container.id ));

                beginChild( container.name, container.size, container.border );

                boolean scrollEnableY = getScrollMaxY( ) > 0.f;
                float scrollPosY = getScrollY();

                for (GuiBuilderClasses.BasicOBJ obj : objs) {
                    if ( obj.form == form.getID() && obj.child == container.id ) {
                        renderOBJ( obj, form.getID());

                        ImVec2 oldPos = obj.pos;

                        if (scrollEnableY) {
                            obj.pos.y -= scrollPosY;
                        }

                        if ( isItemHovered( obj.pos, obj.size, 5.f ) )
                            hover = true;

                        obj.pos = oldPos;
                    }
                }
                endChild( );

                // check if selected
                // container.hover = isItemHovered( );
                container.hover = isItemHovered(container.pos, container.size, 5.f) && !hover;
                //if ( container.hover && limit_bordering_control( container.pos, container.size, -15.f ) != resize_opt::off ) //I don't know if you were good with that
                //	container.hover = false;

                boolean left_clicked = isMouseClicked( 0, false );
                boolean right_clicked = isMouseClicked( 1, false );
                boolean show_context = normal_select;

                // thats is shame but... work good....
                if ( container.hover && ( left_clicked || right_clicked ) && show_context == false )
                {
                    show_context = !left_clicked;
                    if (ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                        container.selected = !container.selected;
                    } else {
                        for (GuiBuilderClasses.BasicOBJ basicOBJ : objs) {
                            basicOBJ.selected = false;
                        }
                    }

                    currentItem	= container.name + ':' + form.getID();
                    family = container.father;
                    index = container.id;
                    type = 10;
                }
                if (container.hover) {
                    //SetCursor( this->cursor.m_arrow_all );
                }

                container.selected = (Objects.equals(currentItem, container.name + ":" + form.getID()));
                if (container.selected && form.getID() == activeWindowID) {
                    drawObjBorder(container.pos, container.size);
                }
                if ( !container.locked )
                    resizeOBJ(container.pos, container.size, container.hover, container.selected);

                pushAllColorsDark(darkStyle);

                if (show_context && beginPopupContextItem("##obj_context")) {
                    // Backup style values
                    float backupBtnTextAlignY = ImGui.getStyle().getButtonTextAlign().y;
                    float backupFramePaddingY = ImGui.getStyle().getFramePadding().y;

                    // Modify style temporarily
                    ImGui.getStyle().setFramePadding(ImGui.getStyle().getFramePaddingX(), -1.3f);
                    ImGui.getStyle().setButtonTextAlign(ImGui.getStyle().getButtonTextAlignX(), 0f);

                    ImVec2 btnSize = new ImVec2(60f, 12f);

                    if (button("delete", btnSize)) {
                        container.deleteMe = true;
                        currentItem = "";
                        type = -1;
                    }

//                    if (button("copy", btnSize)) {
//                        copyOBJ(obj.myType, obj.child, obj.size, obj.pos, false, false, true);
//                    }

                    checkbox("lock", container.locked);

                    // Restore previous style
                    ImGui.getStyle().setFramePadding(ImGui.getStyle().getFramePaddingX(), backupFramePaddingY);
                    ImGui.getStyle().setButtonTextAlign(ImGui.getStyle().getButtonTextAlignX(), backupBtnTextAlignY);

                    endPopup();
                }

                popAllColorsCustom();
            }
            end( );
        }
    }

    private void deleteForm(int formID)
    {
        forms.remove(formID);
        id = forms.size( ) - 1;
        System.out.println("size id " + id + "\n");

        if (!objs.isEmpty() && !forms.isEmpty()) {
            for (int i = objs.size( ) - 1; i > -1; --i )
            {
                System.out.println(i + "\n");
                if (objs.get(i).form == formID)
                {
                    System.out.println("Obj id: " + objs.get(i).id + " \t form id: " + objs.get(i).form + "\n");
                    objs.remove(i);
                    objID = objs.size() - 1;

                    for (int x = i - 1; x < objs.size(); x++)
                        objs.get(x).id = x;
                }
            }

            System.out.println("delete file finish\n");
        }
        else
        {
            objs.clear();
            objID = -1;
            System.out.println("delete all\n");
        }


        for (int id1 = ( formID - 1 ); id >= id1; ++id )
        {
            if ( id1 < 0 )
                continue;

            for (GuiBuilderClasses.BasicOBJ obj : objs) {
                if (obj.form == formID)
                    obj.deleteMe = true;

                if (obj.form == forms.get(id1).getID()) {
                    obj.form = id1;
                }
            }
            forms.get(id).setID(id1);
        }
    }

    void resizeOBJ(GuiBuilderClasses.BasicOBJ currentObj, boolean selected)
    {
        if (currentObj.locked) return;
        resizeOBJ(currentObj.pos, currentObj.size, currentObj.hover, selected);
        currentObj.size = currentObj.sizeObj;
    }

    void resizeOBJ(ImVec2 objectPos, ImVec2 objSize, boolean hover, boolean selected)
    {
        GuiBuilderClasses.ResizeOptions resizeOption = GuiBuilderClasses.ResizeOptions.OFF;

        boolean scrollEnableY = getScrollMaxY() > 0.f;
        float scrollPosY = getScrollY();

        if (hover && tickResize == 0) {
            ImVec2 oldPos = objectPos;

            if ( scrollEnableY )
            {
                objectPos.y -= scrollPosY;
            }

            resizeOption = limitBorderingControl(objectPos, objSize, 3.f);

            objectPos = oldPos;

            switch (resizeOption)
            {
                case BOTTOM_RIGHT:
                case TOP_LEFT:
                    relicApplication.getWindow().setCursorShapeNWSE();
                    break;
                case TOP_RIGHT:
                case BOTTOM_LEFT:
                    relicApplication.getWindow().setCursorShapeNESW();
                    break;
                case TOP:
                case BOTTOM:
                    relicApplication.getWindow().setCursorShapeHorizontal();
                    break;
                case LEFT:
                case RIGHT:
                    relicApplication.getWindow().setCursorShapeVertical();
                    break;
                default:
                    break;
            }

//            if (resizeOption != GuiBuilderClasses.ResizeOptions.OFF)
//            {
//                SetCursor( this->cursor.m_current_icon );
//            }
        }

        noMove = (tickResize != 0) || (resizeOption != GuiBuilderClasses.ResizeOptions.OFF);

        //printf( "pos { %.f, %.f }, size { %.f, %.f }, hover %d, selected %d, moving %d\n", obj_pos.x, obj_pos.y, obj_size.x, obj_size.y, hover, selected, g_moving_obj );
        if (movingObj || !relicApplication.getInput().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_LEFT)) {
            resizeObj = false;
            tickResize = 0;
            resizeOption = GuiBuilderClasses.ResizeOptions.OFF;
            //m_in_resize_id	= 0;
            return;

        }

        if ( !selected  ) return;
        ImVec2 currentPos	= getRelativeCursorPos();
        long tick_now		= getTickCount();

        if (tickResize == 0 && resizeOption != GuiBuilderClasses.ResizeOptions.OFF ) {
            resizeOption = resizeOption;
            tickResize	= tick_now + 80;
            //m_in_resize_id	= obj.id + obj.my_type;
            return;
        }


        //printf( "pos { %.f, %.f }, size { %.f, %.f }, hover %d, selected %d\n", obj_pos.x, obj_pos.y, obj_size.x, obj_size.y, hover, selected );

        if (tickResize != 0 && tick_now > tickResize )
        {
            resizeObj = true;
            ImVec2 currentWinPos = getWindowPos();
            //SetCursor( this->cursor.m_current_icon );

            BiFunction<Float, Float, Float> normalizeDiff = (diff, val) -> {
                if (diff > val) {
                    diff = val;
                } else if (diff < -val) {
                    diff = -val;
                }
                return diff;
            };

            Function<Float, Float> normalizeDiff100 = diff -> normalizeDiff.apply(diff, 100f);


            switch (resizeOption)
            {
                case BOTTOM_RIGHT:
                {
                    basicMargins(GuiBuilderClasses.ResizeOptions.BOTTOM, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    basicMargins(GuiBuilderClasses.ResizeOptions.RIGHT, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    break;
                }
                case TOP_LEFT:
                {
                    basicMargins(GuiBuilderClasses.ResizeOptions.TOP, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    basicMargins(GuiBuilderClasses.ResizeOptions.LEFT, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    break;
                }
                case TOP_RIGHT:
                {
                    basicMargins(GuiBuilderClasses.ResizeOptions.TOP, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    basicMargins(GuiBuilderClasses.ResizeOptions.RIGHT, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    break;
                }
                case BOTTOM_LEFT:
                {
                    basicMargins(GuiBuilderClasses.ResizeOptions.BOTTOM, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    basicMargins(GuiBuilderClasses.ResizeOptions.LEFT, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    break;
                }
                default:
                    basicMargins(resizeOption, objectPos, objSize, currentWinPos, currentPos, scrollPosY, normalizeDiff100);
                    break;
            }
        }

    }

    private void basicMargins(GuiBuilderClasses.ResizeOptions rsOpt, ImVec2 objectPos, ImVec2 objSize, ImVec2 currentWinPos, ImVec2 currentPos, float scrollPosY, Function<Float, Float> normalizeDiff) {
        switch (rsOpt) {
            case RIGHT -> {
                float endPosX = currentWinPos.x + objectPos.x + objSize.x;
                float dif = normalizeDiff.apply(currentPos.x - endPosX);
                objSize.x += dif;
            }
            case LEFT -> {
                float endPosX = currentWinPos.x + objectPos.x;
                float dif = normalizeDiff.apply(endPosX - currentPos.x);
                objectPos.x -= dif;
                objSize.x += dif;
            }
            case TOP -> {
                float endPosY = (currentWinPos.y + objectPos.y) - scrollPosY;
                float dif = normalizeDiff.apply(endPosY - currentPos.y);
                objectPos.y -= dif;
                objSize.y += dif;
            }
            case BOTTOM -> {
                float endPosY = currentWinPos.y + objectPos.y + objSize.y;
                float dif = normalizeDiff.apply(currentPos.y - endPosY);
                objSize.y += dif;
            }
            default -> {
                // do nothing
            }
        }
    }

    private void renderOBJ(GuiBuilderClasses.BasicOBJ obj, int currentFormID) {
        // Set position for next object render
        setCursorPos(obj.pos);

        // If object is marked for deletion
        if (obj.deleteMe) {
            if (obj.id < objID) {
                objs.remove(obj.id);
                objID = objs.size() - 1;
            }

            // Reassign IDs to remaining objects
            for (int newID = obj.id; newID < objs.size(); newID++) {
                objs.get(newID).id = newID;
            }
            return; // Skip rendering this object
        }

        String buffer = "text here";
        int value_i = 0;
        float value_f = 0;
        boolean true_bool = false;
        boolean normalSelect = currentItem.equals(obj.name + ":" + obj.id);

        Function<GuiBuilderClasses.BasicOBJ, Float> relativeForResize = (o) -> {
            ImGuiStyle style = ImGui.getStyle();
            ImVec2 labelSize = calcTextSize(o.name.get(), true);
            ImVec2 frameSize = calcItemSize(new ImVec2(0, 0), calcItemWidth(),
                    labelSize.y + style.getFramePadding().y * 2.0f);
            float labelDif = (labelSize.x > 0.0f ? style.getItemInnerSpacing().x + labelSize.x : 0.0f);
            if (o.size.x == 0 && o.size.y == 0) {
                o.size = new ImVec2(frameSize.x + labelDif, frameSize.y);
            }
            return o.size.x - labelDif;
        };

        // Render based on type
        switch (obj.myType) {
            case 1 -> button(obj.name.get(), obj.size);
            case 2 -> text(obj.name.get());
            case 3 -> {
                pushItemWidth(relativeForResize.apply(obj));
                inputText(obj.name.get(), new ImString(buffer), 254);
                popItemWidth();
            }
            case 4 -> {
                pushItemWidth(relativeForResize.apply(obj));
                sliderInt(obj.name.get(), new int[]{value_i}, 0, 100); // Wrap in array for reference
                popItemWidth();
            }
            case 5 -> {
                pushItemWidth(relativeForResize.apply(obj));
                sliderFloat(obj.name.get(), new float[]{value_f}, 0f, 100f); // Wrap in array for reference
                popItemWidth();
            }
            case 6 -> checkbox(obj.name.get(), true_bool);
            case 7 -> radioButton(obj.name.get(), true_bool);
            case 8 -> toggleButton(obj.name.get(), new boolean[]{true_bool});
            default -> {}
        }

        obj.size = getItemRectSize();

        boolean scrollEnableY = getScrollMaxY() > 0f;
        float scrollPosY = getScrollY();
        ImVec2 oldPos = new ImVec2(obj.pos.x, obj.pos.y);

        if (scrollEnableY) {
            obj.pos.y -= scrollPosY;
        }

        if ((obj.selected || normalSelect) && currentFormID == activeWindowID) {
            drawObjBorder(obj);
        }

        obj.hover = isItemHovered(obj.pos, obj.size, 5f);

        boolean leftClicked = isMouseClicked(0, false);
        boolean rightClicked = isMouseClicked(1, false);
        boolean showContext = normalSelect;

        if (obj.hover && (leftClicked || rightClicked) && !showContext) {
            showContext = !leftClicked;
            if (isKeyDown(ImGuiKey.LeftCtrl)) {
                obj.selected = !obj.selected;
            } else {
                for (GuiBuilderClasses.BasicOBJ o : objs) o.selected = false;
            }

            currentItem = obj.name.get() + ':' + obj.id;
            family = obj.form;
            grandchild = obj.child;
            index = obj.id;
            type = obj.myType;
        }

        pushAllColorsDark(darkStyle);

        if (showContext && beginPopupContextItem("##obj_context")) {

            var backup1_y = ImGui.getStyle().getButtonTextAlign().y;
            var backup2_y = ImGui.getStyle().getFramePadding().y;
            getStyle().setFramePadding(getStyle().getFramePaddingX(), -1.3f);
            getStyle().setButtonTextAlign(getStyle().getButtonTextAlignX(), 0);
            ImVec2 btnSize = new ImVec2(60f, 12f);

            if (button("delete", btnSize)) {
                obj.deleteMe = true;
                currentItem = "";
                type = -1;
            }
            if (button("copy", btnSize)) {
                copyOBJ(obj.myType, obj.child, obj.size, obj.pos, false, false, true);
            }
            checkbox("lock", obj.locked);
            getStyle().setFramePadding(getStyle().getFramePaddingX(), backup1_y);
            getStyle().setButtonTextAlign(getStyle().getButtonTextAlignX(), backup1_y);
            endPopup();
        }

        popAllColorsCustom();

        if (obj.hover) {
            //SetCursor(this.cursor.m_arrow_all);
        }

        obj.pos = oldPos;
        resizeOBJ(obj, normalSelect);
    }

    void objectProperty() {
        List<MoveOBJ> moveOBJS = new ArrayList<>();
        setNextWindowPos(0, 100);
        setNextWindowSize(300, 700 - 100);
        begin( "property", null, NoBringToFrontOnFocus);
        myFormsActive = isWindowFocused();
        if (beginCombo("##items", currentItem)) {
            // list all obj render in array child and form

            for (Form form : forms)
            {
                if (form.shouldDelete()) {
                    break;
                }

                String item = form.getName() + ":" + form.getID();
			    boolean is_selected = (Objects.equals(currentItem, item));

                if (selectable(item, is_selected )) {
                    name = new ImString(form.getName());
                    currentItem = item;
                    type = 0;
                    family = form.getID();
                }

                for (GuiBuilderClasses.Child child : form.getChildren()) {
                    item = child.name + ":" + form.getID();
                    if ( selectable(item, is_selected ) )
                    {
                        family = form.getID();
                        index = child.id;
                        type = 10;
                        currentItem	= item;
                    }
                }
                item = "";

                if ( is_selected )
                    setItemDefaultFocus( );
            }

            for (GuiBuilderClasses.BasicOBJ obj : objs)
            {
                if (obj.deleteMe) {
                    break;
                }

                String item = obj.name + ":" + obj.id;
			    boolean is_selected = (Objects.equals(currentItem, item));

                item = obj.name + ":" + obj.id;
                if (selectable(item, is_selected)) {
                    family = obj.form;
                    grandchild = obj.child;
                    index = obj.id;
                    type = obj.myType;
                    currentItem	= item;
                }

                if ( is_selected )
                    setItemDefaultFocus( );
            }

            endCombo( );
        }

        GuiBuilderClasses.Child child = new GuiBuilderClasses.Child();
        GuiBuilderClasses.BasicOBJ obj = new GuiBuilderClasses.BasicOBJ();
        Form form = new Form();

        switch (type) {
            case -1 -> {
                break;
            }
            case 0 -> {
                form = forms.get(family);
                inputInt("ID", new ImInt(form.getID()), 0);

                formTextBox.draw("Name form");

                //inputText("name form", new ImString(name));

                if (button("Apply name")) {
                    if (!formTextBox.getString().isEmpty()) {
                        form.setName(formTextBox.getString());
                    }
                }
                inputFloat("SizeX", new ImFloat(form.getSize().x), 1);
                inputFloat("SizeY", new ImFloat(form.getSize().y), 1);
                inputFloat("PosX", new ImFloat(form.getPos().x), 1);
                inputFloat("PosY", new ImFloat(form.getPos().y), 1);

                if (button("DELETE") || ImGui.isKeyDown(ImGuiKey.Delete)) {
                    form.setShouldDelete(true);
                    currentItem = "";
                    type = -1;
                }

                forms.set(family, form);
                break;
            }

            case 10 -> {
                child = forms.get(family).getChildren().get(index);
                formPos = vector2fToImVec2(forms.get(family).getPos());
                inputInt("ID", new ImInt(child.id), 0);

                if (inputInt( "Form Father", new ImInt(child.father), 1)) {
                    forms.get(child.father).getChildren().addLast(child);
                    child.deleteMe	= true;
                    currentItem	= "";
                    type			= -1;
                }

                inputFloat( "SizeX", new ImFloat(child.size.x), 1);
                inputFloat( "SizeY", new ImFloat(child.size.y), 1);
                inputFloat( "PosX", new ImFloat(child.pos.x), 1);
                inputFloat( "PosY", new ImFloat(child.pos.y), 1);
                checkbox( "Border", child.border );
                sameLine( );
                checkbox( "Lock", child.locked );
                itemSize = child.size;
                if (child.hover && !child.locked )
                    child.changePos = true;

                if (!noMove ) {
                    moveItem(child.pos, new AtomicBoolean(child.changePos));
                }

                copyOBJ(10, 0, child.size, child.pos, child.border, obj.selected, false);

                if (button( "DELETE") || ImGui.isKeyDown(ImGuiKey.Delete))
                {
                    child.deleteMe	= true;
                    currentItem	= "";
                    type = -1;
                }

                forms.get(family).getChildren().set(index, child);
                break;
            }

            default -> {
                if (grandchild > -1 )
                {
                    obj = objs.get(index);
                    formPos	= forms.get(family).getChildren().get(grandchild).pos;
                    formPos.x += forms.get(family).getPos().x;
                    formPos.y += forms.get(family).getPos().y;
                }
                else
                {
                    if (objs.size() > index) {
                        obj = objs.get(index);
                        formPos = vector2fToImVec2(forms.get(family).getPos());
                    }
                }

                itemSize = obj.sizeObj;
                inputInt("ID", new ImInt(obj.id), 0);
                inputInt("Form Father", new ImInt(obj.form), 1);
                inputInt( "Child Father", new ImInt(obj.child), 1);
                inputText( "Name", new ImString(obj.name), 0);
                //inputText("Name", name, 255);
                inputFloat( "PosX", new ImFloat(obj.pos.x), 1, 1 );
                inputFloat( "PosY", new ImFloat(obj.pos.y), 1, 1 );
                inputFloat( "SizeX", new ImFloat(obj.size.x), 1, 1);
                inputFloat( "SizeY", new ImFloat(obj.size.y), 1, 1);
                checkbox( "Lock", obj.locked);

                //obj.name = name;
                // check if hover because need for change position
                if ( obj.hover )
                    obj.changePos = true;

                if ( obj.selected && !noMove) {
                    moveOBJS.clear();
                    for (GuiBuilderClasses.BasicOBJ r_obj : objs) {
                        if ( r_obj.selected && !r_obj.locked ) {
                            MoveOBJ moveOBJ = new MoveOBJ();
                            moveOBJ.index = r_obj.id;
                            moveOBJ.pos = r_obj.pos;
                            moveOBJS.add(moveOBJ);
                        }
                    }

                    moveItems(moveOBJS, new AtomicBoolean(obj.changePos));

                    for (MoveOBJ teste : moveOBJS)
                    {
                        if (obj.id == teste.index)
                            obj.pos = teste.pos;
                        else
                            objs.get(teste.index).pos = teste.pos;
                    }
                }
                else if (!noMove)
                    if (!obj.locked )
                        moveItem(obj.pos, new AtomicBoolean(obj.changePos));

                copyOBJ(obj.myType, obj.child, obj.size, obj.pos, false, obj.selected, false);

                // dont delete here!
                if (button( "DELETE" ) || ImGui.isKeyDown(ImGuiKey.Delete)) {
                    obj.deleteMe = true;
                    currentItem	= "";
                    type = -1;
                }

                objs.set(index, obj);

                break;
            }
        }
        end();
    }

    private void moveItems(List<MoveOBJ> mto, AtomicBoolean continueEdt) {
        if (!relicApplication.getInput().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_LEFT) || !continueEdt.get()) {
            oldPosObjs.clear();
            movingObj = false;
            continueEdt.set(false);
            oldPos.x = 0;
            oldPos.y = 0;
            tickMove = 0;
            return;
        }

        long tickNow = getTickCount();
        ImVec2 currentPos = getRelativeCursorPos();

        if (tickMove == 0) {
            tickMove = tickNow + 100;
            oldPos.x = currentPos.x;
            oldPos.y = currentPos.y;

            oldPosObjs.clear();
            for (MoveOBJ obj : mto) {
                oldPosObjs.add(new ImVec2(obj.pos.x, obj.pos.y));
            }
            return;
        }

        if (mto.size() != oldPosObjs.size()) {
            oldPosObjs.clear();
            movingObj = false;
            continueEdt.set(false);
            oldPos.x = 0;
            oldPos.y = 0;
            tickMove = 0;
            return;
        }

        if (tickNow > tickMove) {
            movingObj = true;
            for (int i = 0; i < mto.size(); i++) {
                MoveOBJ m = mto.get(i);
                ImVec2 orig = oldPosObjs.get(i);
                m.pos.x = orig.x + currentPos.x - oldPos.x;
                m.pos.y = orig.y + currentPos.y - oldPos.y;
            }
        }
    }



    private void moveItem(ImVec2 objPos, AtomicBoolean continue_edt) {
        if (!ImGui.getIO().getMouseDown(0) || !continue_edt.get()) {
            oldPos.x = 0;
            oldPos.y = 0;
            movingObj = false;
            continue_edt.set(false);
            tickMove = 0;
            return;
        }

        long tickNow = getTickCount();
        ImVec2 current_pos = getRelativeCursorPos();

        if (tickMove == 0) {
            oldPos.x = current_pos.x - objPos.x;
            oldPos.y = current_pos.y - objPos.y;
            tickMove = tickNow + 50;
            return;
        }

        if (tickNow > tickMove) {
            movingObj = true;
            objPos.x = current_pos.x - oldPos.x;
            objPos.y = current_pos.y - oldPos.y;
        }
    }

    private long getTickCount() {
        return System.nanoTime() / 1_000_000;
    }

    private void setStyle(ImGuiStyle newStyle) {
        ImGuiStyle style = ImGui.getStyle();

        style.setColors(newStyle.getColors());

        style.setWindowPadding(newStyle.getWindowPadding());
        style.setFramePadding(newStyle.getFramePadding());
        style.setCellPadding(newStyle.getCellPadding());
        style.setItemSpacing(newStyle.getItemSpacing());
        style.setItemInnerSpacing(newStyle.getItemInnerSpacing());
        style.setTouchExtraPadding(newStyle.getTouchExtraPadding());
        style.setIndentSpacing(newStyle.getIndentSpacing());
        style.setScrollbarSize(newStyle.getScrollbarSize());
        style.setGrabMinSize(newStyle.getGrabMinSize());
        style.setWindowBorderSize(newStyle.getWindowBorderSize());
        style.setChildBorderSize(newStyle.getChildBorderSize());
        style.setPopupBorderSize(newStyle.getPopupBorderSize());
        style.setFrameBorderSize(newStyle.getFrameBorderSize());
        style.setTabBorderSize(newStyle.getTabBorderSize());

        style.setWindowRounding(newStyle.getWindowRounding());
        style.setChildRounding(newStyle.getChildRounding());
        style.setFrameRounding(newStyle.getFrameRounding());
        style.setPopupRounding(newStyle.getPopupRounding());
        style.setScrollbarRounding(newStyle.getScrollbarRounding());
        style.setGrabRounding(newStyle.getGrabRounding());
        style.setLogSliderDeadzone(newStyle.getLogSliderDeadzone());
        style.setTabRounding(newStyle.getTabRounding());

        style.setWindowTitleAlign(newStyle.getWindowTitleAlign());
        style.setWindowMenuButtonPosition(newStyle.getWindowMenuButtonPosition());
        style.setColorButtonPosition(newStyle.getColorButtonPosition());
        style.setButtonTextAlign(newStyle.getButtonTextAlign());
        style.setSelectableTextAlign(newStyle.getSelectableTextAlign());

        style.setDisplaySafeAreaPadding(newStyle.getDisplaySafeAreaPadding());
    }
}
