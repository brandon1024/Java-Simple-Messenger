package ca.brandonrichardson.messenger.client.ui.parseopt;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class UsageDescriptionIterator implements Iterator<CommandUsageDescription> {

    private int index;

    private CommandUsageDescription[] list;

    public UsageDescriptionIterator(final CommandUsageDescription[] list) {
        this.index = 0;
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.list.length;
    }

    @Override
    public CommandUsageDescription next() {
        if(!this.hasNext()) {
            throw new NoSuchElementException("Reached end of list.");
        }

        return this.list[this.index++];
    }
}
