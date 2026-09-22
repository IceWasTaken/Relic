package net.ice.curio.input;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import net.ice.curio.input.enums.Key;
import net.ice.curio.input.enums.Action;
import net.ice.curio.input.enums.MouseButton;
import net.ice.curio.input.event.CursorEnterEvent;
import net.ice.curio.input.event.CursorEvent;
import net.ice.curio.input.event.KeyEvent;
import net.ice.curio.input.event.MouseButtonEvent;
import net.ice.curio.library.glfw.events.*;
import net.ice.heirloom.event.EventListener;
import net.ice.heirloom.event.EventManager;
import org.joml.Vector2f;

import java.util.HashSet;
import java.util.Set;

public class Input {

    private static boolean inWindow = false;

    private static final Set<Key> keysDown = new HashSet<>();
    private static final Set<MouseButton> mouseButtonsDown = new HashSet<>();

    private static final Vector2f scroll = new Vector2f();
    private static final Vector2f mouseDelta = new Vector2f();
    private static final Vector2f mousePosition = new Vector2f();
    private static final Vector2f prevMousePosition = new Vector2f();

    private static Input input;

    public static Input getInstance() {
        if(input == null) {
            input = new Input();
        }
        return input;
    }

    public static void update() {
        mouseDelta.x = 0;
        mouseDelta.y = 0;
        if(prevMousePosition.x > 0 && prevMousePosition.y > 0 && inWindow) {
            float deltaX = mousePosition.x - prevMousePosition.x;
            float deltaY = mousePosition.y - prevMousePosition.y;
            if(deltaX != 0) {
                mouseDelta.x = deltaX;
            }
            if(deltaY != 0) {
                mouseDelta.y = deltaY;
            }
        }
        prevMousePosition.x = mousePosition.x;
        prevMousePosition.y = mousePosition.y;

    }

    @EventListener
    public static void onKeyEvent(KeyEvent keyEvent) {
        if(keyEvent.action() == Action.PRESS) {
            keysDown.add(keyEvent.key());
        } else if(keyEvent.action() == Action.RELEASE) {
            keysDown.remove(keyEvent.key());
        }

        ImGuiIO io = ImGui.getIO();
        if (!io.getWantCaptureKeyboard()) {
            return;
        }
        if (keyEvent.action() == Action.PRESS) {
            io.addKeyEvent(getImKey(keyEvent.key()), true);
        } else if (keyEvent.action() == Action.RELEASE) {
            io.addKeyEvent(getImKey(keyEvent.key()), false);
        }
    }

    @EventListener
    public static void onScrollEvent(ScrollEvent event) {
        scroll.x = (float) event.getXoffset();
        scroll.y = (float) event.getYoffset();
    }

    @EventListener
    public static void onCursorEvent(CursorEvent event) {
        mousePosition.x = (float) event.xpos();
        mousePosition.y = (float) event.ypos();
    }

    @EventListener
    public static void onMouseButtonEvent(MouseButtonEvent event) {
        if(event.action() == Action.PRESS) {
            mouseButtonsDown.add(event.button());
        } else if(event.action() == Action.RELEASE) {
            mouseButtonsDown.remove(event.button());
        }
    }

    @EventListener
    public static void onCursorEnter(CursorEnterEvent event) {
        inWindow = event.entered();
    }

    public static boolean isKeyDown(Key key) {
        return keysDown.contains(key);
    }

    public Vector2f getMousePosition() {
        return mousePosition;
    }

    public Set<MouseButton> getMouseButtonsDown() {
        return mouseButtonsDown;
    }

    public Vector2f getPrevMousePosition() {
        return prevMousePosition;
    }

    public Vector2f getMouseDelta() {
        return mouseDelta;
    }

    static {
        EventManager.addListener(Input.class);
    }

    public static int getImKey(Key key) {
        return switch (key) {
            case KEY_TAB -> ImGuiKey.Tab;
            case KEY_LEFT -> ImGuiKey.LeftArrow;
            case KEY_RIGHT -> ImGuiKey.RightArrow;
            case KEY_UP -> ImGuiKey.UpArrow;
            case KEY_DOWN -> ImGuiKey.DownArrow;
            case KEY_PAGE_UP -> ImGuiKey.PageUp;
            case KEY_PAGE_DOWN -> ImGuiKey.PageDown;
            case KEY_HOME -> ImGuiKey.Home;
            case KEY_END -> ImGuiKey.End;
            case KEY_INSERT -> ImGuiKey.Insert;
            case KEY_DELETE -> ImGuiKey.Delete;
            case KEY_BACKSPACE -> ImGuiKey.Backspace;
            case KEY_SPACE -> ImGuiKey.Space;
            case KEY_ENTER -> ImGuiKey.Enter;
            case KEY_ESCAPE -> ImGuiKey.Escape;
            case KEY_APOSTROPHE -> ImGuiKey.Apostrophe;
            case KEY_COMMA -> ImGuiKey.Comma;
            case KEY_MINUS -> ImGuiKey.Minus;
            case KEY_PERIOD -> ImGuiKey.Period;
            case KEY_SLASH -> ImGuiKey.Slash;
            case KEY_SEMICOLON -> ImGuiKey.Semicolon;
            case KEY_EQUAL -> ImGuiKey.Equal;
            case KEY_LEFT_BRACKET -> ImGuiKey.LeftBracket;
            case KEY_BACKSLASH -> ImGuiKey.Backslash;
            case KEY_RIGHT_BRACKET -> ImGuiKey.RightBracket;
            case KEY_GRAVE_ACCENT -> ImGuiKey.GraveAccent;
            case KEY_CAPS_LOCK -> ImGuiKey.CapsLock;
            case KEY_SCROLL_LOCK -> ImGuiKey.ScrollLock;
            case KEY_NUM_LOCK -> ImGuiKey.NumLock;
            case KEY_PRINT_SCREEN -> ImGuiKey.PrintScreen;
            case KEY_PAUSE -> ImGuiKey.Pause;
            case KEY_KEYPAD_0 -> ImGuiKey.Keypad0;
            case KEY_KEYPAD_1 -> ImGuiKey.Keypad1;
            case KEY_KEYPAD_2 -> ImGuiKey.Keypad2;
            case KEY_KEYPAD_3 -> ImGuiKey.Keypad3;
            case KEY_KEYPAD_4 -> ImGuiKey.Keypad4;
            case KEY_KEYPAD_5 -> ImGuiKey.Keypad5;
            case KEY_KEYPAD_6 -> ImGuiKey.Keypad6;
            case KEY_KEYPAD_7 -> ImGuiKey.Keypad7;
            case KEY_KEYPAD_8 -> ImGuiKey.Keypad8;
            case KEY_KEYPAD_9 -> ImGuiKey.Keypad9;
            case KEY_KEYPAD_DECIMAL -> ImGuiKey.KeypadDecimal;
            case KEY_KEYPAD_DIVIDE -> ImGuiKey.KeypadDivide;
            case KEY_KEYPAD_MULTIPLY -> ImGuiKey.KeypadMultiply;
            case KEY_KEYPAD_SUBTRACT -> ImGuiKey.KeypadSubtract;
            case KEY_KEYPAD_ADD -> ImGuiKey.KeypadAdd;
            case KEY_KEYPAD_ENTER -> ImGuiKey.KeypadEnter;
            case KEY_KEYPAD_EQUAL -> ImGuiKey.KeypadEqual;
            case KEY_LEFT_SHIFT -> ImGuiKey.LeftShift;
            case KEY_LEFT_CONTROL -> ImGuiKey.LeftCtrl;
            case KEY_LEFT_ALT -> ImGuiKey.LeftAlt;
            case KEY_LEFT_SUPER -> ImGuiKey.LeftSuper;
            case KEY_RIGHT_SHIFT -> ImGuiKey.RightShift;
            case KEY_RIGHT_CONTROL -> ImGuiKey.RightCtrl;
            case KEY_RIGHT_ALT -> ImGuiKey.RightAlt;
            case KEY_RIGHT_SUPER -> ImGuiKey.RightSuper;
            case KEY_MENU -> ImGuiKey.Menu;
            case KEY_0 -> ImGuiKey._0;
            case KEY_1 -> ImGuiKey._1;
            case KEY_2 -> ImGuiKey._2;
            case KEY_3 -> ImGuiKey._3;
            case KEY_4 -> ImGuiKey._4;
            case KEY_5 -> ImGuiKey._5;
            case KEY_6 -> ImGuiKey._6;
            case KEY_7 -> ImGuiKey._7;
            case KEY_8 -> ImGuiKey._8;
            case KEY_9 -> ImGuiKey._9;
            case KEY_A -> ImGuiKey.A;
            case KEY_B -> ImGuiKey.B;
            case KEY_C -> ImGuiKey.C;
            case KEY_D -> ImGuiKey.D;
            case KEY_E -> ImGuiKey.E;
            case KEY_F -> ImGuiKey.F;
            case KEY_G -> ImGuiKey.G;
            case KEY_H -> ImGuiKey.H;
            case KEY_I -> ImGuiKey.I;
            case KEY_J -> ImGuiKey.J;
            case KEY_K -> ImGuiKey.K;
            case KEY_L -> ImGuiKey.L;
            case KEY_M -> ImGuiKey.M;
            case KEY_N -> ImGuiKey.N;
            case KEY_O -> ImGuiKey.O;
            case KEY_P -> ImGuiKey.P;
            case KEY_Q -> ImGuiKey.Q;
            case KEY_R -> ImGuiKey.R;
            case KEY_S -> ImGuiKey.S;
            case KEY_T -> ImGuiKey.T;
            case KEY_U -> ImGuiKey.U;
            case KEY_V -> ImGuiKey.V;
            case KEY_W -> ImGuiKey.W;
            case KEY_X -> ImGuiKey.X;
            case KEY_Y -> ImGuiKey.Y;
            case KEY_Z -> ImGuiKey.Z;
            case KEY_F1 -> ImGuiKey.F1;
            case KEY_F2 -> ImGuiKey.F2;
            case KEY_F3 -> ImGuiKey.F3;
            case KEY_F4 -> ImGuiKey.F4;
            case KEY_F5 -> ImGuiKey.F5;
            case KEY_F6 -> ImGuiKey.F6;
            case KEY_F7 -> ImGuiKey.F7;
            case KEY_F8 -> ImGuiKey.F8;
            case KEY_F9 -> ImGuiKey.F9;
            case KEY_F10 -> ImGuiKey.F10;
            case KEY_F11 -> ImGuiKey.F11;
            case KEY_F12 -> ImGuiKey.F12;
            default -> ImGuiKey.None;
        };
    }
}
