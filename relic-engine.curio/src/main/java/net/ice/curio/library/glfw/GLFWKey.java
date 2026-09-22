package net.ice.curio.library.glfw;

import net.ice.curio.input.enums.Key;
import net.ice.curio.input.enums.Action;
import net.ice.curio.input.enums.MouseButton;

import static net.ice.curio.input.enums.Key.*;
import static org.lwjgl.glfw.GLFW.*;

public class GLFWKey {

	public static Key toKey(int keycode) {
		return switch (keycode) {
			case GLFW_KEY_0 -> KEY_0;
			case GLFW_KEY_1 -> KEY_1;
			case GLFW_KEY_2 -> KEY_2;
			case GLFW_KEY_3 -> KEY_3;
			case GLFW_KEY_4 -> KEY_4;
			case GLFW_KEY_5 -> KEY_5;
			case GLFW_KEY_6 -> KEY_6;
			case GLFW_KEY_7 -> KEY_7;
			case GLFW_KEY_8 -> KEY_8;
			case GLFW_KEY_9 -> KEY_9;

			case GLFW_KEY_A -> KEY_A;
			case GLFW_KEY_B -> KEY_B;
			case GLFW_KEY_C -> KEY_C;
			case GLFW_KEY_D -> KEY_D;
			case GLFW_KEY_E -> KEY_E;
			case GLFW_KEY_F -> KEY_F;
			case GLFW_KEY_G -> KEY_G;
			case GLFW_KEY_H -> KEY_H;
			case GLFW_KEY_I -> KEY_I;
			case GLFW_KEY_J -> KEY_J;
			case GLFW_KEY_K -> KEY_K;
			case GLFW_KEY_L -> KEY_L;
			case GLFW_KEY_M -> KEY_M;
			case GLFW_KEY_N -> KEY_N;
			case GLFW_KEY_O -> KEY_O;
			case GLFW_KEY_P -> KEY_P;
			case GLFW_KEY_Q -> KEY_Q;
			case GLFW_KEY_R -> KEY_R;
			case GLFW_KEY_S -> KEY_S;
			case GLFW_KEY_T -> KEY_T;
			case GLFW_KEY_U -> KEY_U;
			case GLFW_KEY_V -> KEY_V;
			case GLFW_KEY_W -> KEY_W;
			case GLFW_KEY_X -> KEY_X;
			case GLFW_KEY_Y -> KEY_Y;
			case GLFW_KEY_Z -> KEY_Z;

			case GLFW_KEY_GRAVE_ACCENT -> KEY_GRAVE_ACCENT;

			case GLFW_KEY_MINUS -> KEY_MINUS;
			case GLFW_KEY_EQUAL -> KEY_EQUAL;

			case GLFW_KEY_LEFT_BRACKET -> KEY_LEFT_BRACKET;
			case GLFW_KEY_RIGHT_BRACKET -> KEY_RIGHT_BRACKET;
			case GLFW_KEY_BACKSLASH -> KEY_BACKSLASH;

			case GLFW_KEY_SEMICOLON -> KEY_SEMICOLON;
			case GLFW_KEY_APOSTROPHE -> KEY_APOSTROPHE;

			case GLFW_KEY_COMMA -> KEY_COMMA;
			case GLFW_KEY_PERIOD -> KEY_PERIOD;
			case GLFW_KEY_SLASH -> KEY_SLASH;

			case GLFW_KEY_SPACE -> KEY_SPACE;

			case GLFW_KEY_LEFT -> KEY_LEFT;
			case GLFW_KEY_UP -> KEY_UP;
			case GLFW_KEY_DOWN  -> KEY_DOWN;
			case GLFW_KEY_RIGHT -> KEY_RIGHT;

			case GLFW_KEY_ESCAPE -> KEY_ESCAPE;
			case GLFW_KEY_BACKSPACE -> KEY_BACKSPACE;
			case GLFW_KEY_TAB -> KEY_TAB;
			case GLFW_KEY_ENTER -> KEY_ENTER;

			case GLFW_KEY_CAPS_LOCK -> KEY_CAPS_LOCK;
			case GLFW_KEY_SCROLL_LOCK -> KEY_SCROLL_LOCK;
			case GLFW_KEY_NUM_LOCK -> KEY_NUM_LOCK;
			case GLFW_KEY_PRINT_SCREEN -> KEY_PRINT_SCREEN;

			case GLFW_KEY_DELETE -> KEY_DELETE;
			case GLFW_KEY_PAGE_UP -> KEY_PAGE_UP;
			case GLFW_KEY_PAGE_DOWN -> KEY_PAGE_DOWN;
			case GLFW_KEY_HOME -> KEY_HOME;
			case GLFW_KEY_END -> KEY_END;
			case GLFW_KEY_INSERT -> KEY_INSERT;
			case GLFW_KEY_PAUSE -> KEY_PAUSE;
			case GLFW_KEY_MENU -> KEY_MENU;

			case GLFW_KEY_LEFT_SUPER -> KEY_LEFT_SUPER;
			case GLFW_KEY_RIGHT_SUPER -> KEY_RIGHT_SUPER;
			case GLFW_KEY_LEFT_SHIFT -> KEY_LEFT_SHIFT;
			case GLFW_KEY_RIGHT_SHIFT -> KEY_RIGHT_SHIFT;
			case GLFW_KEY_LEFT_CONTROL -> KEY_LEFT_CONTROL;
			case GLFW_KEY_RIGHT_CONTROL -> KEY_RIGHT_CONTROL;
			case GLFW_KEY_LEFT_ALT -> KEY_LEFT_ALT;
			case GLFW_KEY_RIGHT_ALT -> KEY_RIGHT_ALT;

			case GLFW_KEY_F1 -> KEY_F1;
			case GLFW_KEY_F2 -> KEY_F2;
			case GLFW_KEY_F3 -> KEY_F3;
			case GLFW_KEY_F4 -> KEY_F4;
			case GLFW_KEY_F5 -> KEY_F5;
			case GLFW_KEY_F6 -> KEY_F6;
			case GLFW_KEY_F7 -> KEY_F7;
			case GLFW_KEY_F8 -> KEY_F8;
			case GLFW_KEY_F9 -> KEY_F9;
			case GLFW_KEY_F10 -> KEY_F10;
			case GLFW_KEY_F11 -> KEY_F11;
			case GLFW_KEY_F12 -> KEY_F12;
			case GLFW_KEY_F13 -> KEY_F13;
			case GLFW_KEY_F14 -> KEY_F14;
			case GLFW_KEY_F15 -> KEY_F15;
			case GLFW_KEY_F16 -> KEY_F16;
			case GLFW_KEY_F17 -> KEY_F17;
			case GLFW_KEY_F18 -> KEY_F18;
			case GLFW_KEY_F19 -> KEY_F19;
			case GLFW_KEY_F20 -> KEY_F20;
			case GLFW_KEY_F21 -> KEY_F21;
			case GLFW_KEY_F22 -> KEY_F22;
			case GLFW_KEY_F23 -> KEY_F23;
			case GLFW_KEY_F24 -> KEY_F24;

			case GLFW_KEY_KP_0 -> KEY_KEYPAD_0;
			case GLFW_KEY_KP_1 -> KEY_KEYPAD_1;
			case GLFW_KEY_KP_2 -> KEY_KEYPAD_2;
			case GLFW_KEY_KP_3 -> KEY_KEYPAD_3;
			case GLFW_KEY_KP_4 -> KEY_KEYPAD_4;
			case GLFW_KEY_KP_5 -> KEY_KEYPAD_5;
			case GLFW_KEY_KP_6 -> KEY_KEYPAD_6;
			case GLFW_KEY_KP_7 -> KEY_KEYPAD_7;
			case GLFW_KEY_KP_8 -> KEY_KEYPAD_8;
			case GLFW_KEY_KP_9 -> KEY_KEYPAD_9;
			case GLFW_KEY_KP_DECIMAL -> KEY_KEYPAD_DECIMAL;
			case GLFW_KEY_KP_DIVIDE -> KEY_KEYPAD_DIVIDE;
			case GLFW_KEY_KP_MULTIPLY -> KEY_KEYPAD_MULTIPLY;
			case GLFW_KEY_KP_SUBTRACT -> KEY_KEYPAD_SUBTRACT;
			case GLFW_KEY_KP_ADD -> KEY_KEYPAD_ADD;
			case GLFW_KEY_KP_ENTER -> KEY_KEYPAD_ENTER;
			case GLFW_KEY_KP_EQUAL -> KEY_KEYPAD_EQUAL;

			default -> KEY_UNKNOWN_1;
		};
	}

	public static Action toAction(int action) {
		return switch(action) {
			case GLFW_PRESS -> Action.PRESS;
			case GLFW_RELEASE -> Action.RELEASE;
			case GLFW_REPEAT -> Action.HOLD;
			default -> Action.UNKNOWN;
		};
	}

	public static MouseButton toMouseButton(int button) {
		return switch (button) {
			case GLFW_MOUSE_BUTTON_LEFT -> MouseButton.BUTTON_LEFT;
			case GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.BUTTON_RIGHT;
			case GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.BUTTON_MIDDLE;
			case GLFW_MOUSE_BUTTON_4 -> MouseButton.BUTTON_4;
			case GLFW_MOUSE_BUTTON_5 -> MouseButton.BUTTON_5;
			case GLFW_MOUSE_BUTTON_6 -> MouseButton.BUTTON_6;
			case GLFW_MOUSE_BUTTON_7 -> MouseButton.BUTTON_7;
			case GLFW_MOUSE_BUTTON_8 -> MouseButton.BUTTON_8;
			default -> null;
		};
	}
}
