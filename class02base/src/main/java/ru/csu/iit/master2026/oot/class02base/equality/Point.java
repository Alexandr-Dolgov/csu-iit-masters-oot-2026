package ru.csu.iit.master2026.oot.class02base.equality;

/**
 * Точка — запись: equals, hashCode и toString по компонентам пишет компилятор.
 * Запись final: подкласса, который сломает её equals, не будет.
 */
public record Point(int x, int y) {
}
