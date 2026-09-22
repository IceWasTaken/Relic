package net.ice.curio.library.sdl;

import net.ice.curio.library.sdl.video.SDLWindow;
import org.lwjgl.sdl.SDLInit;

public class SDLContext {

	public SDLContext() {

		if(!SDLInit.SDL_Init(0)) {
			throw new RuntimeException("[SDLContext] SDLInit failed");
		}

		SDLInit.SDL_SetAppMetadata(
				"Test",
				"0.6.1",
				"net.ice.curio.library.sdl.SDLContext"
		);

	}

//	public SDLWindow initWindow() {
//		//return new SDLWindow();
//	}
}
