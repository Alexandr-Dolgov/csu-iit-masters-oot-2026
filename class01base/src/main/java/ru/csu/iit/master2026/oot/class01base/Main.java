package ru.csu.iit.master2026.oot.class01base;

public class Main {
    public static void main(String[] args) {
        System.out.println("123");
        Person p1 = new Person();
        p1.id = 1;
        p1.name = "John";
        Object p2 = new Person();
        System.out.println(p1);//Person [id=1, name=John]
        System.out.println(p2);//Person [id=2, name=Alice]
        p1.id = 2;
        System.out.println(p1);//Person [id=2, name=John]
    }
}
