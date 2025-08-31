package net.ice.rune.console;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class CommandHistory {

    private final int historySize;
    private final Deque<String> commands;
    private int pointer = -1;

    public CommandHistory() {
        this(100);
    }

    public CommandHistory(int size) {
        this.historySize = size;
        this.commands = new ArrayDeque<>(historySize);
    }

    public void add(String cmd) {
        if (commands.size() >= historySize) {
            commands.removeLast();
        }
        commands.addFirst(cmd);
        pointer = -1; // reset navigation
    }

    public String getRecent() {
        return commands.peekFirst();
    }

    public String getPrevious() {
        if (commands.isEmpty()) return null;
        if (pointer < commands.size() - 1) pointer++;
        return getAt(pointer);
    }

    public String getNext() {
        if (commands.isEmpty()) return null;
        if (pointer > 0) pointer--;
        else pointer = -1;
        return (pointer == -1) ? "" : getAt(pointer);
    }

    public List<String> getAll() {
        return commands.stream().toList();
    }

    private String getAt(int index) {
        Iterator<String> it = commands.iterator();
        for (int i = 0; i < index && it.hasNext(); i++) {
            it.next();
        }
        return it.hasNext() ? it.next() : null;
    }


}
