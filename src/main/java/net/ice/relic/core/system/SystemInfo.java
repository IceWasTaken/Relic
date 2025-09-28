package net.ice.relic.core.system;

import net.ice.relic.core.system.enums.OSArchitecture;
import net.ice.relic.core.system.enums.OSType;
import org.tinylog.Logger;

import java.util.Locale;

import static net.ice.relic.core.system.enums.OSArchitecture.*;


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
        Logger.info("CPU Architecture: " + System.getProperty("os.arch"));
    }
}
