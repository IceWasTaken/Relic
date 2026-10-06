package net.ice.curio.library.sdl;

import net.ice.curio.system.memory.Vector2iBuffer;
import org.lwjgl.system.NativeType;


import static org.lwjgl.sdl.SDLVideo.nSDL_GetWindowSize;
import static org.lwjgl.sdl.SDLVideo.nSDL_GetWindowSizeInPixels;
import static org.lwjgl.system.Checks.CHECKS;

public class SDL {

	public static boolean SDL_GetWindowSizeInPixels(long window, Vector2iBuffer size) {
		if (CHECKS) {
			size.checkSafe(2);
		}
		return nSDL_GetWindowSizeInPixels(window, size.memAddress(0), size.memAddress(4));
	}

	public static boolean SDL_GetWindowSize(long window, Vector2iBuffer size) {
		if (CHECKS) {
			size.checkSafe(2);
		}
		return nSDL_GetWindowSize(window,size.memAddress(0), size.memAddress(4));
	}
}
