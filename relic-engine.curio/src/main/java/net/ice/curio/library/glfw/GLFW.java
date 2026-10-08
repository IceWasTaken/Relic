package net.ice.curio.library.glfw;

import net.ice.curio.system.memory.Vector2fBuffer;
import net.ice.curio.system.memory.Vector2iBuffer;
import org.lwjgl.system.Checks;

import static org.lwjgl.glfw.GLFW.*;

public class GLFW {

	public static void glfwGetWindowSize(long window, Vector2iBuffer buff) {
		if(Checks.CHECKS){
			buff.checkSafe(2);
		}

		nglfwGetWindowSize(window, buff.memAddress(0), buff.memAddress(4));
	}

	public static void glfwGetFramebufferSize(long window, Vector2iBuffer buff) {
		if(Checks.CHECKS){
			buff.checkSafe(2);
		}

		nglfwGetFramebufferSize(window, buff.memAddress(0), buff.memAddress(4));
	}

	public static void glfwGetMonitorPos(long window, Vector2iBuffer buff) {
		if(Checks.CHECKS){
			buff.checkSafe(2);
		}

		nglfwGetMonitorPos(window, buff.memAddress(0), buff.memAddress(4));
	}

	public static void glfwGetWindowContentScale(long window, Vector2fBuffer buff) {
		if(Checks.CHECKS){
			buff.checkSafe(2);
		}

		nglfwGetWindowContentScale(window, buff.memAddress(0), buff.memAddress(4));
	}



}
