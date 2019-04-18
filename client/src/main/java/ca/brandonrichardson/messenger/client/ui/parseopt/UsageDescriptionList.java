package ca.brandonrichardson.messenger.client.ui.parseopt;

import java.util.Iterator;

public class UsageDescriptionList implements Iterable<CommandUsageDescription> {

    private CommandUsageDescription[] descriptions;

    private UsageDescriptionList(final CommandUsageDescription[] descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public Iterator<CommandUsageDescription> iterator() {
        return new UsageDescriptionIterator(this.descriptions);
    }

    public static UsageDescriptionList of(final CommandUsageDescription... descriptions) {
        return new UsageDescriptionList(descriptions);
    }
}
