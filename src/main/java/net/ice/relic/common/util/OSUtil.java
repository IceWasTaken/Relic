package net.ice.relic.common.util;

import java.util.Locale;

public class OSUtil {

    public static OSType getOSType() {
        OSType result;
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        System.out.println(os);
        result = switch (os) {
            case "mac" -> OSType.MAC;
            case "nux" -> OSType.LINUX;
            case "win" -> OSType.WINDOWS;
            default -> OSType.OTHER;
        };
        return result;
    }

    public enum OSType {
        WINDOWS,
        MAC,
        LINUX,
        OTHER
    }
}
