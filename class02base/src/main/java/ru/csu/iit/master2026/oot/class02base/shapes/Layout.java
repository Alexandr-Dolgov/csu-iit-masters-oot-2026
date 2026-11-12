package ru.csu.iit.master2026.oot.class02base.shapes;

/** Клиент, написанный в терминах {@link Rectangle}: про квадраты он не знает. */
public final class Layout {

    private Layout() {
    }

    /** Растянуть блок по ширине колонки, высоту не трогать. */
    public static void fitToColumn(Rectangle block, int columnWidth) {
        block.setWidth(columnWidth);
    }
}
