package net.ice.relic.common.console.logging;

import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.ConsoleItem;
import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.writers.AbstractFormatPatternWriter;

import java.util.Map;

public class RelicWriter extends AbstractFormatPatternWriter {

    /**
     * @param properties Configuration for writer
     */
    public RelicWriter(Map<String, String> properties) {
        super(properties);
    }

    @Override
    public void write(LogEntry logEntry) throws Exception {
        String message = logEntry.getMessage();
        Level level = logEntry.getLevel();

        ConsoleItem.ItemType type = switch(level) {
            case TRACE, OFF, DEBUG -> ConsoleItem.ItemType.LOG;
            case INFO -> ConsoleItem.ItemType.INFO;
            case WARN -> ConsoleItem.ItemType.WARNING;
            case ERROR -> ConsoleItem.ItemType.ERROR;
        };

        Console.consoleItems.add(new ConsoleItem(type, render(logEntry)));
    }

    @Override
    public void flush() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }
}
