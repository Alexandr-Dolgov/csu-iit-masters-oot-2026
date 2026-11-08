package ru.csu.iit.master2026.oot.class01base;

public class Main {
    public static void main(String[] args) {
        System.out.println("123");
        Person p1 = new Person();
        p1.id = 1;
        p1.name = "John";
        Person p2 = new Person();
        p2.id = 2;
        p2.name = "Alice";
        System.out.println(p1);//ru.chelgu.oot.class01.Person@5f184fc6
        System.out.println(p2);//ru.chelgu.oot.class01.Person@3feba861
        p1.id = 2;
        System.out.println(p1);//ru.chelgu.oot.class01.Person@5f184fc6
    }
}
