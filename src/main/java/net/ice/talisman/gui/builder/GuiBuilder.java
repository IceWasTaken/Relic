package net.ice.talisman.gui.builder;

import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static imgui.ImGui.*;
import static imgui.extension.imguifiledialog.ImGuiFileDialog.openDialog;
import static imgui.flag.ImGuiWindowFlags.*;

public class GuiBuilder {

    private int id = -1;
    private int childID = -1;
    private int objID = -1;
    private int activeWindowID = 0;
    private int index = 0;
    private int family = 0;
    private int grandchild = -1;
    private int type = -1;
    private boolean colorMenu = false;
    private boolean styleMenu = false;
    private boolean fontMenu = false;
    private String currentItem = "";
    private String name = "";
    private ImVec2 formPos = new ImVec2();
    private ImVec2 itemSize = new ImVec2();
    private ImVec2 posOBJ = new ImVec2();
    private List<GuiBuilderClasses.Form> forms = new ArrayList<>();
    private List<GuiBuilderClasses.BasicOBJ> objs = new ArrayList<>();
    private ImGuiStyle darkStyle = new ImGuiStyle();
    private ImGuiStyle customGUIStyle = new ImGuiStyle();


    boolean	movingObj	= false;
    boolean resizeObj = false;
    long tickMove = 0;

    class moveOBJ {
        int index = 0;
        ImVec2 pos = new ImVec2();
    }

    private String getNameType(int type)
    {
        return switch (type) {
            case 1 -> "button";
            case 2 -> "label";
            case 3 -> "edit";
            case 4 -> "sliderI";
            case 5 -> "sliderF";
            case 6 -> "checkbox";
            case 7 -> "radio";
            case 8 -> "toggle";
            default -> "";
        };
    }

    resize_opt limit_bordering_control( ImVec2 obj_pos, ImVec2 obj_size, float thickness = 3.f )
    {
        var current_win_pos	= getWindowPos( );

        var control_win_pos	= ImVec2( current_win_pos.x + obj_pos.x, current_win_pos.y + obj_pos.y );

        var pos				= window::i( )->get_relative_cursor_pos( );

        var top				= ( pos.y >= (long)( control_win_pos.y - thickness ) && pos.y <= (long)( control_win_pos.y ) );

        var bottom				= ( pos.y >= (long)( control_win_pos.y + obj_size.y ) && pos.y <= (long)( control_win_pos.y + obj_size.y + thickness ) );

        var left				= ( pos.x >= (long)( control_win_pos.x - thickness ) && pos.x <= (long)( control_win_pos.x ) );

        var right				= ( pos.x >= (long)( control_win_pos.x + obj_size.x ) && pos.x <= (long)( control_win_pos.x + obj_size.x + thickness ) );

        if ( ( bottom && right ) || ( top && left ) )
        {
            return ( bottom && right ) ? resize_opt::bottom_right : resize_opt::top_left;
        }
        else
        if ( ( top && right ) || ( bottom && left ) )
        {
            return ( top && right ) ? resize_opt::top_right : resize_opt::bottom_left;
        }
        else
        {
            if ( top || bottom )
                return ( top ) ? resize_opt::top : resize_opt::bottom;
            else
            if ( left || right )
                return ( left ) ? resize_opt::left : resize_opt::right;

        }
        return resize_opt::off;
    }

    boolean isItemHovered(ImVec2 obj_pos, ImVec2 obj_size, float distance) {
        var current_win_pos	= getWindowPos();
        var control_win_pos	= new ImVec2(current_win_pos.x + ( obj_pos.x - distance ), current_win_pos.y + ( obj_pos.y - distance ) );
        var pos				= window::i( )->get_relative_cursor_pos( );
        return ( pos.y >= control_win_pos.y && pos.y <= ( control_win_pos.y + (obj_size.y  + ( distance * 2 ) ) ) && pos.x >= control_win_pos.x && pos.x <= ( control_win_pos.x + obj_size.x + ( distance * 2 ) ) );
    }

    void move_item( ImVec2& obj_pos, bool& continue_edt );
    void move_items( std::vector<move_obj>& mto, bool& continue_edt );

    imgui_builder::imgui_builder( )
    {


        this->cursor.m_arrow_top_or_bottom				= LoadCursor( NULL, IDC_SIZENS );
        this->cursor.m_arrow_left_or_right				= LoadCursor( NULL, IDC_SIZEWE );
        this->cursor.m_arrow_northwest_and_southeast	= LoadCursor( NULL, IDC_SIZENWSE );
        this->cursor.m_arrow_northeast_and_southwest	= LoadCursor( NULL, IDC_SIZENESW );
        this->cursor.m_arrow_all						= LoadCursor( NULL, IDC_SIZEALL );

        styleColorsDark( &m_dark_style );
        for ( auto i = 0; i < ImGuiCol_COUNT; i++ )
            customGUIStyle.Colors[ i ] = m_dark_style.Colors[ i ];

        m_forms.clear( );
        m_objs.clear( );
    }

    /// <summary>
/// here will show the dialogs for saving or uploading files
/// </summary>
///
    void imgui_builder::draw_dialogs_save_open( )
    {

        static std::vector<std::string> dialogs_keys = {
            "SaveProjectFileDlgKey",
            "OpenProjectFileDlgKey",
            "GenCodeProjectFileDlgKey",
            "SaveColorsDlgKey",
            "OpenColorsDlgKey",
            "SaveFlagsDlgKey",
            "OpenFlagsDlgKey"
    };

        var DeleteKey = [ & ] ( const std::string& keyname ) -> void {
        HKEY hKey;
        RegOpenKeyExA( HKEY_CURRENT_USER, NULL, 0, KEY_SET_VALUE, &hKey );

        RegDeleteKeyExA( hKey, keyname.c_str( ), KEY_WOW64_64KEY, NULL );

        RegCloseKey( hKey );
    };

        var SetValue = [ & ] ( const std::string& keyname, const std::string& value ) -> void {
        DeleteKey( keyname );

        HKEY hKey;
        RegOpenKeyExA( HKEY_CURRENT_USER, NULL, 0, KEY_SET_VALUE, &hKey );

        var status = RegSetValueExA( hKey, keyname.c_str( ), NULL, REG_SZ, (LPBYTE)value.c_str( ), value.size( ) + 1 );

        printf_s( "RegSetValueEx: %d\n", status );

        RegCloseKey( hKey );
    };

        var i = 0;
        for ( var key : dialogs_keys )
        {
            if ( ImGuiFileDialog::Instance( )->Display( key.c_str(), 32, { 350.f, 300.f } ) )
            {
                if ( ImGuiFileDialog::Instance( )->IsOk( ) )
                {
                    std::string file_full_path	= ImGuiFileDialog::Instance( )->GetFilePathName( );
                    std::string full_path		= ImGuiFileDialog::Instance( )->GetCurrentPath( );

                    SetValue( "ImGuiBuilderPath", full_path );

                    switch ( i )
                    {
                        case 0:
                        {
                            if ( im_config::controls::save( file_full_path, m_forms, m_objs ) )
                            MessageBoxA( nullptr, "Project saved!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        case 1:
                        {
                            m_forms.clear( );
                            m_objs.clear( );
                            if ( im_config::controls::load( file_full_path, m_forms, m_objs, &m_id ) )
                            MessageBoxA( nullptr, "Project loaded!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        case 2:
                        {
                            if ( im_config::controls::create_code( file_full_path, m_forms, m_objs ) )
                            MessageBoxA( nullptr, "Code been generated!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        case 3:
                        {
                            if ( im_config::color::save( file_full_path, customGUIStyle ) )
                            MessageBoxA( nullptr, "Colors saved!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        case 4:
                        {
                            if ( im_config::color::load( file_full_path, customGUIStyle ) )
                            MessageBoxA( nullptr, "Colors loaded!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        case 5:
                        {
                            if ( im_config::window_flags::save( file_full_path, customGUIStyle ) )
                            MessageBoxA( nullptr, "Flags saved!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        case 6:
                        {
                            if ( im_config::window_flags::load( file_full_path, customGUIStyle ) )
                            MessageBoxA( nullptr, "Flags loaded!", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
                            break;
                        }
                        default:
                            break;
                    }
                }

                ImGuiFileDialog::Instance( )->Close( );
            }
            ++i;
        }
    }

    String RegeditGetPath ( const std::string& keyname ) -> std::string {
        HKEY hKey;
        RegOpenKeyExA( HKEY_CURRENT_USER, NULL, 0, KEY_SET_VALUE, &hKey );

        char buffer[ MAX_PATH ];
        DWORD dwBufferSize = sizeof( buffer );

        var status = RegQueryValueExA( hKey, keyname.c_str( ), NULL, NULL, (LPBYTE)buffer, &dwBufferSize );

        RegCloseKey( hKey );

        return std::string( buffer );
    }

    /// <summary>
/// this function is for displaying the project's flags editing window.
/// </summary>
    void formWindowFlag()
    {
        begin( "Style Window Editor", &m_style_menu );
        if ( button( "Export" ) )
        {
            im_config::window_flags::to_clipboard( customGUIStyle );
            MessageBoxA( nullptr, "Code exported to clipboard !", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
        }

        sameLine( );

        if (button("Load"))
        {
            openDialog("OpenFlagsDlgKey", "Open File", ".flags", RegeditGetPath( "ImGuiBuilderPath" ), "style_flags" );
        }

        sameLine();
        if (button("Save"))
        {
            openDialog("SaveFlagsDlgKey", "Save File", ".flags", RegeditGetPath( "ImGuiBuilderPath" ), "style_flags" );
        }

        text("First");
        if (sliderFloat( "FrameRounding", &customGUIStyle.FrameRounding, 0.0f, 12.0f, "%.0f" ) )
        customGUIStyle.getGrabRounding() = customGUIStyle.FrameRounding;
        // Make GrabRounding always the same value as FrameRounding
        {
            var border = (customGUIStyle.getWindowBorderSize() > 0.0f );
            if (checkbox("WindowBorder", &border ) ) { customGUIStyle.WindowBorderSize = border ? 1.0f : 0.0f; }
        }
        sameLine( );
        {
            var border = ( customGUIStyle.FrameBorderSize > 0.0f );
            if ( checkbox( "FrameBorder", &border ) ) { customGUIStyle.FrameBorderSize = border ? 1.0f : 0.0f; }
        }
        sameLine( );
        {
            var border = ( customGUIStyle.PopupBorderSize > 0.0f );
            if ( checkbox( "PopupBorder", &border ) ) { customGUIStyle.PopupBorderSize = border ? 1.0f : 0.0f; }
        }

        text("Main");
        sliderFloat2( "WindowPadding",		reinterpret_cast<float*>( &customGUIStyle.WindowPadding		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "FramePadding",		reinterpret_cast<float*>( &customGUIStyle.FramePadding		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "CellPadding",			reinterpret_cast<float*>( &customGUIStyle.CellPadding		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "ItemSpacing",			reinterpret_cast<float*>( &customGUIStyle.ItemSpacing		), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "ItemInnerSpacing",	reinterpret_cast<float*>( &customGUIStyle.ItemInnerSpacing	), 0.0f, 20.0f, "%.0f" );
        sliderFloat2( "TouchExtraPadding",	reinterpret_cast<float*>( &customGUIStyle.TouchExtraPadding ), 0.0f, 10.0f, "%.0f" );
        sliderFloat( "IndentSpacing",		&customGUIStyle.IndentSpacing,		0.0f, 30.0f, "%.0f" );
        sliderFloat( "ScrollbarSize",		&customGUIStyle.ScrollbarSize,		1.0f, 20.0f, "%.0f" );
        sliderFloat( "GrabMinSize",			&customGUIStyle.GrabMinSize,		1.0f, 20.0f, "%.0f" );
        text( "Borders" );
        sliderFloat( "WindowBorderSize",		&customGUIStyle.WindowBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "ChildBorderSize",		&customGUIStyle.ChildBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "PopupBorderSize",		&customGUIStyle.PopupBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "FrameBorderSize",		&customGUIStyle.FrameBorderSize,	0.0f, 1.0f, "%.0f" );
        sliderFloat( "TabBorderSize",		&customGUIStyle.TabBorderSize,		0.0f, 1.0f, "%.0f" );
        text( "Rounding" );
        sliderFloat( "WindowRounding",		&customGUIStyle.WindowRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "ChildRounding",		&customGUIStyle.ChildRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "FrameRounding",		&customGUIStyle.FrameRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "PopupRounding",		&customGUIStyle.PopupRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "ScrollbarRounding",	&customGUIStyle.ScrollbarRounding,	0.0f, 12.0f, "%.0f" );
        sliderFloat( "GrabRounding",			&customGUIStyle.GrabRounding,		0.0f, 12.0f, "%.0f" );
        sliderFloat( "LogSliderDeadzone",	&customGUIStyle.LogSliderDeadzone,	0.0f, 12.0f, "%.0f" );
        sliderFloat( "TabRounding",			&customGUIStyle.TabRounding,		0.0f, 12.0f, "%.0f" );
        ImGui::Text( "Alignment" );
        sliderFloat2( "WindowTitleAlign",	reinterpret_cast<float*>( &customGUIStyle.WindowTitleAlign ), 0.0f, 1.0f, "%.2f" );
        var window_menu_button_position = customGUIStyle.WindowMenuButtonPosition + 1;
        if ( combo( "WindowMenuButtonPosition", static_cast<int*>( &window_menu_button_position ), "None\0Left\0Right\0" ) )
        customGUIStyle.WindowMenuButtonPosition = window_menu_button_position - 1;
        combo( "ColorButtonPosition",		static_cast<int*>( &customGUIStyle.ColorButtonPosition ), "Left\0Right\0" );
        sliderFloat2( "ButtonTextAlign",		reinterpret_cast<float*>( &customGUIStyle.ButtonTextAlign ), 0.0f, 1.0f, "%.2f" );

        sliderFloat2( "SelectableTextAlign", reinterpret_cast<float*>( &customGUIStyle.SelectableTextAlign ), 0.0f, 1.0f, "%.2f" );
        ImGui::Text( "Safe Area Padding" );
        sliderFloat2( "DisplaySafeAreaPadding", reinterpret_cast<float*>( &customGUIStyle.DisplaySafeAreaPadding ), 0.0f, 30.0f, "%.0f" );

        end( );
    }

    void imgui_builder::form_color_editor( )
    {
        var& style = ImGui::GetStyle( );

        setNextWindowSize( { 400.f, 500.f }, ImGuiCond_Once );

        begin( "Gui Builder color export/import ", &m_color_menu );

        if ( button( "Export" ) )
        {
            im_config::color::to_clipboard( customGUIStyle );
            MessageBoxA( nullptr, "Code exported to clipboard !", "ImGui Builder", MB_OK | MB_ICONINFORMATION );
        }

        sameLine( );

        if ( button( "Load" ) )
        {
            ImGuiFileDialog::Instance( )->OpenDialog( "OpenColorsDlgKey", "Open File", ".colors", RegeditGetPath( "ImGuiBuilderPath" ), "style_colors" );
        }

        sameLine( );
        if ( button( "Save" ) )
        {
            ImGuiFileDialog::Instance( )->OpenDialog( "SaveColorsDlgKey", "Save File", ".colors", RegeditGetPath( "ImGuiBuilderPath" ), "style_colors" );
        }


        static ImGuiTextFilter filter;
        filter.Draw( "Filter colors", ImGui::GetFontSize( ) * 16 );

        static var alpha_flags = 0;
        if ( radioButton( "Opaque", alpha_flags == ImGuiColorEditFlags_None ) )
        alpha_flags = ImGuiColorEditFlags_None;


        sameLine( );
        if ( radioButton( "Alpha", alpha_flags == ImGuiColorEditFlags_AlphaPreview ) )
        alpha_flags = ImGuiColorEditFlags_AlphaPreview;


        sameLine( );
        if ( radioButton( "Both", alpha_flags == ImGuiColorEditFlags_AlphaPreviewHalf ) )
        alpha_flags = ImGuiColorEditFlags_AlphaPreviewHalf;


        beginChild( "##colors", ImVec2( 0, 0 ), true,
            ImGuiWindowFlags_AlwaysVerticalScrollbar | ImGuiWindowFlags_AlwaysHorizontalScrollbar |
                    ImGuiWindowFlags_NavFlattened );

        pushItemWidth( -160 );
        for ( var i = 0; i < ImGuiCol_COUNT; i++ )
        {
		const var* name = ImGui::GetStyleColorName( i );
            //std::cout << "Name: " << name << " Index: " << i << std::endl;
            if ( !filter.PassFilter( name ) )
                continue;
            pushID( i );
            colorEdit4( "##color", reinterpret_cast<float*>( &customGUIStyle.Colors[ i ] ),
            ImGuiColorEditFlags_AlphaBar | alpha_flags );
            if ( memcmp( &customGUIStyle.Colors[ i ], &im_config::color::saved_colors()[ i ], sizeof( ImVec4 ) ) != 0 )
            {
                sameLine( 0.0f, style.ItemInnerSpacing.x );
                if ( button( "Save" ) ) { im_config::color::saved_colors( )[ i ] = customGUIStyle.Colors[ i ]; }
                sameLine( 0.0f, style.ItemInnerSpacing.x );
                if ( button( "Revert" ) ) { customGUIStyle.Colors[ i ] = im_config::color::saved_colors( )[ i ]; }
            }
            sameLine( 0.0f, style.ItemInnerSpacing.x );
            ImGui::TextUnformatted( name );
            popID( );
        }
        popItemWidth( );
        endChild( );

        end( );
    }


    void formFontEditor( )
    {
        begin( "Font Editor", &m_font_menu);

        if (button("Import font from file")) {

        }

        text("Current font: " + "123")).c_str( ) );

        end();
    }


    private void draw() {
        pushAllColorsDark( m_dark_style );
        int width = 1280;
        RECT rect;
        if ( GetWindowRect( window::i()->get_win32_window(), &rect ) )
        {
            width = rect.right - rect.left;
        }

        draw_dialogs_save_open( );

        if (colorMenu)
            form_color_editor();

        if (styleMenu)
            form_window_flag( );

        if (fontMenu)
            form_font_editor( );

        paste_obj( );

        setNextWindowSize( { static_cast<float>( width - 16 ), 100 } );
        setNextWindowPos( { 0, 0 } );
        begin( "BUILDER", null, NoBringToFrontOnFocus | MenuBar);
        m_my_forms_active = isWindowFocused();
        if (beginMenuBar()) {
            if (beginMenu( "Project")){
                if (menuItem("Save")) {
                    openDialog( "SaveProjectFileDlgKey", "Save File", ".builder", RegeditGetPath( "ImGuiBuilderPath" ), "project" );
                }

                if ( menuItem( "Open" ) )
                {
                    openDialog( "OpenProjectFileDlgKey", "Open File", ".builder", RegeditGetPath( "ImGuiBuilderPath" ), "project" );
                }

                if ( menuItem( "Generate Code" ) )
                {
                    openDialog( "GenCodeProjectFileDlgKey", "Open File", ".cpp,.h,.hpp", RegeditGetPath( "ImGuiBuilderPath" ), "imgui_builder" );
                }

                endMenu( );
            }
            if ( beginMenu( "Editor" ) )
            {
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

        if (button("New Form"))
        {
            createForm();
        }
        sameLine();
        if (button("New Child")) {
            createChild();
        }
        sameLine();
        if (button("New Button")) {
            createObject( 1 );
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
        popAllColorsCustom( );

        pushAllColorsCustom(customGUIStyle);

        showForm();

        popAllColorsCustom( );

        end( );
    }

    private void createForm( ) {
        id++;
        GuiBuilderClasses.Form frm = new GuiBuilderClasses.Form();
        frm.id = id;
        frm.name = "form" + id;
        frm.size = new ImVec2(50.f, 50.f);
        forms.addLast(frm);
    }

    void createChild() {
        if (forms.isEmpty()) {
            return;
        }

        childID = forms.get(id).child.size();
        GuiBuilderClasses.Child child = new GuiBuilderClasses.Child();
        child.id = childID;
        child.name = "child" + childID;
        child.father = id;
        child.border = true;
        child.size = new ImVec2(50,50);
        child.pos = new ImVec2(15,15);
        forms.get(id).child.addLast(child);
    }

    void createObject(int type)
    {
        if (forms.isEmpty()) {
            return;
        }

        objID++;
        var name = getNameType(type);

        name += objID;

        var child_id = [ & ] ( ) -> int {
        for ( var& chl : m_forms[ m_active_window_id ].child )
        if ( chl.selected )
            return chl.id;
        return -1;
    };

	const basic_obj new_obj = { m_obj_id, m_active_window_id, child_id(), name, type, { }, { 30, 30 } };
        //form_[id_].obj_render_me.push_back(new_obj);
        m_objs.push_back( new_obj );
    }

    private void pasteOBJ( ) //NOT NEED  OVERLOAD FOR THAT!
    {
        if ( m_my_forms_active ) return;
        if ( window::i( )->pressed_bind_keys( VK_LCONTROL, 'V' )  )
        {
		const var* psz_text = ImGui::GetClipboardText( );

		const std::string text( psz_text );
            var m_copy = utils::split( text, '\n' );
            ImVec2 pos = { 30, 30 };
            for ( const auto& n_text : m_copy )
            {
                auto o = utils::split( n_text, ',' );

                if ( o.size( ) != 7 )
                    return;

                for ( auto tx : o )
                {
                    if ( !utils::is_number( tx ) )
                    return;
                }

                auto name = get_name_type( std::stoi( o[ 0 ] ) );

                if ( std::stoi( o[ 0 ] ) == 10 )
                {
                    m_child_id = static_cast<int>( m_forms[ m_active_window_id ].child.size( ) );
                    m_forms[ m_active_window_id ].child.push_back( {
                            m_child_id, "child" + std::to_string( m_child_id ), m_active_window_id,
                            std::stoi( o[ 4 ] ) != 0, { std::stof( o[ 2 ] ), std::stof( o[ 3 ] ) },
                            { std::stof( o[ 4 ] ), std::stof( o[ 5 ] ) }
                    } );
                    std::cout << "child obj\n";
                }
			else if ( !name.empty( ) )
            {
                m_obj_id++;
                name += std::to_string( m_obj_id );
				const basic_obj new_obj = {
                    m_obj_id, m_active_window_id, std::stoi( o[ 1 ] ), name, std::stoi( o[ 0 ] ), { std::stof( o[ 3 ] ), std::stof( o[ 4 ] ) },
                    { std::stof( o[ 5 ] ), std::stof( o[ 6 ] ) }
            };
                m_objs.push_back( new_obj );
                System.out.println("paste obj\n");
            }
                pos.x += 15;
                pos.y += 15;
            }
        }
    }

    private void copyOBJ(int type, int child, ImVec2 size, ImVec2 pos, boolean border, boolean selected, boolean pass_key_check ) {
        if ((!m_my_forms_active && window::i( )->pressed_bind_keys(VK_CONTROL, 'C' ) ) || pass_key_check) {
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
        for (GuiBuilderClasses.Form form : forms) {
            setNextWindowSize(form.size);
            //	if any other obj is moving position
            //freezer form because if obj dont have much area for hover like label form move too
            //

            if (form.deleteMe)
            {
                deleteForm(form.id);
                break;
            }
            begin(form.name, null, movingObj ? (NoCollapse | NoMove) : (NoCollapse));

            form.pos = getWindowPos(); // get value position form
            form.size = getWindowSize(); // get value size form

            // get propriety of form with user double click mouse
            if ( isWindowHovered( ) && isMouseDoubleClicked( 0 ) )
            {
                name = form.name;
                currentItem = form.name + ":" + form.id;
                family = form.id;
                type = 0;
            }
            m_my_forms_active = !isWindowFocused();
            if ( isWindowFocused( ) || isWindowAppearing( ) || isWindowHovered( ) )
            {
                activeWindowID = form.id;
            }

            for (GuiBuilderClasses.BasicOBJ obj : objs) {
                if ( obj.form == form.id && obj.child < 0 ) {
                    renderOBJ(obj, form.id);
                }
            }
            for (GuiBuilderClasses.Child container : form.child) {
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

                    form.child.erase( form.child.begin( ) + container.id );
                    childID = form.child.size( ) - 1;

                    for (int newID = container.id - 1; newID < form.child.size( ); ++newID ) {
                        form.child.get(newID).name = ("child" + newID);
                        form.child.get(newID).id = newID;
                    }
                    break;
                }

                boolean hover = false;

                var normal_select = (currentItem == ( container.name + ":" + container.id ));

                beginChild( container.name, container.size, container.border );

                boolean scrollEnableY = getScrollMaxY( ) > 0.f;
                float scrollPosY = getScrollY();

                for (GuiBuilderClasses.BasicOBJ obj : objs) {
                    if ( obj.form == form.id && obj.child == container.id ) {
                        renderOBJ( obj, form.id );

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
                container.hover = my_IsItemHovered( container.pos, container.size, 5.f ) && !hover;
                //if ( container.hover && limit_bordering_control( container.pos, container.size, -15.f ) != resize_opt::off ) //I don't know if you were good with that
                //	container.hover = false;

                auto left_clicked = isMouseClicked( 0, false );
                auto right_clicked = isMouseClicked( 1, false );
                auto show_context = normal_select;

                // thats is shame but... work good....
                if ( container.hover && ( left_clicked || right_clicked ) && show_context == false )
                {
                    show_context = !left_clicked;
                    if ( GetKeyState( VK_CONTROL ) & 0x8000 )
                        container.selected = !container.selected;
                    else
                    {
                        for ( auto& o_obj : m_objs )
                        o_obj.selected = false;
                    }
                    currentItem	= container.name + ':' + form.id;
                    family = container.father;
                    index = container.id;
                    type = 10;
                }
                if (container.hover) {
                    SetCursor( this->cursor.m_arrow_all );
                }

                container.selected = ( m_current_item == ( container.name + ":" + std::to_string( form.id ) ) );
                if ( container.selected && form.id == m_active_window_id )
                {
                    ImGui::DrawObjBorder( container.pos, container.size );
                }
                if ( !container.locked )
                    resize_obj( container.pos, container.size, container.hover, container.selected );

                pushAllColorsDark( m_dark_style );

                if ( show_context && beginPopupContextItem( "##obj_context" ) )
                {
                    //style.ButtonTextAlign
                    auto& g = *GImGui;
                    auto backup1_y = g.Style.ButtonTextAlign.y;
                    auto backup2_y = g.Style.FramePadding.y;
                    g.Style.FramePadding.y = -1.3f;
                    g.Style.ButtonTextAlign.y = 0.f;
                    ImVec2 btn_size = { 60.f, 12.f };
                    if ( button( "delete", btn_size ) )
                    {
                        container.delete_me = true;
                        m_current_item = "";
                        m_type = -1;
                    }
                    //if ( button( "copy", btn_size ) )
                    //{
                    //	copy_obj( container.my_type, obj.child, obj.size, obj.pos, false, false, true );
                    //}
                    checkbox( "lock", &container.locked );
                    g.Style.ButtonTextAlign.y = backup1_y;
                    g.Style.FramePadding.y = backup2_y;
                    endPopup( );
                }

                popAllColorsCustom( );
            }
            end( );
        }
    }

    private void deleteForm(int formID)
    {
        forms.erase( m_forms.begin( ) + form_id );
        id = forms.size( ) - 1;
        std::cout << "size id " << m_id << std::endl;

        if ( !m_objs.empty( ) && !m_forms.empty( ) )
        {
            for ( auto i = m_objs.size( ) - 1; i > -1; --i )
            {
                std::cout << i << std::endl;
                if ( m_objs[ i ].form == form_id )
                {
                    std::cout << "Obj id: " << m_objs[ i ].id << " \t form id: " << m_objs[ i ].form << std::endl;
                    m_objs.erase( m_objs.begin( ) + i );
                    m_obj_id = m_objs.size( ) - 1;

                    for ( auto x = i - 1; x < m_objs.size( ); ++x )
                        m_objs[ x ].id = x;
                }
            }

            std::cout << "delete file finish\n";
        }
        else
        {
            m_objs.clear( );
            m_obj_id = -1;
            std::cout << "delete all\n";
        }


        for ( auto id = ( form_id - 1 ); m_id >= id; ++id )
        {
            if ( id < 0 )
                continue;

            for ( auto& obj : m_objs )
            {
                if ( obj.form == form_id )
                    obj.delete_me = true;

                if ( obj.form == m_forms[ id ].id )
                    obj.form = id;
            }
            m_forms[ id ].id = id;
        }
    }

    void imgui_builder::resize_obj( basic_obj& current_obj, bool selected )
    {
        if ( current_obj.locked ) return;
        resize_obj( current_obj.pos, current_obj.size_obj, current_obj.hover, selected );
        current_obj.size = current_obj.size_obj;
    }

    void resizeOBJ(ImVec2 objPos, ImVec2 objSize, boolean hover, boolean selected)
    {
        //if ( obj.my_type != button ) return;
        //printf( "pos { %.f, %.f }, size { %.f, %.f }, hover %d, selected %d\n", obj_pos.x, obj_pos.y, obj_size.x, obj_size.y, hover, selected );
        auto resze_opt = resize_opt::off;

        bool scrollEnableY = ImGui::GetScrollMaxY( ) > 0.f;
        auto scrollPosY = ImGui::GetScrollY( );

        if ( hover && !m_tick_resize )
        {
            ImVec2 oldPos = obj_pos;

            if ( scrollEnableY )
            {
                obj_pos.y -= scrollPosY;
            }

            resze_opt = limit_bordering_control( obj_pos, obj_size, 3.f );

            obj_pos = oldPos;

            switch ( resze_opt )
            {
                case resize_opt::bottom_right:
                case resize_opt::top_left:
                    this->cursor.m_current_icon	= this->cursor.m_arrow_northwest_and_southeast;
                    break;
                case resize_opt::top_right:
                case resize_opt::bottom_left:
                    this->cursor.m_current_icon	= this->cursor.m_arrow_northeast_and_southwest;
                    break;
                case resize_opt::top:
                case resize_opt::bottom:
                    this->cursor.m_current_icon	= this->cursor.m_arrow_top_or_bottom;
                    break;
                case resize_opt::left:
                case resize_opt::right:
                    this->cursor.m_current_icon	= this->cursor.m_arrow_left_or_right;
                    break;
                default:
                    break;
            }
            if ( resze_opt != resize_opt::off )
            {
                SetCursor( this->cursor.m_current_icon );
            }
        }

        m_no_move = (m_tick_resize ) || ( resze_opt != resize_opt::off );

        //printf( "pos { %.f, %.f }, size { %.f, %.f }, hover %d, selected %d, moving %d\n", obj_pos.x, obj_pos.y, obj_size.x, obj_size.y, hover, selected, g_moving_obj );
        if ( g_moving_obj || !window::i( )->holding_key( VK_LBUTTON )  )
        {
            resizeObj = false;
            m_tick_resize	= 0;
            m_resize_opt	= resize_opt::off;
            //m_in_resize_id	= 0;
            return;

        }

        if ( !selected  ) return;
        auto current_pos	= window::i( )->get_relative_cursor_pos( );
        auto tick_now		= GetTickCount64( );

        if ( m_tick_resize == 0 && resze_opt != resize_opt::off )
        {
            m_resize_opt	= resze_opt;
            m_tick_resize	= tick_now + 80;
            //m_in_resize_id	= obj.id + obj.my_type;
            return;
        }


        //printf( "pos { %.f, %.f }, size { %.f, %.f }, hover %d, selected %d\n", obj_pos.x, obj_pos.y, obj_size.x, obj_size.y, hover, selected );

        if ( m_tick_resize && tick_now > m_tick_resize )
        {
            resizeObj = true;
            auto current_win_pos	= ImGui::GetWindowPos( );
            SetCursor( this->cursor.m_current_icon );

            auto normalize_diff		= []( float diff, float val = 100.f ) -> float //at some point it retains an exorbitant value, it will help to control that
            {
                if ( diff > val )
                    diff = val;
                else if ( diff < -val )
                    diff = -val;
                return diff;
            };

            auto basic_margins = [&]( resize_opt rs_opt ) -> void
            {
                switch ( rs_opt )
                {
                    case resize_opt::right:
                    {
                        auto end_pos_x	= current_win_pos.x + obj_pos.x + obj_size.x;
                        auto dif		= normalize_diff( current_pos.x - end_pos_x );
                        obj_size.x		+= dif;
                        break;
                    }
                    case resize_opt::left:
                    {
                        auto end_pos_x	= current_win_pos.x + obj_pos.x;
                        auto dif		= normalize_diff( end_pos_x - current_pos.x );
                        obj_pos.x		-= dif;
                        obj_size.x		+= dif;
                        break;
                    }
                    case resize_opt::top:
                    {
                        auto end_pos_y	= ( current_win_pos.y + obj_pos.y ) - scrollPosY;
                        auto dif		= normalize_diff( end_pos_y - current_pos.y );
                        obj_pos.y		-= dif;
                        obj_size.y		+= dif;
                        break;
                    }
                    case resize_opt::bottom:
                    {
                        auto end_pos_y	= current_win_pos.y + obj_pos.y + obj_size.y;
                        auto dif		= normalize_diff( current_pos.y - end_pos_y );
                        obj_size.y		+= dif;
                        break;
                    }
                    default:
                        break;
                }
            };

            switch ( m_resize_opt )
            {
                case resize_opt::bottom_right:
                {
                    basic_margins( resize_opt::bottom );
                    basic_margins( resize_opt::right );
                    break;
                }
                case resize_opt::top_left:
                {
                    basic_margins( resize_opt::top );
                    basic_margins( resize_opt::left );
                    break;
                }
                case resize_opt::top_right:
                {
                    basic_margins( resize_opt::top );
                    basic_margins( resize_opt::right );
                    break;
                }
                case resize_opt::bottom_left:
                {
                    basic_margins( resize_opt::bottom );
                    basic_margins( resize_opt::left );
                    break;
                }
                default:
                    basic_margins( m_resize_opt );
                    break;
            }
        }

    }

    void renderOBJ(GuiBuilderClasses.BasicOBJ obj, int currentFormID)
    {
        // set pos for next obj render
        setCursorPos(obj.pos);

        // if signal of delete object
        if (obj.deleteMe)
        {
            // delete obj
            if (obj.id < m_obj_id)
            {
                m_objs.erase(m_objs.begin() + obj.id);
                // reform id objs
                m_obj_id = m_objs.size() - 1;
            }

            // previous object, before rendering the others
            for ( auto new_id = obj.id - 1; new_id < m_objs.size( ); ++new_id )
            {
                m_objs[ new_id ].id = new_id;
                //std::cout << obj_render_me[new_id].id << std::endl;
            }
            return; //not to render the object
        }

        // To type any obj for render set in case
        // to render all obj for 1 time in loop

        // buffer for inputs
        std::string		buffer			= "text here";
        int				value_i			= 0;
        float			value_f			= 0;
        static bool		true_bool		= false;
        auto			normal_select	= ( m_current_item == ( obj.name + ":" + std::to_string( obj.id ) ) );


        auto relative_for_resize = []( basic_obj& obj ) -> float
        {
            auto&		g			= *GImGui;
            auto*		window		= g.CurrentWindow;
		const auto&	style		= g.Style;
		const auto	id			= window->GetID( obj.name.c_str( ) );
		const auto	label_size	= calcTextSize( obj.name.c_str( ), nullptr, true );
		const auto	frame_size	= calcItemSize( ImVec2( 0, 0 ), calcItemWidth( ), ( label_size.y ) + style.FramePadding.y * 2.0f );
		const auto	label_dif	= ( label_size.x > 0.0f ? style.ItemInnerSpacing.x + label_size.x : 0.0f );
            if ( obj.size.x == 0.f && obj.size.y == 0.f )
                obj.size = ImVec2( frame_size.x + label_dif, frame_size.y );
            return obj.size.x - label_dif;
        };

        // render obj
        switch ( obj.my_type )
        {
            case 1:
                button( obj.name.c_str( ), obj.size );
                break;
            case 2:
                ImGui::Text( obj.name.c_str( ) );
                break;
            case 3:
            {
                pushItemWidth(relative_for_resize(obj));
                inputText( obj.name.c_str( ), const_cast<char*>( buffer.c_str( ) ), 254 );
                popItemWidth( );
                break;
            }
            case 4:
            {
                pushItemWidth( relative_for_resize( obj ) );
                sliderInt( obj.name.c_str( ), &value_i, 0, 100 );
                popItemWidth( );
                break;
            }
            case 5:
            {
                pushItemWidth( relative_for_resize( obj ) );
                sliderFloat( obj.name.c_str( ), &value_f, 0, 100 );
                popItemWidth( );
                break;
            }
            case 6:
                checkbox( obj.name.c_str( ), &true_bool );

                break;
            case 7:
                radioButton( obj.name.c_str( ), true_bool );

                break;
            case 8:
                ImGui::ToggleButton( obj.name.c_str( ), &true_bool );

                break;
            default:
                break;
        }
        obj.size_obj		= ImGui::GetItemRectSize( );

        bool scrollEnableY	= ImGui::GetScrollMaxY( ) > 0.f;

        auto scrollPosY		= ImGui::GetScrollY( );

        ImVec2 oldPos = obj.pos;

        if ( scrollEnableY )
        {
            obj.pos.y -= scrollPosY;
        }

        if ( ( obj.selected || normal_select ) && current_form_id == m_active_window_id )
        {
            ImGui::DrawObjBorder( obj );
        }

        // get size and hover of object
        //obj.hover		= isItemHovered( );
        obj.hover		= my_IsItemHovered( obj.pos, obj.size_obj, 5.f );

        // Set family and type child etc for propri and execution modification on type
        auto left_clicked	= isMouseClicked( 0, false );
        auto right_clicked	= isMouseClicked( 1, false );
        auto show_context	= normal_select;
        if ( obj.hover && (left_clicked || right_clicked) && show_context == false )
        {
            show_context = !left_clicked;
            if ( GetKeyState( VK_CONTROL ) & 0x8000 )
                obj.selected = !obj.selected;
            else
            {
                for ( auto& o_obj : m_objs )
                o_obj.selected = false;
            }

            m_current_item	= obj.name + ':' + std::to_string( obj.id );
            m_family		= obj.form;
            m_grandchild	= obj.child;
            m_index			= obj.id;
            m_type			= obj.my_type;
        }

        pushAllColorsDark(m_dark_style);

        if ( show_context && beginPopupContextItem( "##obj_context" ) )
        {
            //style.ButtonTextAlign
            auto& g = *GImGui;
            auto backup1_y = g.Style.ButtonTextAlign.y;
            auto backup2_y = g.Style.FramePadding.y;
            g.Style.FramePadding.y		= -1.3f;
            g.Style.ButtonTextAlign.y	= 0.f;
            ImVec2 btn_size				= { 60.f, 12.f };
            if ( button( "delete", btn_size ) )
            {
                obj.delete_me	= true;
                m_current_item	= "";
                m_type			= -1;
            }
            if ( button( "copy", btn_size ) )
            {
                copy_obj( obj.my_type, obj.child, obj.size, obj.pos, false, false, true );
            }
            checkbox( "lock", &obj.locked );
            g.Style.ButtonTextAlign.y	= backup1_y;
            g.Style.FramePadding.y		= backup2_y;
            endPopup( );
        }

        popAllColorsCustom( );

        if ( obj.hover )
            SetCursor( this->cursor.m_arrow_all );

        obj.pos = oldPos;

        resize_obj( obj, normal_select  );
    }

    void objectProperty() {
        static std::vector<move_obj> mto{ };
        setNextWindowPos(0, 100);
        setNextWindowSize(300, 700 - 100);
        begin( "property", null, NoBringToFrontOnFocus);
        m_my_forms_active = isWindowFocused();
        if ( beginCombo( "##itens", m_current_item.c_str( ) ) )
        {
            // list all obj render in array child and form

            for ( auto& n : m_forms )
            {
                if ( n.delete_me )
                {
                    break;
                }

                // it is simply possible to simplify this please do this
                auto item = n.name + ":" + std::to_string( n.id );
			const auto is_selected = ( m_current_item == item );

                if ( selectable( item.c_str( ), is_selected ) )
                {
                    m_name = const_cast<char*>( n.name.c_str( ) );
                    m_current_item = item;
                    m_type = 0;
                    m_family = n.id;
                }

                for ( auto& c : n.child )
                {
                    item = c.name + ":" + std::to_string( n.id );
                    if ( selectable( item.c_str( ), is_selected ) )
                    {
                        m_family		= n.id;
                        m_index			= c.id;
                        m_type			= 10;
                        m_current_item	= item;
                    }
                }

                item = "";

                if ( is_selected )
                    setItemDefaultFocus( );
            }

            for ( auto& o : m_objs )
            {
                if ( o.delete_me )
                {
                    break;
                }

                auto item = o.name + ":" + std::to_string( o.id );
			const auto is_selected = ( m_current_item == item );

                item = o.name + ":" + std::to_string( o.id );
                if ( selectable( item.c_str( ), is_selected ) )
                {
                    m_family		= o.form;
                    m_grandchild	= o.child;
                    m_index			= o.id;
                    m_type			= o.my_type;
                    m_current_item	= item;
                }

                if ( is_selected )
                    setItemDefaultFocus( );
            }

            endCombo( );
        }

        // vars for simplification functions less line length
        child		chl{ };
        basic_obj	obj{ };
        form		fm{ };

        switch ( m_type )
        {
            case -1: // none

                break;

            case 0: // form
                fm = m_forms[ m_family ];
                inputInt( "ID", &fm.id, 0 );

                inputTextEx( "Name form", &m_name, 0 );
                //inputText("name form", name, 255);
                if ( button( "Apply name" ) )
            {
                if ( !m_name.empty( ) ) fm.name = m_name;
            }
            inputFloat( "SizeX", &fm.size.x, 1 );
            inputFloat( "SizeY", &fm.size.y, 1 );
            inputFloat( "PosX", &fm.pos.x, 1 );
            inputFloat( "PosY", &fm.pos.y, 1 );

            if ( button( "DELETE" ) || window::i( )->pressed_key( VK_DELETE ) )
            {
                fm.delete_me	= true;
                m_current_item	= "";
                m_type			= -1;
            }

            m_forms[ m_family ] = fm;
            break;

            case 10: // child
                chl = m_forms[ m_family ].child[ m_index ];
                m_form_pos = m_forms[ m_family ].pos;
                inputInt( "ID", &chl.id, 0 );

                if ( inputInt( "Form Father", &chl.father, 1 ) )
            {
                m_forms[ chl.father ].child.push_back( chl );
                chl.delete_me	= true;
                m_current_item	= "";
                m_type			= -1;
            }

            inputFloat( "SizeX", &chl.size.x, 1 );
            inputFloat( "SizeY", &chl.size.y, 1 );
            inputFloat( "PosX", &chl.pos.x, 1 );
            inputFloat( "PosY", &chl.pos.y, 1 );
            checkbox( "Border", &chl.border );
            sameLine( );
            checkbox( "Lock", &chl.locked );
            m_item_size = chl.size;
            if ( chl.hover && !chl.locked )
                chl.change_pos = true;

            if ( !m_no_move )
                move_item( chl.pos, chl.change_pos );

            copy_obj( 10, 0, chl.size, chl.pos, chl.border, obj.selected );

            if ( button( "DELETE" ) || window::i( )->pressed_key( VK_DELETE ) )
            {
                chl.delete_me	= true;
                m_current_item	= "";
                m_type			= -1;
            }

            m_forms[ m_family ].child[ m_index ] = chl;
            break;
            default: // another obj

                if ( m_grandchild > -1 )
                {
                    obj = m_objs[ m_index ];
                    m_form_pos	= m_forms[ m_family ].child[ m_grandchild ].pos;
                    m_form_pos.x += m_forms[ m_family ].pos.x;
                    m_form_pos.y += m_forms[ m_family ].pos.y;
                }
                else
                {
                    if ( m_objs.size( ) > static_cast<unsigned>( m_index ) )
                    {
                        obj = m_objs[ m_index ];
                        m_form_pos = m_forms[ m_family ].pos;
                    }
                }

                m_item_size = obj.size_obj;
                inputInt( "ID", &obj.id, 0 );
                inputInt( "Form Father", &obj.form, 1 );
                inputInt( "Child Father", &obj.child, 1 );
                inputTextEx( "Name", &obj.name, 0 );
                //inputText("Name", name, 255);
                inputFloat( "PosX", &obj.pos.x, 1, 1 );
                inputFloat( "PosY", &obj.pos.y, 1, 1 );
                inputFloat( "SizeX", &obj.size.x, 1, 1 );
                inputFloat( "SizeY", &obj.size.y, 1, 1 );
                checkbox( "Lock", &obj.locked );

                //obj.name = name;
                // check if hover because need for change position
                if ( obj.hover )
                    obj.change_pos = true;

                if ( obj.selected && !m_no_move )
                {
                    mto.clear( );
                    for ( auto& r_obj : m_objs )
                    {
                        if ( r_obj.selected == true && !r_obj.locked )
                        {
                            mto.push_back( { r_obj.id, r_obj.pos } );
                        }
                    }

                    move_items( mto, obj.change_pos );

                    for ( auto& teste : mto )
                    {
                        if ( obj.id == teste.index )
                            obj.pos = teste.pos;
                        else
                            m_objs[ teste.index ].pos = teste.pos;
                    }
                }
                else if ( !m_no_move )
                    if (!obj.locked )
                        move_item( obj.pos, obj.change_pos );

                copy_obj( obj.my_type, obj.child, obj.size, obj.pos, false, obj.selected );

                // dont delete here!
                if ( button( "DELETE" ) || window::i( )->pressed_key( VK_DELETE ) )
            {
                obj.delete_me	= true;
                m_current_item	= "";
                m_type			= -1;
            }

            m_objs[ m_index ] = obj;

            break;
        }

        end( );
    }

    void imgui_builder::routine_draw( )
    {
        static imgui_builder* instance = nullptr;
        if ( !instance )
            instance = new imgui_builder( );
        instance->draw( );
    }

    void move_items( std::vector<move_obj>& mto, bool& continue_edt )
    {
        static POINT old_pos = { 0, 0 };
        static std::vector<ImVec2> old_pos_obj{ };
        if ( !window::i( )->holding_key( VK_LBUTTON ) || !continue_edt )
        {
            old_pos_obj.clear( );
            g_moving_obj	= false;
            continue_edt	= false;
            old_pos			= { 0, 0 };
            tickMove = 0;
            return;
        }
        auto tick_now		= GetTickCount64( );
        auto current_pos	= window::i( )->get_relative_cursor_pos( );
        if ( tickMove == 0 )
        {
            tickMove = tick_now + 100;
            old_pos			= current_pos;
            for ( auto& i : mto )
            old_pos_obj.push_back( i.pos );
            return;
        }
        if ( mto.size( ) != old_pos_obj.size( ) )
        {
            old_pos_obj.clear( );
            g_moving_obj	= false;
            continue_edt	= false;
            old_pos			= { 0, 0 };
            tickMove = 0;
            return;
        }
        if ( tick_now > tickMove)
        {
            g_moving_obj	= true;
            auto i			= 0;
            for ( auto& m : mto )
            {
			const auto x_pos = old_pos_obj[ i ].x + current_pos.x - old_pos.x;
			const auto y_pos = old_pos_obj[ i ].y + current_pos.y - old_pos.y;
                m.pos.x = x_pos;
                m.pos.y = y_pos;
                ++i;
            }
        }
        return;
    }

    void move_item( ImVec2& obj_pos, bool& continue_edt )
    {
        static ImVec2 old_pos{ };
        if ( !window::i( )->holding_key( VK_LBUTTON ) || !continue_edt  )
        {
            old_pos				= { 0, 0 };
            g_moving_obj		= false;
            continue_edt		= false;
            tickMove = 0;
            return;
        }
        auto tick_now			= GetTickCount64( );
        auto current_pos		= window::i( )->get_relative_cursor_pos( );
        if ( tickMove == 0 )
        {
            old_pos				= { current_pos.x - obj_pos.x, current_pos.y - obj_pos.y };
            tickMove = tick_now + 50;
            return;
        }
        auto current_win_pos = ImGui::GetWindowPos( );
        if ( tick_now > tickMove)
        {
            g_moving_obj		= true;
		const auto x_pos	=  current_pos.x - old_pos.x;
		const auto y_pos	=  current_pos.y - old_pos.y;
            obj_pos.x = x_pos;
            obj_pos.y = y_pos;
        }
    }
}
