package ru.csu.iit.master2026.oot.class01base;

public class Main {
    public static void main(String[] args) {
        System.out.println("123");
        Person p1 = new Person(1, "John");
        Object p2 = new Person(2, "Alice");
        System.out.println(p1);//Person [id=1, name=John]
        System.out.println(p2);//Person [id=2, name=Alice]
    }
}
