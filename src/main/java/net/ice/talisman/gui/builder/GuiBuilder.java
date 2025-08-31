package net.ice.talisman.gui.builder;

import imgui.ImVec2;
import imgui.type.ImBoolean;
import net.ice.relic.application.RelicApplication;
import org.joml.Vector2f;

import static imgui.ImGui.*;
import static imgui.extension.imguifiledialog.ImGuiFileDialog.openDialog;
import static imgui.flag.ImGuiWindowFlags.NoBringToFrontOnFocus;

public class GuiBuilder {

    private final RelicApplication application;

    private int id = -1;
    private int childID = -1;
    private int objID = -1;
    private boolean colorMenu = false;
    private boolean styleMenu = false;
    private boolean fontMenu = false;

    public GuiBuilder(RelicApplication application) {
        this.application = application;
    }


    private void draw() {

    }

    private boolean isHovered(ImVec2 objPos, ImVec2 objSize, float distance) {
        ImVec2 currentPos = getWindowPos();
        ImVec2 controlPos = new ImVec2(currentPos.x + (objPos.x - distance), currentPos.y + (objPos.y - distance));
        Vector2f cursorPos = application.getInput().getMousePosition();
        return ( cursorPos.y >= controlPos.y && cursorPos.y <= ( controlPos.y + (objSize.y  + ( distance * 2 ) ) ) && cursorPos.x >= controlPos.x && cursorPos.x <= ( controlPos.x + objSize.x + ( distance * 2 ) ) );
    }

    private void formWindowFlag()
    {
        begin( "Style Window Editor", new ImBoolean(styleMenu));
        if ( button( "Export" ) )
        {
            im_config::window_flags::to_clipboard( m_custom_gui_style );
            MessageBoxA( nullptr, "Code exported to clipboard !", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
        }

        sameLine();

        if (button("Load"))
        {
            ImGuiFileDialog::Instance( )->openDialog( "OpenFlagsDlgKey", "Open File", ".flags", RegeditGetPath( "ImGuiBuilderPath" ), "style_flags" );
        }

        sameLine( );
        if (button("Save" ))
        {
            ImGuiFileDialog::Instance( )->openDialog( "SaveFlagsDlgKey", "Save File", ".flags", RegeditGetPath( "ImGuiBuilderPath" ), "style_flags" );
        }

        text("First");
        if (sliderFloat( "FrameRounding", &m_custom_gui_style.FrameRounding, 0.0f, 12.0f, "%.0f" ))
        m_custom_gui_style.GrabRounding = m_custom_gui_style.FrameRounding;
        // Make GrabRounding always the same value as FrameRounding
        {
            var border = ( m_custom_gui_style.WindowBorderSize > 0.0f );
            if ( checkbox( "WindowBorder", &border ) ) { m_custom_gui_style.WindowBorderSize = border ? 1.0f : 0.0f; }
        }
        sameLine( );
        {
            var border = ( m_custom_gui_style.FrameBorderSize > 0.0f );
            if ( checkbox( "FrameBorder", &border ) ) { m_custom_gui_style.FrameBorderSize = border ? 1.0f : 0.0f; }
        }
        sameLine( );
        {
            var border = ( m_custom_gui_style.PopupBorderSize > 0.0f );
            if (checkbox( "PopupBorder", &border ) ) { m_custom_gui_style.PopupBorderSize = border ? 1.0f : 0.0f; }
        }

        text( "Main" );
        sliderFloat2( "WindowPadding",		reinterpret_cast<float*>( &m_custom_gui_style.WindowPadding		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "FramePadding",		reinterpret_cast<float*>( &m_custom_gui_style.FramePadding		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "CellPadding",			reinterpret_cast<float*>( &m_custom_gui_style.CellPadding		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "ItemSpacing",			reinterpret_cast<float*>( &m_custom_gui_style.ItemSpacing		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "ItemInnerSpacing",	reinterpret_cast<float*>( &m_custom_gui_style.ItemInnerSpacing	), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "TouchExtraPadding",	reinterpret_cast<float*>( &m_custom_gui_style.TouchExtraPadding ), 0.0f, 10.0f, "%.0f" );
        sliderFloat( "IndentSpacing",		&m_custom_gui_style.IndentSpacing,		0.0f, 30.0f, "%.0f" );
        sliderFloat( "ScrollbarSize",		&m_custom_gui_style.ScrollbarSize,		1.0f, 20.0f, "%.0f" );
        sliderFloat( "GrabMinSize",			&m_custom_gui_style.GrabMinSize,		1.0f, 20.0f, "%.0f" );
        text( "Borders" );
        sliderFloat( "WindowBorderSize",		&m_custom_gui_style.WindowBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "ChildBorderSize",		&m_custom_gui_style.ChildBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "PopupBorderSize",		&m_custom_gui_style.PopupBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "FrameBorderSize",		&m_custom_gui_style.FrameBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "TabBorderSize",		&m_custom_gui_style.TabBorderSize,		0.0f, 1.0f, "%.0f" );
        text( "Rounding" );
        sliderFloat( "WindowRounding",		&m_custom_gui_style.WindowRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "ChildRounding",		&m_custom_gui_style.ChildRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "FrameRounding",		&m_custom_gui_style.FrameRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "PopupRounding",		&m_custom_gui_style.PopupRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "ScrollbarRounding",	&m_custom_gui_style.ScrollbarRounding,	0.0f, 12.0f, "%.0f" );
        sliderFloat( "GrabRounding",			&m_custom_gui_style.GrabRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "LogSliderDeadzone",	&m_custom_gui_style.LogSliderDeadzone,	0.0f, 12.0f, "%.0f" );
        sliderFloat( "TabRounding",			&m_custom_gui_style.TabRounding,		0.0f, 12.0f, "%.0f" );
        text( "Alignment" );
        sliderFloat2( "WindowTitleAlign",	reinterpret_cast<float*>( &m_custom_gui_style.WindowTitleAlign ), 0.0f, 1.0f, "%.2f" );
        var window_menu_button_position = m_custom_gui_style.WindowMenuButtonPosition + 1;
        if (combo( "WindowMenuButtonPosition", static_cast<int*>( &window_menu_button_position ), "None\0Left\0Right\0" ) )
        m_custom_gui_style.WindowMenuButtonPosition = window_menu_button_position - 1;
        combo( "ColorButtonPosition",		static_cast<int*>( &m_custom_gui_style.ColorButtonPosition ), "Left\0Right\0" );
        sliderFloat2( "ButtonTextAlign",		reinterpret_cast<float*>( &m_custom_gui_style.ButtonTextAlign ), 0.0f, 1.0f, "%.2f" );

        sliderFloat2( "SelectableTextAlign", reinterpret_cast<float*>( &m_custom_gui_style.SelectableTextAlign ), 0.0f, 1.0f, "%.2f" );
        text( "Safe Area Padding" );
        sliderFloat2( "DisplaySafeAreaPadding", reinterpret_cast<float*>( &m_custom_gui_style.DisplaySafeAreaPadding ), 0.0f, 30.0f, "%.0f" );

        end( );
    }


    private void objectProperty() {
        setNextWindowPos(0, 100);
        setNextWindowSize(300, 700 - 100);
        begin("Property", null, NoBringToFrontOnFocus);
        boolean active = isWindowFocused();
        if(beginCombo("##items", )) {

        }
    }
}
