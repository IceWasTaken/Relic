package net.ice.curio.library.sdl.video;

import net.ice.curio.Curio;
import net.ice.curio.input.enums.Action;
import net.ice.curio.input.event.KeyEvent;
import net.ice.curio.input.event.MouseButtonEvent;
import net.ice.curio.input.event.CursorEvent;
import net.ice.curio.input.event.CursorEnterEvent;
import net.ice.curio.library.sdl.SDLKey;
import net.ice.curio.window.Window;
import net.ice.curio.window.enums.WindowAttribute;
import net.ice.heirloom.event.EventManager;
import org.joml.Vector2i;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.sdl.SDLError.SDL_ClearError;
import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLVideo.*;

public class SDLWindow extends Window {

	private int width = 1280;
	private int height = 720;

	private boolean resized = false;
	private boolean shouldClose = false;

	private final long windowHandle;
	private final long context;

	public SDLWindow(Curio curio) {
		super(curio);

		if(!SDLInit.SDL_InitSubSystem(SDLInit.SDL_INIT_VIDEO)) {
			SDL_ClearError();
			throw new RuntimeException("[SDLVideo]: SDL Failed to initialize SDLVideo: [" + SDL_GetError() + "]");
		}

		this.windowHandle = SDLVideo.SDL_CreateWindow("Test", 1280, 640, SDLVideo.SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE);
		this.context = SDLVideo.SDL_GL_CreateContext(windowHandle);

	}

	@Override
	public void update(float deltaTime) {
		SDL_GL_MakeCurrent(windowHandle, context);
		SDL_GL_SwapWindow(windowHandle);
		try(MemoryStack stack = MemoryStack.stackPush()) {
			SDL_Event event = SDL_Event.malloc(stack);
			while(SDL_PollEvent(event)) {
				switch(event.type()) {
					case SDL_EVENT_QUIT -> shouldClose = true;

					//resize window
					case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> resize(event);

					//key up/down
					case SDL_EVENT_KEY_DOWN -> EventManager.execute(new KeyEvent(
							SDLKey.toKey(event.key().scancode()),
							Action.PRESS
					));
					case SDL_EVENT_KEY_UP -> EventManager.execute(new KeyEvent(
							SDLKey.toKey(event.key().scancode()),
							Action.RELEASE
					));

					//move mouse
					case SDL_EVENT_MOUSE_MOTION -> EventManager.execute(new CursorEvent(
							event.motion().x(),
							event.motion().y()
					));

					//mouse button up/down
					case SDL_EVENT_MOUSE_BUTTON_DOWN -> EventManager.execute(new MouseButtonEvent(
							SDLKey.toMouseButton(event.button().button()),
							Action.PRESS
					));
					case SDL_EVENT_MOUSE_BUTTON_UP -> EventManager.execute(new MouseButtonEvent(
							SDLKey.toMouseButton(event.button().button()),
							Action.RELEASE
					));

					case SDL_EVENT_WINDOW_MOUSE_ENTER ->  EventManager.execute(new CursorEnterEvent(true));
					case SDL_EVENT_WINDOW_MOUSE_LEAVE ->  EventManager.execute(new CursorEnterEvent(false));


				}
			}
		}
	}

	public void resize(SDL_Event event) {
		this.width = event.window().data1();
		this.height = event.window().data2();
		this.resized = true;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public Vector2i getSize() {
		return new Vector2i(width, height);
	}

	@Override
	public boolean shouldResize() {
		if(resized) {
			this.resized = false;
			return true;
		}
		return false;
	}

	@Override
	public void attribute(WindowAttribute attribute, int value) {
		SDL_GL_SetAttribute(getAttribute(attribute), value);
	}

	public boolean shouldClose() {
		return shouldClose;
	}

	private static int getAttribute(WindowAttribute attribute) {
		return switch(attribute) {
			case CONTEXT_VERSION_MAJOR -> SDL_GL_CONTEXT_MAJOR_VERSION;
			case CONTEXT_VERSION_MINOR -> SDL_GL_CONTEXT_MINOR_VERSION;
			case CONTEXT_PROFILE -> SDL_GL_CONTEXT_PROFILE_MASK;
			case CONTEXT_DEBUG -> SDL_GL_CONTEXT_DEBUG_FLAG;
		};
	}
}