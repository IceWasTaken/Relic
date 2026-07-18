package net.ice.relic.common.console;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConsoleItem {

    private final char newLine = '\n';

    private ItemType type;
    private String data;
    private String timestamp;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss.SSS a").withZone(ZoneId.systemDefault());

    public ConsoleItem(ItemType type, String data) {
        this.type = type;
        this.data = data;
        this.timestamp = formatter.format(Instant.ofEpochMilli(System.currentTimeMillis()));
    }

    public ItemType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public enum ItemType {
        COMMAND,
        LOG,
        WARNING,
        ERROR,
        INFO,
        NONE
    }


}
