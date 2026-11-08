package ru.csu.iit.master2026.oot.class01base;

public class Main {
    public static void main(String[] args) {
        System.out.println("123");
        Person p1 = new Person(1, "John");
        Object p2 = new Person(2, "Alice");
        Renameable p3 = new Person(3, "Mike");
        p3.rename("Mikel");
        System.out.println(p1);//Person [id=1, name=John]
        System.out.println(p2);//Person [id=2, name=Alice]
        System.out.println(p3);//Person [id=2, name=Mikel]
    }
}
