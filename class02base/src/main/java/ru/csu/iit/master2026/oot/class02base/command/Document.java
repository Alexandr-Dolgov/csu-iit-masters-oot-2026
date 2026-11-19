package ru.csu.iit.master2026.oot.class02base.command;

/** Получатель команд: умеет только своё, об отмене ничего не знает. */
public final class Document {

    private final StringBuilder text = new StringBuilder();

    public void insert(int position, String fragment) {
        text.insert(position, fragment);
    }

    public void delete(int position, int length) {
        text.delete(position, position + length);
    }

    public int length() {
        return text.length();
    }

    public String text() {
        return text.toString();
    }
}
