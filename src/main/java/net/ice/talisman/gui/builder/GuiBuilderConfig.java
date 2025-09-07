package net.ice.talisman.gui.builder;

import imgui.ImGuiStyle;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;
import net.ice.talisman.gui.builder.objs.Form;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public class GuiBuilderConfig {

    private static ImGuiStyle savedStyle = new ImGuiStyle();
    private static boolean outputOnlyModified = false;

    private static boolean checkFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static class WindowFlags {
        public static boolean loadFlags(String style, ImGuiStyle guiStyle) {
            try (BufferedReader reader = new BufferedReader(new FileReader(style))) {
                String line;
                int fileIndex = 0;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(",")) {
                        String[] parts = line.split(",");
                        switch (fileIndex) {
                            case 0:
                                guiStyle.getWindowPadding().x = Float.parseFloat(parts[0]);
                                guiStyle.getWindowPadding().y = Float.parseFloat(parts[1]);
                                break;
                            case 1:
                                guiStyle.getFramePadding().x = Float.parseFloat(parts[0]);
                                guiStyle.getFramePadding().y = Float.parseFloat(parts[1]);
                                break;
                            case 2:
                                guiStyle.getCellPadding().x = Float.parseFloat(parts[0]);
                                guiStyle.getCellPadding().y = Float.parseFloat(parts[1]);
                                break;
                            case 3:
                                guiStyle.getItemSpacing().x = Float.parseFloat(parts[0]);
                                guiStyle.getItemSpacing().y = Float.parseFloat(parts[1]);
                                break;
                            case 4:
                                guiStyle.getItemInnerSpacing().x = Float.parseFloat(parts[0]);
                                guiStyle.getItemInnerSpacing().y = Float.parseFloat(parts[1]);
                                break;
                            case 5:
                                guiStyle.getTouchExtraPadding().x = Float.parseFloat(parts[0]);
                                guiStyle.getTouchExtraPadding().y = Float.parseFloat(parts[1]);
                                break;
                            case 22:
                                guiStyle.getWindowTitleAlign().x = Float.parseFloat(parts[0]);
                                guiStyle.getWindowTitleAlign().y = Float.parseFloat(parts[1]);
                                break;
                            case 25:
                                guiStyle.getButtonTextAlign().x = Float.parseFloat(parts[0]);
                                guiStyle.getButtonTextAlign().y = Float.parseFloat(parts[1]);
                                break;
                            case 26:
                                guiStyle.getSelectableTextAlign().x = Float.parseFloat(parts[0]);
                                guiStyle.getSelectableTextAlign().y = Float.parseFloat(parts[1]);
                                break;
                            case 27:
                                guiStyle.getDisplaySafeAreaPadding().x = Float.parseFloat(parts[0]);
                                guiStyle.getDisplaySafeAreaPadding().y = Float.parseFloat(parts[1]);
                                break;
                        }
                    } else {
                        switch (fileIndex) {
                            case 6:  guiStyle.setIndentSpacing(Float.parseFloat(line)); break;
                            case 7:  guiStyle.setScrollbarSize(Float.parseFloat(line)); break;
                            case 8:  guiStyle.setGrabMinSize(Float.parseFloat(line)); break;
                            case 9:  guiStyle.setWindowBorderSize(Float.parseFloat(line)); break;
                            case 10: guiStyle.setChildBorderSize(Float.parseFloat(line)); break;
                            case 11: guiStyle.setPopupBorderSize(Float.parseFloat(line)); break;
                            case 12: guiStyle.setFrameBorderSize(Float.parseFloat(line)); break;
                            case 13: guiStyle.setTabBorderSize(Float.parseFloat(line)); break;
                            case 14: guiStyle.setWindowRounding(Float.parseFloat(line)); break;
                            case 15: guiStyle.setChildRounding(Float.parseFloat(line)); break;
                            case 16: guiStyle.setFrameRounding(Float.parseFloat(line)); break;
                            case 17: guiStyle.setPopupRounding(Float.parseFloat(line)); break;
                            case 18: guiStyle.setScrollbarRounding(Float.parseFloat(line)); break;
                            case 19: guiStyle.setGrabRounding(Float.parseFloat(line)); break;
                            case 20: guiStyle.setLogSliderDeadzone(Float.parseFloat(line)); break;
                            case 21: guiStyle.setTabRounding(Float.parseFloat(line)); break;
                            case 23: guiStyle.setWindowMenuButtonPosition((int)Float.parseFloat(line)); break;
                            case 24: guiStyle.setColorButtonPosition((int)Float.parseFloat(line)); break;
                        }
                    }
                    fileIndex++;
                }
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static boolean saveFlags(String style, ImGuiStyle guiStyle) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(style))) {
                writer.write(guiStyle.getWindowPadding().x + "," + guiStyle.getWindowPadding().y + "\n");
                writer.write(guiStyle.getFramePadding().x + "," + guiStyle.getFramePadding().y + "\n");
                writer.write(guiStyle.getCellPadding().x + "," + guiStyle.getCellPadding().y + "\n");
                writer.write(guiStyle.getItemSpacing().x + "," + guiStyle.getItemSpacing().y + "\n");
                writer.write(guiStyle.getItemInnerSpacing().x + "," + guiStyle.getItemInnerSpacing().y + "\n");
                writer.write(guiStyle.getTouchExtraPadding().x + "," + guiStyle.getTouchExtraPadding().y + "\n");
                writer.write(guiStyle.getIndentSpacing() + "\n");
                writer.write(guiStyle.getScrollbarSize() + "\n");
                writer.write(guiStyle.getGrabMinSize() + "\n");
                writer.write(guiStyle.getWindowBorderSize() + "\n");
                writer.write(guiStyle.getChildBorderSize() + "\n");
                writer.write(guiStyle.getPopupBorderSize() + "\n");
                writer.write(guiStyle.getFrameBorderSize() + "\n");
                writer.write(guiStyle.getTabBorderSize() + "\n");

                writer.write(guiStyle.getWindowRounding() + "\n");
                writer.write(guiStyle.getChildRounding() + "\n");
                writer.write(guiStyle.getFrameRounding() + "\n");
                writer.write(guiStyle.getPopupRounding() + "\n");
                writer.write(guiStyle.getScrollbarRounding() + "\n");
                writer.write(guiStyle.getGrabRounding() + "\n");
                writer.write(guiStyle.getLogSliderDeadzone() + "\n");
                writer.write(guiStyle.getTabRounding() + "\n");

                writer.write(guiStyle.getWindowTitleAlign().x + "," + guiStyle.getWindowTitleAlign().y + "\n");
                writer.write(guiStyle.getWindowMenuButtonPosition() + "\n");
                writer.write(guiStyle.getColorButtonPosition() + "\n");
                writer.write(guiStyle.getButtonTextAlign().x + "," + guiStyle.getButtonTextAlign().y + "\n");
                writer.write(guiStyle.getSelectableTextAlign().x + "," + guiStyle.getSelectableTextAlign().y + "\n");

                writer.write(guiStyle.getDisplaySafeAreaPadding().x + "," + guiStyle.getDisplaySafeAreaPadding().y + "\n");

            } catch (IOException ioException) {
                ioException.printStackTrace();
                return false;
            }
            return checkFileExists( style );
        }

        public static void flagsToClipboard(ImGuiStyle guiStyle) {
            logToClipboard();

            logText("namespace ImGui {\n void CustomStyle() {\n");
            logText("ImGuiStyle& style = ImGui::GetStyle();\n");

            // Padding
            logText(String.format("style.WindowPadding = ImVec2(%.2ff, %.2ff);\n", guiStyle.getWindowPadding().x, guiStyle.getWindowPadding().y));
            logText(String.format("style.FramePadding = ImVec2(%.2ff, %.2ff);\n", guiStyle.getFramePadding().x, guiStyle.getFramePadding().y));
            logText(String.format("style.CellPadding = ImVec2(%.2ff, %.2ff);\n", guiStyle.getCellPadding().x, guiStyle.getCellPadding().y));
            logText(String.format("style.ItemSpacing = ImVec2(%.2ff, %.2ff);\n", guiStyle.getItemSpacing().x, guiStyle.getItemSpacing().y));
            logText(String.format("style.ItemInnerSpacing = ImVec2(%.2ff, %.2ff);\n", guiStyle.getItemInnerSpacing().x, guiStyle.getItemInnerSpacing().y));
            logText(String.format("style.TouchExtraPadding = ImVec2(%.2ff, %.2ff);\n", guiStyle.getTouchExtraPadding().x, guiStyle.getTouchExtraPadding().y));

            logText(String.format("style.IndentSpacing = %.2ff;\n", guiStyle.getIndentSpacing()));
            logText(String.format("style.ScrollbarSize = %.2ff;\n", guiStyle.getScrollbarSize()));
            logText(String.format("style.GrabMinSize = %.2ff;\n", guiStyle.getGrabMinSize()));
            logText(String.format("style.WindowBorderSize = %.2ff;\n", guiStyle.getWindowBorderSize()));
            logText(String.format("style.ChildBorderSize = %.2ff;\n", guiStyle.getChildBorderSize()));
            logText(String.format("style.PopupBorderSize = %.2ff;\n", guiStyle.getPopupBorderSize()));
            logText(String.format("style.FrameBorderSize = %.2ff;\n", guiStyle.getFrameBorderSize()));
            logText(String.format("style.TabBorderSize = %.2ff;\n", guiStyle.getTabBorderSize()));

            // Rounding
            logText(String.format("style.WindowRounding = %.2ff;\n", guiStyle.getWindowRounding()));
            logText(String.format("style.ChildRounding = %.2ff;\n", guiStyle.getChildRounding()));
            logText(String.format("style.FrameRounding = %.2ff;\n", guiStyle.getFrameRounding()));
            logText(String.format("style.PopupRounding = %.2ff;\n", guiStyle.getPopupRounding()));
            logText(String.format("style.ScrollbarRounding = %.2ff;\n", guiStyle.getScrollbarRounding()));
            logText(String.format("style.GrabRounding = %.2ff;\n", guiStyle.getGrabRounding()));
            logText(String.format("style.LogSliderDeadzone = %.2ff;\n", guiStyle.getLogSliderDeadzone()));
            logText(String.format("style.TabRounding = %.2ff;\n", guiStyle.getTabRounding()));

            // Position & alignment
            logText(String.format("style.WindowTitleAlign = ImVec2(%.2ff, %.2ff);\n", guiStyle.getWindowTitleAlign().x, guiStyle.getWindowTitleAlign().y));
            logText(String.format("style.WindowMenuButtonPosition = %d;\n", guiStyle.getWindowMenuButtonPosition()));
            logText(String.format("style.ColorButtonPosition = %d;\n", guiStyle.getColorButtonPosition()));
            logText(String.format("style.ButtonTextAlign = ImVec2(%.2ff, %.2ff);\n", guiStyle.getButtonTextAlign().x, guiStyle.getButtonTextAlign().y));
            logText(String.format("style.SelectableTextAlign = ImVec2(%.2ff, %.2ff);\n", guiStyle.getSelectableTextAlign().x, guiStyle.getSelectableTextAlign().y));

            logText(String.format("style.DisplaySafeAreaPadding = ImVec2(%.2ff, %.2ff);\n", guiStyle.getDisplaySafeAreaPadding().x, guiStyle.getDisplaySafeAreaPadding().y));

            logText("}\n}\n");

            logFinish();
        }
    }
    public static class Color {
        public static boolean saveColors(String style, ImGuiStyle guiStyle)
        {
            File file = new File(style);
            if (file.exists()) {
                file.delete();
            }

            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(""))) {
                for(ImVec4 vec4 : guiStyle.getColors()) {
                    bufferedWriter.write(String.format("%f, %f, %f, %f", vec4.x, vec4.y, vec4.z, vec4.w));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return checkFileExists(style);
        }

        public static boolean loadColors(String style, ImGuiStyle guiStyle) {
            File file = new File(style);

            if (!file.exists()) {
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int index = 0;

                while ((line = reader.readLine()) != null && index < guiStyle.getColors().length) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        float x = Float.parseFloat(parts[0].trim());
                        float y = Float.parseFloat(parts[1].trim());
                        float z = Float.parseFloat(parts[2].trim());
                        float w = Float.parseFloat(parts[3].trim());

                        guiStyle.getColors()[index] = new ImVec4(x, y, z, w);
                    }
                    index++;
                }

                return true;
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static void colorsToClipboard(ImGuiStyle guiStyle )
        {
            logToClipboard();

            logText( "namespace ImGui {\n void CustomColor() {\n" );
            logText( "auto* colors = ImGui::GetStyle().Colors;\n" );
            for (var i = 0; i < ImGuiCol.COUNT; i++ ) {

                ImVec4 col = guiStyle.getColors()[ i ];
                String name = getStyleColorName( i );

                if ( !outputOnlyModified || !col.equals(savedStyle.getColors()[i])) {
                    int padding = 23 - name.length();
                    String spaces = " ".repeat(Math.max(0, padding));

                    String line = String.format(
                            "colors[ImGuiCol_%s]%s= ImVec4(%.2ff, %.2ff, %.2ff, %.2ff);\n",
                            name, spaces, col.x, col.y, col.z, col.w
                    );
                    logText(line);
                }
            }
            logText( "}\n}\n" );
            logFinish();
        }

        public static ImVec4[] getSavedColors()
        {
            return savedStyle.getColors();
        }
    }
    public static class Controls {
        public static boolean saveControls(String fileName, List<Form> forms, List<GuiBuilderClasses.BasicOBJ> objs) {
            File file = new File(fileName);
            if(file.exists()) {
                file.delete();
            }
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for(Form form : forms) {
                    writer.write("#forms\n");
                    writer.write(form.getID() + "," + form.getName() + "," + form.getSize().x + "," + form.getSize().y + "\n");
                    for(GuiBuilderClasses.Child child : form.getChildren()) {
                        writer.write("#child\n");
                        writer.write(child.id + "," + child.father + "," + child.name + "," + child.size.x + "," + child.size.y + "," + child.pos.x + "," + child.pos.y + "\n");
                    }
                    for(GuiBuilderClasses.BasicOBJ obj : objs) {
                        if (form.getID() == obj.form ) {
                            writer.write("#obj\n");
                            writer.write(obj.id + "," + obj.form + "," + obj.child + "," + obj.name + "," + obj.myType + "," + obj.size.x + "," + obj.size.y + "," + obj.pos.x + "," + obj.pos.y + "\n");
                        }
                    }
                }
                writer.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return false;
            }
            return checkFileExists(fileName);
        }
        public static boolean loadControls(String file, List<Form> forms, List<GuiBuilderClasses.BasicOBJ> objs, int[] last_ids) {
            if (last_ids == null) return false;

            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                if(reader.ready()) {
                    String line;
                    var menu = 0;

                    while((line = reader.readLine()) != null) {
                        Form formLoad = new Form();
                        GuiBuilderClasses.Child childLoad = new GuiBuilderClasses.Child();
                        GuiBuilderClasses.BasicOBJ objLoad = new GuiBuilderClasses.BasicOBJ();

                        if(!line.equals("#forms")) {
                            menu = 0;
                            continue;
                        }
                        if (!line.equals("#child")) {
                            menu = 1;
                            continue;
                        }
                        if (!line.equals("#obj")) {
                            menu = 2;
                            continue;
                        }
                        if(!line.isEmpty()) {
                            menu = 5;
                            continue;
                        }

                        var varString = line.split(",");
                        switch(menu) {
                            case 0 -> {
                                formLoad.setID(Integer.parseInt(varString[0]));
                                last_ids[0]			= formLoad.getID();
                                //id_ = form_load.id;
                                formLoad.setName(varString[1]);
                                formLoad.getSize().x = Float.parseFloat(varString[2]);
                                formLoad.getSize().y = Float.parseFloat(varString[3]);


                                forms.addLast(formLoad);

                                System.out.println( "Loading form\n" );
                                break;
                            }
                            case 1 -> {
                                childLoad.id = Integer.parseInt(varString[0]);
                                last_ids[1] = childLoad.id;
                                //child_id = child_load.id;
                                childLoad.father	= Integer.parseInt(varString[1]);
                                childLoad.name		= varString[2];
                                childLoad.size.x	= Float.parseFloat(varString[3]);
                                childLoad.size.y	= Float.parseFloat(varString[4]);
                                childLoad.pos.x	= Float.parseFloat(varString[5]);
                                childLoad.pos.y	= Float.parseFloat(varString[6]);
                                forms.get(childLoad.father).getChildren().addLast(childLoad);

                                System.out.println( "Loading child\n" );
                                break;
                            }
                            case 2 -> {
                                objLoad.id = Integer.parseInt(varString[0]);
                                last_ids[2] = objLoad.id;
                                //obj_id = obj_load.id;
                                objLoad.form = Integer.parseInt(varString[1]);
                                objLoad.child = Integer.parseInt(varString[2]);
                                objLoad.name = new ImString(varString[3]);
                                objLoad.myType = Integer.parseInt(varString[4]);
                                objLoad.size.x = Float.parseFloat(varString[5]);
                                objLoad.size.y = Float.parseFloat(varString[6]);
                                objLoad.pos.x = Float.parseFloat(varString[7]);
                                objLoad.pos.y = Float.parseFloat(varString[8]);

                                objs.addLast(objLoad);

                                System.out.println("Loading obj\n");
                                break;
                            }
                            default -> {
                                break;
                            }
                        }
                    }
                    reader.close();
                    return true;
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return false;
            }
            return false;
        }
        public static boolean createCode(String fileName, List<Form> forms, List<GuiBuilderClasses.BasicOBJ> objs) {
            int fctn = 0;
            StringBuilder fileBuilder = new StringBuilder(
                    " /* GENERATED WITH IMGUI BUILDER :) HAS " + objs.size() + " Objs & " + forms.size() + " forms */\n\n\n"
            );

            // Check if there is any toggle object (type 8)
            boolean haveToggle = objs.stream().anyMatch(obj -> obj.myType == 8);
            if (haveToggle) {
                fileBuilder.append(
                        "void ToggleButton(const char* str_id, bool* v)\n{\n" +
                                "    ImVec2 p = ImGui::GetCursorScreenPos();\n" +
                                "    ImDrawList* draw_list = ImGui::GetWindowDrawList();\n" +
                                "    float height = ImGui::GetFrameHeight();\n" +
                                "    float width = height * 1.55f;\n" +
                                "    float radius = height * 0.50f;\n" +
                                "    if (ImGui::InvisibleButton(str_id, ImVec2(width, height))) *v = !*v;\n" +
                                "    ImU32 col_bg;\n" +
                                "    if (ImGui::IsItemHovered())\n" +
                                "        col_bg = *v ? IM_COL32(165, 211, 88, 255) : IM_COL32(198, 198, 198, 255);\n" +
                                "    else\n" +
                                "        col_bg = *v ? IM_COL32(145, 211, 68, 255) : IM_COL32(218, 218, 218, 255);\n" +
                                "    draw_list->AddRectFilled(p, ImVec2(p.x + width, p.y + height), col_bg, height * 0.5f);\n" +
                                "    draw_list->AddCircleFilled(ImVec2(*v ? (p.x + width - radius) : (p.x + radius), p.y + radius), radius - 1.5f, IM_COL32(255, 255, 255, 255));\n" +
                                "}\n\n"
                );
            }

            // Loop over forms
            for (Form form : forms) {
                fileBuilder.append("void gui_builder").append(fctn).append("() {\n");
                fileBuilder.append("    setNextWindowSize({")
                        .append((int) form.getSize().x).append(".f,")
                        .append((int) form.getSize().y).append(".f});\n");
                fileBuilder.append("    ImGui::Begin(\"").append(form.getName()).append("\");\n");

                // Prepare child counts
                List<Integer> xild = new ArrayList<>();
                for (GuiBuilderClasses.Child child : form.getChildren()) {
                    int itemOnChild = (int) objs.stream().filter(o -> o.child == child.id).count();
                    xild.add(itemOnChild);
                }

                int writerChild = 0;
                int coutChild = -1;

                for (GuiBuilderClasses.BasicOBJ obj : objs) {
                    for (GuiBuilderClasses.Child chl : form.getChildren()) {
                        if (obj.child == chl.id) {
                            if (writerChild == 0) {
                                coutChild++;
                                fileBuilder.append("    setCursorPos({")
                                        .append((int) chl.pos.x).append(".f,")
                                        .append((int) chl.pos.y).append(".f});\n");
                                fileBuilder.append("    ImGui::BeginChild(\"").append(chl.name).append("\",{")
                                        .append((int) chl.size.x).append(".f,")
                                        .append((int) chl.size.y).append(".f}, true);\n\n");
                            }

                            if (obj.child == chl.id && obj.form == form.getID() && chl.father == form.getID()) {
                                fileBuilder.append("    setCursorPos({")
                                        .append((int) obj.pos.x).append(".f,")
                                        .append((int) obj.pos.y).append(".f});\n");

                                switch (obj.myType) {
                                    case 1 -> fileBuilder.append("    if(ImGui::Button(\"").append(obj.name).append("\",{")
                                            .append((int) obj.size.x).append(".f,")
                                            .append((int) obj.size.y).append(".f})) {\n    }\n");
                                    case 2 -> {
                                        fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                        fileBuilder.append("    ImGui::Text(\"").append(obj.name).append("\");\n");
                                        fileBuilder.append("    ImGui::PopItemWidth();\n\n");
                                    }
                                    case 3 -> {
                                        fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                        fileBuilder.append("    ImGui::InputText(\"").append(obj.name).append("\", buffer, 255);\n");
                                        fileBuilder.append("    ImGui::PopItemWidth();\n\n");
                                    }
                                    case 4 -> {
                                        fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                        fileBuilder.append("    sliderInt(\"").append(obj.name).append("\", &valueI, 0, 100);\n");
                                        fileBuilder.append("    ImGui::PopItemWidth();\n\n");
                                    }
                                    case 5 -> {
                                        fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                        fileBuilder.append("    sliderFloat(\"").append(obj.name).append("\", &valueF, 0, 100);\n");
                                        fileBuilder.append("    ImGui::PopItemWidth();\n\n");
                                    }
                                    case 6 -> fileBuilder.append("    ImGui::Checkbox(\"").append(obj.name).append("\", &the_bool);\n");
                                    case 7 -> fileBuilder.append("    ImGui::RadioButton(\"").append(obj.name).append("\", the_bool);\n");
                                    case 8 -> fileBuilder.append("    ToggleButton(\"").append(obj.name).append("\", the_bool);\n");
                                    default -> {}
                                }
                                writerChild++;
                            }
                        }
                    }

                    // End child if all items written
                    if (coutChild > -1 && xild.get(coutChild) == writerChild) {
                        fileBuilder.append("    ImGui::EndChild();\n");
                        coutChild = -1;
                        writerChild = 0;
                    }

                    // Obj without child
                    if (obj.child == -1 && obj.form == form.getID()) {
                        fileBuilder.append("    setCursorPos({")
                                .append((int) obj.pos.x).append(".f,")
                                .append((int) obj.pos.y).append(".f});\n");

                        switch (obj.myType) {
                            case 1 -> fileBuilder.append("    if(ImGui::Button(\"").append(obj.name).append("\",{")
                                    .append((int) obj.size.x).append(".f,")
                                    .append((int) obj.size.y).append(".f})) {\n    }\n");
                            case 2 -> {
                                fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                fileBuilder.append("    ImGui::Text(\"").append(obj.name).append("\");\n");
                                fileBuilder.append("    ImGui::PopItemWidth();\n");
                            }
                            case 3 -> {
                                fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                fileBuilder.append("    ImGui::InputText(\"").append(obj.name).append("\", buffer, 255);\n");
                                fileBuilder.append("    ImGui::PopItemWidth();\n");
                            }
                            case 4 -> {
                                fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                fileBuilder.append("    sliderInt(\"").append(obj.name).append("\", &valueI, 0, 100);\n");
                                fileBuilder.append("    ImGui::PopItemWidth();\n");
                            }
                            case 5 -> {
                                fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                fileBuilder.append("    liderFloat(\"").append(obj.name).append("\", &valueF, 0, 100);\n");
                                fileBuilder.append("    ImGui::PopItemWidth();\n");
                            }
                            case 6 -> fileBuilder.append("    ImGui::Checkbox(\"").append(obj.name).append("\", &the_bool);\n");
                            case 7 -> fileBuilder.append("    ImGui::RadioButton(\"").append(obj.name).append("\", the_bool);\n");
                            case 8 -> fileBuilder.append("    ToggleButton(\"").append(obj.name).append("\", the_bool);\n");
                            default -> {}
                        }
                    }
                }

                fileBuilder.append("    ImGui::End();\n}\n\n\n");
                fctn++;
            }

            // Write to file
            try {
                File file = new File(fileName);
                if (file.exists()) file.delete();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(fileBuilder.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return checkFileExists(fileName);
        }
    }
}
