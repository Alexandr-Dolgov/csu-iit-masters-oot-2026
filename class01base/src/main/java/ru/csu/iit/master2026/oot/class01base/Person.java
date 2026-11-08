package ru.csu.iit.master2026.oot.class01base;

public class Person extends Object {
    // private, - (package/default), protected, public
    private int id;
    private String name;

    Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return "Person [id=" + id + ", name=" + name + "]";
    }
}
