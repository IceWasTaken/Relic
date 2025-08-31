package net.ice.talisman.gui.builder;

import imgui.ImGuiStyle;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;

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
        private boolean load(String style, ImGuiStyle guiStyle) {
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

        private boolean save(String style, ImGuiStyle guiStyle) {
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

        private void toClipboard(ImGuiStyle guiStyle) {
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

        private boolean saveColor(String style, ImGuiStyle guiStyle)
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

        private boolean loadColor(String style, ImGuiStyle guiStyle) {
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

        private void colorsToClipboard(ImGuiStyle guiStyle )
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

        private ImVec4[] getColor()
        {
            return savedStyle.getColors();
        }
    }
    public static class Controls {
        private boolean save(String file, List<GuiBuilderClasses.Form> forms, List<GuiBuilderClasses.BasicOBJ> objs) {}
        private boolean load(String file, List<GuiBuilderClasses.Form> forms, List<GuiBuilderClasses.BasicOBJ> objs, int[] theIds) {}
        private boolean createCode(String fileName, List<GuiBuilderClasses.Form> forms, List<GuiBuilderClasses.BasicOBJ> objs) {
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
            for (GuiBuilderClasses.Form form : forms) {
                fileBuilder.append("void gui_builder").append(fctn).append("() {\n");
                fileBuilder.append("    ImGui::SetNextWindowSize({")
                        .append((int) form.size.x).append(".f,")
                        .append((int) form.size.y).append(".f});\n");
                fileBuilder.append("    ImGui::Begin(\"").append(form.name).append("\");\n");

                // Prepare child counts
                List<Integer> xild = new ArrayList<>();
                for (GuiBuilderClasses.Child child : form.child) {
                    int itemOnChild = (int) objs.stream().filter(o -> o.child == child.id).count();
                    xild.add(itemOnChild);
                }

                int writerChild = 0;
                int coutChild = -1;

                for (GuiBuilderClasses.BasicOBJ obj : objs) {
                    for (GuiBuilderClasses.Child chl : form.child) {
                        if (obj.child == chl.id) {
                            if (writerChild == 0) {
                                coutChild++;
                                fileBuilder.append("    ImGui::SetCursorPos({")
                                        .append((int) chl.pos.x).append(".f,")
                                        .append((int) chl.pos.y).append(".f});\n");
                                fileBuilder.append("    ImGui::BeginChild(\"").append(chl.name).append("\",{")
                                        .append((int) chl.size.x).append(".f,")
                                        .append((int) chl.size.y).append(".f}, true);\n\n");
                            }

                            if (obj.child == chl.id && obj.form == form.id && chl.father == form.id) {
                                fileBuilder.append("    ImGui::SetCursorPos({")
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
                                        fileBuilder.append("    ImGui::SliderInt(\"").append(obj.name).append("\", &valueI, 0, 100);\n");
                                        fileBuilder.append("    ImGui::PopItemWidth();\n\n");
                                    }
                                    case 5 -> {
                                        fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                        fileBuilder.append("    ImGui::SliderFloat(\"").append(obj.name).append("\", &valueF, 0, 100);\n");
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
                    if (obj.child == -1 && obj.form == form.id) {
                        fileBuilder.append("    ImGui::SetCursorPos({")
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
                                fileBuilder.append("    ImGui::SliderInt(\"").append(obj.name).append("\", &valueI, 0, 100);\n");
                                fileBuilder.append("    ImGui::PopItemWidth();\n");
                            }
                            case 5 -> {
                                fileBuilder.append("    ImGui::PushItemWidth(").append(obj.size.x).append(");\n");
                                fileBuilder.append("    ImGui::SliderFloat(\"").append(obj.name).append("\", &valueF, 0, 100);\n");
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

    private boolean loadControls(String file, std::vector<form> & forms, std::vector<basic_obj> & objs, int * last_ids )
    {
        if ( !last_ids ) return false;

        std::ifstream f_read( file );
        if ( f_read.is_open( ) )
        {
            std::string line;
            auto menu = 0;

            while ( !f_read.eof( ) )
            {
                form		form_load;
                child		child_load;
                basic_obj	obj_load;

                std::getline( f_read, line );
                if ( !line.compare( "#forms" ) )
                {
                    menu = 0;
                    continue;
                }
                if ( !line.compare( "#child" ) )
                {
                    menu = 1;
                    continue;
                }
                if ( !line.compare( "#obj" ) )
                {
                    menu = 2;
                    continue;
                }
                if ( !line.compare( "" ) )
                {
                    menu = 5;
                    continue;
                }

                // VAR STRING
                auto vstr = utils::split( line, ',' );
                switch ( menu )
                {
                    case 0:

                        form_load.id		= std::stoi( vstr[ 0 ] );
                        last_ids[0]			= form_load.id;
                        //id_ = form_load.id;
                        form_load.name		= vstr[ 1 ];
                        form_load.size.x	= std::stof( vstr[ 2 ] );
                        form_load.size.y	= std::stof( vstr[ 3 ] );

                        forms.push_back( form_load );

                        printf( "Loading form\n" );
                        break;

                    case 1:
                        child_load.id		= std::stoi( vstr[ 0 ] );
                        last_ids[ 1 ]		= child_load.id;
                        //child_id = child_load.id;
                        child_load.father	= std::stoi( vstr[ 1 ] );
                        child_load.name		= vstr[ 2 ];
                        child_load.size.x	= std::stof( vstr[ 3 ] );
                        child_load.size.y	= std::stof( vstr[ 4 ] );
                        child_load.pos.x	= std::stof( vstr[ 5 ] );
                        child_load.pos.y	= std::stof( vstr[ 6 ] );
                        forms[ child_load.father ].child.push_back( child_load );

                        printf( "Loading child\n" );
                        break;

                    case 2:
                        obj_load.id			= std::stoi( vstr[ 0 ] );
                        last_ids[ 2 ]		= obj_load.id;
                        //obj_id = obj_load.id;
                        obj_load.form		= std::stoi( vstr[ 1 ] );
                        obj_load.child		= std::stoi( vstr[ 2 ] );
                        obj_load.name		= vstr[ 3 ];
                        obj_load.my_type	= std::stoi( vstr[ 4 ] );
                        obj_load.size.x		= std::stof( vstr[ 5 ] );
                        obj_load.size.y		= std::stof( vstr[ 6 ] );
                        obj_load.pos.x		= std::stof( vstr[ 7 ] );
                        obj_load.pos.y		= std::stof( vstr[ 8 ] );

                        objs.push_back( obj_load );

                        printf( "Loading obj\n" );
                        break;
                    default:

                        break;
                }
            }
            f_read.close( );
            return true;
        }
        return false;
    }

    private boolean saveControls( std::string& file, std::vector<form> forms, std::vector<basic_obj> objs )
    {
        remove( file.c_str( ) );
        std::ofstream f_write( file );

        if ( f_write.is_open( ) )
        {
            for ( const auto& form : forms )
            {
                f_write << "#forms\n";
                f_write << form.id << "," << form.name << "," << form.size.x << "," << form.size.y << "\n";

                for ( const auto& ch : form.child )
                {
                    f_write << "#child\n";
                    f_write << ch.id << "," << ch.father << "," << ch.name << "," << ch.size.x << "," << ch.size.y << "," <<
                            ch.pos.x << "," << ch.pos.y << "\n";
                }

                for ( const auto& obj : objs )
                {
                    if ( form.id == obj.form )
                    {
                        f_write << "#obj\n";
                        f_write << obj.id << "," << obj.form << "," << obj.child << "," << obj.name << "," << obj.my_type <<
                                "," << obj.size.x << "," << obj.size.y << "," << obj.pos.x << "," << obj.pos.y << "\n";
                    }
                }
            }

            f_write.close( );
        }
        return checkFileExists(file);
    }


}
