package net.ice.curio.system;

import org.tinylog.Logger;
import net.ice.curio.system.enums.OSArchitecture;
import net.ice.curio.system.enums.OSType;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.util.Locale;

import static net.ice.curio.system.enums.OSArchitecture.*;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL42.GL_MAX_IMAGE_UNITS;
import static org.lwjgl.opengl.GL43.*;


public class SystemInfo {

    private final OSType osType;
    private final OSArchitecture osArchitecture;

    public SystemInfo() {
        this.osType = getOSType();
        this.osArchitecture = getOSArchitecture();
    }

    public static OSType getOSType() {
        String os = System.getProperty("os.name", "generic");

        if(os != null) {
            os = os.toLowerCase(Locale.ENGLISH);
            if(os.startsWith("windows")) {
                return OSType.WINDOWS;
            } else if (os.startsWith("mac") || os.startsWith("macos")) {
                return OSType.MAC;
            } else if (os.startsWith("linux")) {
                return OSType.LINUX;
            } else {
                return OSType.OTHER;
            }
        }
        return OSType.UNKNOWN_OR_NULL;
    }

    public static void logUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long memoryBytes = runtime.totalMemory() - runtime.freeMemory();
        long memoryKB = memoryBytes / 1024;
        long memoryMB = memoryKB / 1024;

        //Logger.info("Used memory: {} B.", memoryBytes);
        //Logger.info("Used memory: {} KB.", memoryKB);
        Logger.info("Used memory: {} MB.", memoryMB);
    }

    public static void logUnusedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long memoryBytes = runtime.freeMemory();
        long memoryKB = memoryBytes / 1024;
        long memoryMB = memoryKB / 1024;

        //Logger.info("Unused memory: {} B.", memoryBytes);
        //Logger.info("Unused memory: {} KB.", memoryKB);
        Logger.info("Unused memory: {} MB.", memoryMB);
    }

    public OSArchitecture getOSArchitecture() {
        String architecture = System.getProperty("os.arch");

        if(architecture != null) {
            architecture = architecture.toLowerCase(Locale.ENGLISH);
            if(architecture.startsWith("amd64") || architecture.startsWith(AMD64.alias)) {
                return AMD64;
            } else if (architecture.startsWith("arm64") || architecture.startsWith(ARM64.alias)) {
                return ARM64;
            } else if (architecture.startsWith("x86") || architecture.startsWith(x86.alias)) {
                return x86;
            } else {
                return UNKNOWN;
            }
        }
        return UNKNOWN;
    }



    public static void logSystemInfo() {
        Logger.info("OS Name: " + System.getProperty("os.name"));
        Logger.info("CPU Architecture: " + System.getProperty("os.arch") + "\n");
    }


    public static void logGLInfo() {
        if (!GL.getCapabilities().OpenGL40) {
            Logger.warn("OpenGL 4.0 or higher is not available!");
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            System.out.print("\n");
            Logger.info("------------ OpenGL INFO ------------");
            Logger.info("OpenGL Version: " + glGetString(GL_VERSION));
            Logger.info("OpenGL Renderer: " + glGetString(GL_RENDERER));
            Logger.info("OpenGL Vendor: " + glGetString(GL_VENDOR));
            Logger.info("OpenGL Shading Language Version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
            Logger.info("-------------------------------------\n");
            Logger.info("-------- OpenGL Texture Max Values --------");
            Logger.info("GL_MAX_TEXTURE_SIZE: " + getIntegerValue(GL_MAX_TEXTURE_SIZE));
            Logger.info("GL_MAX_3D_TEXTURE_SIZE: " + getIntegerValue(GL_MAX_3D_TEXTURE_SIZE));
            Logger.info("GL_MAX_CUBE_MAP_TEXTURE_SIZE: " + getIntegerValue(GL_MAX_CUBE_MAP_TEXTURE_SIZE));
            Logger.info("GL_MAX_ARRAY_TEXTURE_LAYERS: " + getIntegerValue(GL_MAX_ARRAY_TEXTURE_LAYERS));
            Logger.info("GL_MAX_TEXTURE_BUFFER_SIZE: " + getIntegerValue(GL_MAX_TEXTURE_BUFFER_SIZE));
            Logger.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + getIntegerValue(GL_MAX_TEXTURE_IMAGE_UNITS));
            Logger.info("GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS: " + getIntegerValue(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS));
            Logger.info("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS: " + getIntegerValue(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
            Logger.info("GL_MAX_TEXTURE_LOD_BIAS: " + getIntegerValue(GL_MAX_TEXTURE_LOD_BIAS));
            Logger.info("GL_MAX_TESS_GEN_LEVEL: " + getIntegerValue(GL_MAX_TESS_GEN_LEVEL));
            Logger.info("GL_MAX_IMAGE_UNITS: " + getIntegerValue(GL_MAX_IMAGE_UNITS));
            Logger.info("-------------------------------------\n");
            Logger.info("-------- OpenGL Shader Max Values --------");
            Logger.info("GL_MAX_VERTEX_ATTRIBS: " + getIntegerValue(GL_MAX_VERTEX_ATTRIBS));
            Logger.info("GL_MAX_VERTEX_UNIFORM_COMPONENTS: " + getIntegerValue(GL_MAX_VERTEX_UNIFORM_COMPONENTS));
            Logger.info("GL_MAX_VERTEX_UNIFORM_VECTORS: " + getIntegerValue(GL_MAX_VERTEX_UNIFORM_VECTORS));
            Logger.info("GL_MAX_VERTEX_OUTPUT_COMPONENTS: " + getIntegerValue(GL_MAX_VERTEX_OUTPUT_COMPONENTS));
            Logger.info("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS: " + getIntegerValue(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));
            Logger.info("GL_MAX_FRAGMENT_UNIFORM_VECTORS: " + getIntegerValue(GL_MAX_FRAGMENT_UNIFORM_VECTORS));
            Logger.info("GL_MAX_FRAGMENT_INPUT_COMPONENTS: " + getIntegerValue(GL_MAX_FRAGMENT_INPUT_COMPONENTS));
            Logger.info("GL_MAX_GEOMETRY_SHADER_INVOCATIONS: " + getIntegerValue(GL_MAX_GEOMETRY_SHADER_INVOCATIONS));
            Logger.info("GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS: " + getIntegerValue(GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS));
            Logger.info("GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS: " + getIntegerValue(GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS));
            Logger.info("GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS: " + getIntegerValue(GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS));
            Logger.info("GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS: " + getIntegerValue(GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS));
            Logger.info("GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS: " + getIntegerValue(GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS));
            Logger.info("GL_MAX_SHADER_STORAGE_BLOCK_SIZE: " + getIntegerValue(GL_MAX_SHADER_STORAGE_BLOCK_SIZE));
            Logger.info("-------------------------------------\n");
            Logger.info("-------- Framebuffer Limits --------");
            Logger.info("GL_MAX_COLOR_ATTACHMENTS: " + getIntegerValue(GL_MAX_COLOR_ATTACHMENTS));
            Logger.info("GL_MAX_FRAMEBUFFER_WIDTH: " + getIntegerValue(GL_MAX_FRAMEBUFFER_WIDTH));
            Logger.info("GL_MAX_FRAMEBUFFER_HEIGHT: " + getIntegerValue(GL_MAX_FRAMEBUFFER_HEIGHT));
            Logger.info("GL_MAX_FRAMEBUFFER_SAMPLES: " + getIntegerValue(GL_MAX_FRAMEBUFFER_SAMPLES));
            Logger.info("GL_MAX_FRAMEBUFFER_LAYERS: " + getIntegerValue(GL_MAX_FRAMEBUFFER_LAYERS));
            Logger.info("GL_MAX_RENDERBUFFER_SIZE: " + getIntegerValue(GL_MAX_RENDERBUFFER_SIZE));
            Logger.info("-------------------------------------\n");
            Logger.info("-------- Shader Storage Info --------");
            Logger.info("GL_MAX_SHADER_STORAGE_BLOCK_SIZE: " + getIntegerValue(GL_MAX_SHADER_STORAGE_BLOCK_SIZE));
            Logger.info("-------------------------------------\n");
        } catch (Exception e) {
            Logger.error("Error retrieving OpenGL information", e);
        }
    }

    private static int getIntegerValue(int parameter) {
        int[] value = new int[1];
        glGetIntegerv(parameter, value);
        return value[0];
    }
}
