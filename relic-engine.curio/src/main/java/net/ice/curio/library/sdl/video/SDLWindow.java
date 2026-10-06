package net.ice.curio.library.sdl.video;

import net.ice.curio.Curio;
import net.ice.curio.config.RendererConfig;
import net.ice.curio.input.enums.Action;
import net.ice.curio.input.event.KeyEvent;
import net.ice.curio.input.event.MouseButtonEvent;
import net.ice.curio.input.event.CursorEvent;
import net.ice.curio.input.event.CursorEnterEvent;
import net.ice.curio.library.sdl.SDL;
import net.ice.curio.library.sdl.SDLKey;
import net.ice.curio.system.memory.Vector2iBuffer;
import net.ice.curio.window.Window;
import net.ice.curio.window.enums.WindowAttribute;
import net.ice.heirloom.event.EventManager;
import org.joml.Vector2i;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;

import static org.lwjgl.sdl.SDLError.SDL_ClearError;
import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLVideo.*;
import static org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_CreateSurface;

public class SDLWindow extends Window {

	protected Vector2i windowSize = new Vector2i(1280, 720);
	protected Vector2i framebufferSize = new Vector2i(0, 0);

	protected boolean windowResized = false;
	protected boolean framebufferResized = false;

	private boolean shouldClose = false;

	private final long windowHandle;
	private final long context;

	public SDLWindow(Curio curio) {
		super(curio);

		if(!SDLInit.SDL_InitSubSystem(SDLInit.SDL_INIT_VIDEO)) {
			SDL_ClearError();
			throw new RuntimeException("[SDLVideo]: SDL Failed to initialize SDLVideo: [" + SDL_GetError() + "]");
		}

		long flags = SDL_WINDOW_RESIZABLE | SDL_WINDOW_HIGH_PIXEL_DENSITY;
		switch(RendererConfig.getBackendType()) {
			case OPENGL -> flags = flags | SDL_WINDOW_OPENGL;
			case VULKAN -> flags = flags | SDL_WINDOW_VULKAN;
		}

		curio.getGraphicsContext().setupWindowAttributes(this);

		this.windowHandle = SDLVideo.SDL_CreateWindow("Test", 1280, 720, flags);
		this.context = SDLVideo.SDL_GL_CreateContext(windowHandle);

		updateWindowSize();
		updateFramebufferSize();
	}

	@Override
	public void update(float deltaTime) {
		SDL_GL_SwapWindow(windowHandle);

		try(MemoryStack stack = MemoryStack.stackPush()) {
			SDL_Event event = SDL_Event.malloc(stack);
			while(SDL_PollEvent(event)) {
				switch(event.type()) {
					case SDL_EVENT_QUIT -> shouldClose = true;

					//resize window
					case SDL_EVENT_WINDOW_RESIZED -> {
						windowSize.set(event.window().data1(), event.window().data2());
						windowResized = true;
					}

					//resize framebuffer
					case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> {
						framebufferSize.set(event.window().data1(), event.window().data2());
						framebufferResized = true;
					}

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

	public void createSurface(VkInstance instance, LongBuffer buffer) {
		SDL_Vulkan_CreateSurface(windowHandle, instance, null, buffer);
	}

	@Override
	public void attribute(WindowAttribute attribute, int value) {
		SDL_GL_SetAttribute(getAttribute(attribute), value);
	}

	@Override
	public void createContext() {
		SDL_GL_MakeCurrent(windowHandle, context);
	}

	public boolean shouldClose() {
		return shouldClose;
	}

	public Vector2i updateWindowSize() {
		try(Vector2iBuffer buffer = new Vector2iBuffer()) {
			SDL.SDL_GetWindowSize(windowHandle, buffer);
			return buffer.get(windowSize);
		}
	}

	public Vector2i updateFramebufferSize() {
		try(Vector2iBuffer buffer = new Vector2iBuffer()) {
			SDL.SDL_GetWindowSizeInPixels(windowHandle, buffer);
			return buffer.get(framebufferSize);
		}
	}

	@Override
	public boolean shouldResizeWindow() {
		if (windowResized) {
			this.windowResized = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldResizeFramebuffer() {
		if(framebufferResized) {
			this.framebufferResized = false;
			return true;
		}
		return false;
	}

	@Override
	public Vector2i getWindowSize() {
		return windowSize;
	}

	@Override
	public Vector2i getFramebufferSize() {
		return framebufferSize;
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