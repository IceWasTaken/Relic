package net.ice.relic.util;

public class MessageUtil {

    public static String createErrorMessage(String message, String ext) {
        return message + " [" + ext + "]";
    }
}
