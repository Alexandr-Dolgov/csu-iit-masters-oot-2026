package ru.csu.iit.master2026.oot.class02base.command;

/** Дописать в конец. Для отмены команда помнит, куда писала. */
public final class Append implements Command {

    private final Document document;
    private final String fragment;
    private int position;

    public Append(Document document, String fragment) {
        this.document = document;
        this.fragment = fragment;
    }

    @Override
    public void execute() {
        position = document.length();
        document.insert(position, fragment);
    }

    @Override
    public void undo() {
        document.delete(position, fragment.length());
    }
}
