package net.ice.relic.util;

import java.io.File;

public class IOUtil {

    //yes, it's dumb. is it slightly more efficient than a for loop? yes.
    public static String createPath(String... path) {
        return String.join("/", path);
    }

    public static File loadFile(String... path) {
        return new File(createPath(path));
    }

    public static File loadRealFile(String... path) {
        File file = new File(createPath(path));

        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + file.getAbsolutePath());
        }

        return file;
    }





}
