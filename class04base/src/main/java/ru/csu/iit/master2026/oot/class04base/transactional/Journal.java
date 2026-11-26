package ru.csu.iit.master2026.oot.class04base.transactional;

import java.util.ArrayList;
import java.util.List;

/** Общий журнал: что сделал сервис и что — менеджер транзакций, в одном порядке. */
public final class Journal {

    private final List<String> entries = new ArrayList<>();

    public void add(String entry) {
        entries.add(entry);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
