package ru.csu.iit.master2026.oot.class02base.equality;

/** Точка на плоскости. Две точки с одинаковыми координатами — одна и та же точка. */
public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    /** Равны только объекты ровно одного класса: симметрично и транзитивно. */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    /** Равные объекты — равные хэши: иначе HashSet и HashMap их не найдут. */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
