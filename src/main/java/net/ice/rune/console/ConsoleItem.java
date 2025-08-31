package net.ice.rune.console;

public class ConsoleItem {

    private final char newLine = '\n';

    private ItemType type;
    private String data;
    private long timestamp;


    public ConsoleItem(ItemType type, String data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public ItemType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
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
