package ru.csu.iit.master2026.oot.class04base.events;

import java.util.ArrayList;
import java.util.List;

/** Кто и в каком порядке обработал события. */
public final class EventLog {

    private final List<String> entries = new ArrayList<>();

    public void add(String entry) {
        entries.add(entry);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
