package ru.csu.iit.master2026.oot.class02base.shapes;

/** Клиент, написанный в терминах {@link Rectangle}. */
public final class Layout {

    private Layout() {
    }

    /** Растянуть блок по ширине колонки, высоту не трогать. */
    public static Rectangle fitToColumn(Rectangle block, int columnWidth) {
        return block.withWidth(columnWidth);
    }
}
