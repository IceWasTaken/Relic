package net.ice.relic.common.util;

public class MessageUtil {

    public static String createErrorMessage(String message, String ext) {
        return message + " [" + ext + "]";
    }
}
