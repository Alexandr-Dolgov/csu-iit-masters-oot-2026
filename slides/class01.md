---
theme: default
title: Занятие 1 · Конспект
info: |
  Конспект занятия 1 курса «Объектно-ориентированные технологии»
  Магистратура ЧелГУ, 1 курс, 1 семестр

  Программы: 09.04.04 «Программная инженерия», 02.04.02 «ФИИТ»
class: text-left
highlighter: shiki
lineNumbers: false
transition: slide-left
slideNumbers: true
exportFilename: ООТ-01-конспект
---

# Занятие 1

## Базовые понятия ООП. От структуры данных к объекту. Ответственность и абстракция

<div class="pt-10 text-sm opacity-60">
Объектно-ориентированные технологии
</div>

<div class="text-sm opacity-50">
Магистратура. Институт информационных технологий. ЧелГУ
<br> · 09.04.04 «Программная инженерия»
<br> · 02.04.02 «Фундаментальная информатика и информационные технологии»
</div>

<!--
Конспект прошедшего занятия: слайды повторяют то, что показывалось в IDE.
-->

---
layout: default
---

# План занятия

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

### Часть 1 · от структуры к объекту

- процедурная и структурная программа
- объект, `toString`, наследование от `Object`
- один объект — несколько типов, полиморфизм
- области видимости и конструкторы
- инварианты и публичный контракт
- интерфейсы
- композиция и наследование

</div>

<div>

### Часть 2 · ответственность и абстракция

- процедурное оформление доставки заказа
- деньги в `double` и абстракция `Money`
- значимый объект и сущность
- инкапсуляция
- инварианты заказа
- примитивозависимость
- связность и сцепление

</div>

</div>

---
layout: section
---

# Часть 1

## Базовые понятия ООП. От структуры данных к объекту

---
layout: two-cols
commit: "0781849"
---

# Процедурная программа

```java
package ru.chelgu.oot.class01;

public class Main {
    public static void main(String[] args) {
        System.out.println("123");
    }
}
```

<div class="text-sm opacity-60 mt-2">
<code>Main.java</code> — вывод: <code>123</code>
</div>

::right::

<div class="pl-8 pt-4">

### Объекты уже здесь, но "технические"

- `"123"` — объект класса `String`
- `System.out` — объект класса `PrintStream`, а `println` — его метод
- у самого класса `Main` тоже есть объект — класса `Class`: `Main.class`. Через него JVM знает имя класса, его методы и поля

<div class="mt-3 callout text-sm">

Объекты бывают **технические** (строка, поток вывода, список, файл) и **предметные** (человек, заказ). ООП-дизайн — в основном про вторые.

</div>

</div>

---
layout: two-cols
commit: "0fa1b8b"
---

# Класс как структура данных

```java
package ru.chelgu.oot.class01;

public class Person {
    int id;
    String name;
}
```

<div class="text-sm opacity-60 mt-2">
<code>Person.java</code>
</div>

::right::

<div class="pl-8 pt-16">

### Структурная программа

Данные сгруппированы в запись — как `struct` в C или `record` в Pascal.

- у `Person` есть **поля**, но нет **поведения**
- что с этими данными делать, решает тот, кто их использует
- правила («`id` положительный», «имя не пустое») нигде не записаны

</div>

---
layout: two-cols
commit: "95bfb25"
---

# Здесь объекты это — структуры данных, «тупые» хранилища

```java
public static void main(String[] args) {
    System.out.println("123");
    Person p1 = new Person();
    p1.id = 1;
    p1.name = "John";
    Person p2 = new Person();
    p2.id = 2;
    p2.name = "Alice";
    System.out.println(p1);
    System.out.println(p2);
    p1.id = 2;
    System.out.println(p1);
}
```

::right::

<div class="pl-8 pt-16">

### Вывод

```text
123
ru.chelgu.oot.class01.Person@4517d9a3
ru.chelgu.oot.class01.Person@372f7a8d
ru.chelgu.oot.class01.Person@4517d9a3
```

<div class="text-sm opacity-60 mt-2">
Числа после <code>@</code> у вас будут другими
</div>

<div class="mt-6 text-sm">

- `new` создаёт **новый объект**, у каждого свои значения полей
- поля меняет кто угодно и как угодно: `p1.id = 2`
- после изменения это **тот же объект** — `@4517d9a3` не изменилось
- а печатается что-то странное. Откуда оно берётся?

</div>

</div>

---
layout: default
---

# Что внутри `System.out.println`

<div class="grid grid-cols-2 gap-8 mt-2">

<div>

```java
// java.io.PrintStream
public void println(Object x) {
    String s = String.valueOf(x);
    ...
}

// java.lang.String
public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
}

// java.lang.Object
public String toString() {
    return getClass().getName() + "@"
        + Integer.toHexString(hashCode());
}
```

<div class="text-sm opacity-60 mt-2">Исходники JDK 21, сокращено</div>

</div>

<div>

`println(p1)` → `String.valueOf(p1)` → `p1.toString()`

У `Person` своего `toString()` нет — работает метод класса `Object`: **имя класса + `@` + хэш-код** в шестнадцатеричном виде.

<div class="mt-6 callout text-sm">

Как дойти до этого самому: в IDEA `Ctrl+B` (или `Ctrl`+клик) по `println` → провалиться в исходник; документация — `Ctrl+Q`. Читать исходники — необходимая рабочая практика.

</div>

</div>

</div>

<!--
Почему у Person вообще есть метод toString, хотя мы его не писали, — на следующем слайде.
-->

---
layout: two-cols
commit: "0a38211"
---

# Наследование класса

```java
package ru.chelgu.oot.class01;

public class Person extends Object {
    int id;
    String name;
}
```

<div class="text-sm opacity-60 mt-2">
<code>extends Object</code> можно не писать — компилятор добавит его сам
</div>

::right::

<div class="pl-8 pt-16">

### Каждый класс — наследник `Object`

- `Person` **наследует** всё, что есть у `Object`: `toString()`, `equals()`, `hashCode()`, `getClass()`…
- поэтому `p1.toString()` компилируется, хотя в `Person` такого метода нет
- наследник может **заменить** поведение родителя — следующий шаг

</div>

---
layout: two-cols
commit: "514321d"
---

# Переопределяем `toString`

```java
public class Person extends Object {
    int id;
    String name;

    public String toString() {
        return "Person [id=" + id
            + ", name=" + name + "]";
    }
}
```

<div class="text-sm opacity-60 mt-2">
<code>Main</code> не менялся — поменялся только <code>Person</code>
</div>

::right::

<div class="pl-8 pt-16">

### Вывод

```text
123
Person [id=1, name=John]
Person [id=2, name=Alice]
Person [id=2, name=John]
```

<div class="mt-6 text-sm">

- `Person` **переопределил** (override) поведение родителя
- `println` про `Person` ничего не знает и по-прежнему вызывает `toString()` — но теперь выполняется версия `Person`

</div>

</div>

---
layout: two-cols
commit: "e128d5d"
---

# Один объект — разные типы

```java
Person p1 = new Person();
p1.id = 1;
p1.name = "John";
Object p2 = new Person();
System.out.println(p1);
System.out.println(p2);
p1.id = 2;
System.out.println(p1);
```

```text
123
Person [id=1, name=John]
Person [id=0, name=null]
Person [id=2, name=John]
```

::right::

<div class="pl-8 pt-16">

### Тип переменной и тип объекта

- `p2` — переменная типа `Object`, объект в ней — `Person`
- через `Object` полей `id` и `name` **не видно**: `p2.id = 2` не скомпилируется. Поэтому поля `p2` остались по умолчанию — `0` и `null`
- но `p2.toString()` выполняет версию `Person`

<div class="mt-6 callout text-sm">

**Полиморфизм**: какой код выполнится, решает **объект**, а не тип переменной. Тип переменной решает только, **что можно вызвать**.

</div>

</div>

<!--
В комментарии в коммите для p2 указан вывод «Person [id=2, name=Alice]» — он остался от предыдущего шага.
Реальный вывод — «Person [id=0, name=null]»: поля через тип Object не задать.
-->

---
layout: default
---

# Из литературы: полиморфизм

<div class="quote">

> *Moreover, dynamic binding lets you substitute objects that have identical interfaces for each other at run-time. This substitutability is known as polymorphism, and it's a key concept in object-oriented systems.*

Более того, динамическое связывание позволяет во время выполнения подставлять друг вместо друга объекты с одинаковыми интерфейсами. Такая взаимозаменяемость называется полиморфизмом; это ключевое понятие объектно-ориентированных систем. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Ch. 1, sect. 1.6 «Specifying Object Interfaces»</div>

</div>

<div class="quote">

> *…polymorphism (also called dynamic binding or late binding or run-time binding)…*

…полиморфизм (его также называют динамическим, поздним связыванием или связыванием во время выполнения)… <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 8 «Polymorphism», p. 193</div>

</div>

<div class="quote">

> *When I send a message to an object, it has complete leeway to interpret that message as it sees fit. The receiver of the message decides the appropriate response and how to respond.*

Когда я посылаю объекту сообщение, он волен истолковать его так, как считает нужным. Что и как ответить, решает получатель сообщения. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">West D. Object Thinking. — Microsoft Press, 2004. ISBN 978-0-7356-3751-1. Ch. 5, p. 140</div>

</div>

---
layout: two-cols
commit: "bb7889d"
---

# Области видимости

```java
package ru.chelgu.oot.class01;

public class Person extends Object {
    // private, - (package/default), protected, public
    private int id;
    private String name;
    ...
}
```

<div class="text-sm opacity-60 mt-2">
Теперь <code>p1.id = 1</code> в <code>Main</code> не компилируется
</div>

::right::

<div class="pl-8 pt-12">

| Модификатор       | Кому доступно          |
|-------------------|------------------------|
| `private`         | только этому классу    |
| *(нет)* — package | классам того же пакета |
| `protected`       | пакету + наследникам   |
| `public`          | всем                   |

<div class="mt-6 text-sm">

Без модификатора поля были доступны всему пакету — поэтому `Main` и мог их менять. `private` закрывает поля: менять состояние объекта теперь может **только сам объект**.

</div>

</div>

---
layout: default
---

# Из литературы: сокрытие реализации

<div class="quote">

> *The goal of the class creator is to build a class that exposes only what's necessary to the client programmer and keeps everything else hidden. Why? Because if it's hidden, the client programmer can't access it, which means that the class creator can change the hidden portion at will without worrying about the impact on anyone else.*

Цель создателя класса — построить класс, который открывает программисту-клиенту только необходимое, а всё остальное прячет. Зачем? Если что-то скрыто, клиент не может к нему обратиться, а значит, создатель класса может менять скрытую часть как угодно, не беспокоясь, что это кого-то затронет. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 1, sect. «The hidden implementation», p. 19</div>

</div>

<div class="quote">

> *So the first reason for access control is to keep client programmers' hands off portions they shouldn't touch…*

Итак, первая причина управления доступом — не подпускать программистов-клиентов к тому, чего им не следует касаться… <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 1, p. 20</div>

</div>

---
layout: two-cols
commit: "bb7889d"
---

# Конструкторы

```java
public class Person extends Object {
    private int id;
    private String name;

    Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() { ... }
}
```

```java
Person p1 = new Person(1, "John");
Object p2 = new Person(2, "Alice");
```

::right::

<div class="pl-8 pt-16">

### Вывод

```text
123
Person [id=1, name=John]
Person [id=2, name=Alice]
```

<div class="mt-6 text-sm">

- **конструктор** — код, который выполняется при `new`; объект появляется сразу заполненным
- `this.id` — поле объекта, `id` — параметр конструктора
- поля `private`, но через конструктор их можно задать и для `Object p2`
- пока у класса нет ни одного конструктора, Java добавляет пустой — именно его вызывал `new Person()`

</div>

</div>

---
layout: statement
---

# Данные + поведение над ними = класс

<div class="mt-6 text-xl opacity-70">
В структурной программе данные лежат отдельно, а правила работы с ними разбросаны по коду.<br>
В ООП класс хранит данные <b>вместе</b> с операциями над ними.
</div>

<!--
Сравните Person со слайда «Класс как структура данных» (только поля, менять может кто угодно)
и Person после конструкторов (поля закрыты, объект сам решает, что с ними делать).
-->

---
layout: default
---

# Из литературы: класс

<div class="quote">

> *Encapsulation creates new data types by combining characteristics and behaviors. Implementation hiding separates the interface from the implementation by making the details private.*

Инкапсуляция создаёт новые типы данных, объединяя характеристики и поведение. Сокрытие реализации отделяет интерфейс от реализации, делая детали закрытыми (private). <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 8 «Polymorphism», p. 193</div>

</div>

<div class="quote">

> *A class is an abstract data type equipped with a possibly partial implementation.*

Класс — это абстрактный тип данных, снабжённый реализацией, возможно частичной. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Meyer B. Object-Oriented Software Construction, 2nd ed. — Prentice Hall, 1997. ISBN 0-13-629155-4. Ch. 6 «Abstract data types»</div>

</div>

<div class="quote">

> *The fact that a computer program consists of data and functions does not mean that the nonsoftware world is so composed.*

Из того, что компьютерная программа состоит из данных и функций, не следует, что так же устроен мир за её пределами. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">West D. Object Thinking. — Microsoft Press, 2004. ISBN 978-0-7356-3751-1. Ch. 3 «From Philosophy to Culture», p. 74</div>

</div>

---
layout: two-cols
commit: "01dbf6c"
---

# Инварианты и ограничения

```java
public class Person extends Object
        implements Renameable {
    // private, - (package/default), protected, public
    private int id;
    private String name;

    Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void rename(String newName) {
        this.name = newName;
    }

    public String toString() { ... }
}
```

::right::

<div class="pl-8 pt-16">

### Инвариант

**Инвариант** — условие, которое верно для объекта всегда, на протяжении всей его жизни.

- `id` задаётся в конструкторе, и **нет метода**, который его меняет → `id` не меняется
- имя меняется, но **только через `rename`** — одно место, где можно проверить новое имя

<div class="mt-6 callout text-sm">

Попробуйте сами: добавьте в конструктор и `rename` проверку «имя не пустое». Сколько мест пришлось поменять? А сколько пришлось бы, если бы поля оставались открытыми?

</div>

</div>

---
layout: two-cols
commit: "01dbf6c"
---

# Публичные методы — контракт класса

```java
public class Person ... {
    private int id;                       // скрыто
    private String name;                  // скрыто

    Person(int id, String name) { ... }   // пакет

    public void rename(String newName)    // API
    public String toString()              // API
}
```

::right::

<div class="pl-8 pt-16">

### Контракт = API = интерфейс класса

- **публичные методы** — то, что класс обещает своим пользователям
- всё `private` можно менять, не трогая пользователей класса: хранить имя иначе, добавить кэш…
- изменение **контракта** ломает всех, кто его использует

<div class="mt-6 text-sm opacity-70">

Чем меньше и понятнее контракт, тем дешевле менять всё остальное.

</div>

</div>

---
layout: default
---

# Из литературы: интерфейс и тип

<div class="quote">

> *The set of all signatures defined by an object's operations is called the interface to the object. … A type is a name used to denote a particular interface.*

Множество всех сигнатур, определённых операциями объекта, называется интерфейсом этого объекта. … Тип — это имя, обозначающее конкретный интерфейс. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Ch. 1, sect. 1.6 «Specifying Object Interfaces»</div>

</div>

<div class="quote">

> *Together these lists constitute our behavioral promise to the world at large. We (speaking as an object) do not share with the world any notion of our internal structure and certainly no sense of how we do what we do.*

Вместе эти списки [предоставляемых услуг и видимых состояний] составляют наше поведенческое обещание всему миру. Мы (говоря от лица объекта) не делимся с миром никакими сведениями о своём внутреннем устройстве и уж точно не рассказываем, как делаем то, что делаем. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">West D. Object Thinking. — Microsoft Press, 2004. ISBN 978-0-7356-3751-1. Ch. 5, p. 121</div>

</div>

---
layout: two-cols
commit: "01dbf6c"
---

# Интерфейсы

```java
package ru.chelgu.oot.class01;

public interface Renameable {
    /**
     * Задаёт сущности новое имя
     */
    void rename(String newName);
}
```

```java
public class Person extends Object
        implements Renameable {
    ...
    @Override
    public void rename(String newName) {
        this.name = newName;
    }
}
```

::right::

<div class="pl-8 pt-16">

### Контракт отдельно от реализации

- **интерфейс** — контракт без реализации: только что умеет объект, без как
- `implements` — класс обещает выполнить контракт; компилятор проверит, что `rename` есть
- `extends` — только от **одного** класса, `implements` — **сколько угодно** интерфейсов
- переименовать можно не только человека: товар, файл, группу — каждый реализует `Renameable` по-своему

</div>

---
layout: two-cols
commit: "6f05907"
---

# Ещё больше типов

```java
Person p1 = new Person(1, "John");
Object p2 = new Person(2, "Alice");
Renameable p3 = new Person(3, "Mike");
p3.rename("Mikel");
System.out.println(p1);
System.out.println(p2);
System.out.println(p3);
```

```text
123
Person [id=1, name=John]
Person [id=2, name=Alice]
Person [id=3, name=Mikel]
```

::right::

<div class="pl-8 pt-16">

### Один `Person` — три типа

| Тип переменной | Что можно вызвать          |
|----------------|----------------------------|
| `Person`       | всё публичное в `Person`   |
| `Renameable`   | `rename` + методы `Object` |
| `Object`       | `toString`, `equals`, …    |

<div class="mt-6 callout text-sm">

Тип выбираем на том **уровне абстракции, которого здесь хватает**. Коду, который только переименовывает, нужен `Renameable` — тогда он подойдёт для любого переименуемого объекта, а не только для `Person`.

</div>

</div>

<!--
В комментарии в коммите для p3 указано «id=2» — опечатка, реальный вывод «Person [id=3, name=Mikel]».
-->

---
layout: default
---

# Из литературы: программировать на уровне интерфейса

<div class="quote">

> *This so greatly reduces implementation dependencies between subsystems that it leads to the following principle of reusable object-oriented design: Program to an interface, not an implementation.*

Это настолько ослабляет зависимости подсистем от реализации, что приводит к следующему принципу повторно используемого объектно-ориентированного дизайна: программируйте на уровне интерфейса, а не реализации. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Ch. 1, sect. 1.6 «Class versus Interface Inheritance»</div>

</div>

<div class="quote">

> *The interface keyword produces a completely abstract class, one that provides no implementation at all. … An interface provides only a form, but no implementation.*

Ключевое слово interface создаёт полностью абстрактный класс, в котором нет никакой реализации. … Интерфейс задаёт только форму, но не реализацию. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 9 «Interfaces», p. 222</div>

</div>

<div class="text-sm opacity-70 mt-2">

`Renameable p3` — пример: коду после этой строки неважно, что внутри `Person`.

</div>

---
layout: two-cols-header
---

# Композиция vs наследование

::left::

<div class="text-lg font-bold mb-2">Наследование — «является»</div>

```java
class Student extends Person {
    private String group;
}
```

<div class="text-sm mt-2">

- `Student` получает **весь** контракт `Person` и может подменить его поведение
- связь жёсткая и навсегда: изменения в `Person` сразу касаются `Student`
- нельзя быть наследником двух классов сразу

</div>

::right::

<div class="text-lg font-bold mb-2">Композиция — «имеет»</div>

```java
class Person {
    private Address address;
}
```

<div class="text-sm mt-2">

- `Person` **использует** `Address` через его публичный контракт
- часть можно заменить, в том числе во время работы программы
- частей может быть сколько угодно

</div>

<div class="mt-6 callout text-sm">

Правило по умолчанию: **сначала композиция**. Наследование — когда наследник действительно может заменить родителя везде, где тот используется. Подробно — на занятии 2.

</div>

<!--
Пример иллюстративный, в коммитах class01 его нет.
-->

---
layout: default
---

# Из литературы: композиция и наследование

<div class="quote">

> *That leads us to our second principle of object-oriented design: Favor object composition over class inheritance.*

Это подводит нас ко второму принципу объектно-ориентированного дизайна: предпочитайте композицию объектов наследованию классов. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Ch. 1, sect. 1.6 «Putting Reuse Mechanisms to Work»</div>

</div>

<div class="quote">

> *Favoring object composition over class inheritance helps you keep each class encapsulated and focused on one task.*

Предпочтение композиции наследованию помогает сохранить каждый класс инкапсулированным и сосредоточенным на одной задаче. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Ch. 1, sect. 1.6</div>

</div>

<div class="quote">

> *The is-a relationship is expressed with inheritance, and the has-a relationship is expressed with composition.*

Отношение «является» (is-a) выражается наследованием, а отношение «имеет» (has-a) — композицией. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 7 «Reusing Classes», p. 179</div>

</div>

---
layout: default
---

# Из литературы: объект

<div class="quote">

> *Booch offers an even more succinct description of an object: An object has state, behavior and identity.*

Буч даёт ещё более сжатое описание объекта: объект обладает состоянием, поведением и идентичностью. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 1 «Introduction to Objects», p. 17</div>

</div>

<div class="quote">

> *…each object can be uniquely distinguished from every other object—to put this in a concrete sense, each object has a unique address in memory.*

…каждый объект можно однозначно отличить от любого другого — говоря конкретнее, у каждого объекта свой уникальный адрес в памяти. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 1, p. 17</div>

</div>

<div class="text-sm opacity-70 mt-2">

У `Person` на этом шаге есть состояние (`id`, `name`) и идентичность (`@4517d9a3`), но ещё нет поведения.

</div>

---
layout: default
---

# Из литературы: всё — объект

<div class="quote">

> *Everything is an object. … A program is a bunch of objects telling each other what to do by sending messages. … Every object has a type.*

Всё есть объект. … Программа — это набор объектов, которые говорят друг другу, что делать, посылая сообщения. … У каждого объекта есть тип. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. 1 «Introduction to Objects», p. 16 — пять характеристик Smalltalk по Алану Кэю</div>

</div>

<div class="quote">

> *Everything is an object! An object is anything capable of providing a limited set of useful services.*

Всё есть объект! Объект — это всё, что способно предоставлять ограниченный набор полезных услуг. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">West D. Object Thinking. — Microsoft Press, 2004. ISBN 978-0-7356-3751-1. Ch. 5 «Vocabulary: Words to Think With», p. 121</div>

</div>

<div class="text-sm opacity-70 mt-2">

В нашем `Main` объекты — это и `"123"`, и `System.out`, и `Main.class`.

</div>

---
layout: default
---

# Итоги части 1

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-4"><span class="accent font-bold">①</span> Структура — только данные. <b>Класс</b> в ООП — данные вместе с поведением над ними.</div>

<div class="mb-4"><span class="accent font-bold">②</span> Каждый класс — наследник <code>Object</code>; наследник может <b>переопределить</b> поведение родителя.</div>

<div class="mb-4"><span class="accent font-bold">③</span> <b>Полиморфизм</b>: что вызвать можно, решает тип переменной; что выполнится — объект.</div>

<div class="mb-4"><span class="accent font-bold">④</span> <code>private</code> и конструктор позволяют объекту самому держать свои <b>инварианты</b>.</div>

<div class="mb-4"><span class="accent font-bold">⑤</span> Публичные методы — <b>контракт</b>; интерфейс — контракт без реализации, их может быть много.</div>

<div class="mb-4"><span class="accent font-bold">⑥</span> Тип выбираем на нужном <b>уровне абстракции</b>; композиция — по умолчанию, наследование — по необходимости.</div>

</div>

---
layout: section
---

# Часть 2

## Ответственность и абстракция на примере OrderFlow

<div class="text-sm opacity-60 mt-6">
Код — модуль <code>class01OrderFlow</code>: пакет <code>legacy</code> шаг за шагом перерастает в <code>domain</code> / <code>app</code> / <code>infra</code>
</div>

---
layout: statement
---

# Пришло требование

<div class="mt-8 text-2xl">
«Добавьте <b>самовывоз</b>: бесплатно, забрать можно в день заказа»
</div>

<div class="mt-6 text-base opacity-60">
— отдел логистики, понедельник, 9:40
</div>

<!--
Простое требование, первая оценка — «полчаса работы». Дальше смотрим, во что оно обходится в legacy-коде.
-->

---
layout: two-cols
commit: "c491f5f"
---

# Где это править?

```java
double deliveryCost;
if ("express".equals(delivery)) {
    deliveryCost = 500;
} else if ("courier".equals(delivery)) {
    deliveryCost = total >= 5000 ? 0 : 300;
} else {
    System.out.println(
        "Ошибка: неизвестный тип доставки " + delivery);
    return null;
}
total += deliveryCost;
```

<div class="text-sm opacity-60 mt-2">
<code>OrderService.place()</code> — один из девяти параметров, <code>String delivery</code>
</div>

::right::

<div class="pl-8 pt-16">

### Что придётся тронуть

- ветку расчёта стоимости в `place()`
- текст уведомления (там свой `"express"`)
- `deliveryDays()` — срок
- `report()` — статистику по тарифам
- `Main` и всех, кто передаёт строку
- ...и надеяться, что нигде нет `"Courier"` с большой буквы

</div>

<!--
Одно требование логистики — пять мест в одном файле, и ни одно из них не подскажет компилятор: тип доставки — просто строка.
-->

---
layout: default
commit: "c491f5f"
---

<div class="text-sm accent font-bold mb-2">В IDEA делали · шаг 1</div>

# Найти всё, что знает о доставке

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

Модуль `class01OrderFlow`, пакет `legacy`

1. `Ctrl+Shift+F` → `"express"`
2. пройти по каждому вхождению
3. посчитать: **сколько методов** и **сколько файлов**

</div>

<div>

<div class="callout">

Вопрос себе: сколько из этих мест вы нашли бы **без поиска по строке**?

</div>

</div>

</div>

<!--
Вхождения "express": place() — дважды (расчёт стоимости и текст уведомления), deliveryDays(), report(), плюс Main.
В report() считается только express — попросят статистику по самовывозу, и это ещё одно место.
-->

---
layout: statement
---

# Дизайн — это управление ценой изменения

<div class="mt-6 text-xl opacity-70">
Мера: сколько мест я открою, чтобы выполнить требование
</div>

<!--
Главный тезис курса. Требования меняются всегда. Дизайн не отменяет изменения, он решает, сколько мест каждое из них затронет.
Эту метрику — «сколько файлов я открою» — будем считать весь семестр.
-->

---
layout: default
---

# Требования за следующий год

| Что попросят                   | Кто попросит | Где править сегодня                            |
|--------------------------------|--------------|------------------------------------------------|
| Самовывоз, ПВЗ, постаматы      | логистика    | `place()`, `deliveryDays()`, `report()`        |
| Промокод «бесплатная доставка» | маркетинг    | `place()` — между промокодом и доставкой       |
| Страховка только от товаров    | бухгалтерия  | `place()`, а потом `pay()` сравнивает `double` |
| Уведомления в Telegram         | поддержка    | `place()`, `pay()`, `ship()`, `cancel()`       |
| Хранить в PostgreSQL           | эксплуатация | всё, где есть `row[5]`                         |

<div class="mt-6 text-sm opacity-60">
Пять ролей. Один файл. Каждая правка одной роли рискует сломать работу другой.
</div>

<!--
Смотрите на средний столбец: пять разных ролей просят менять один и тот же файл — это ключ к следующему слайду.
Самое опасное требование спорно: Telegram трогает четыре метода, а промокод на доставку — место, где две логики уже переплетены.
-->

---
layout: default
---

# Ответственность — это причина изменения

<div class="mt-4 text-lg">
У класса одна ответственность, если есть <b>одна роль</b>, по запросу которой его меняют.
</div>

<div class="grid grid-cols-2 gap-10 mt-8">

<div>

<div class="text-sm opacity-60 mb-3">Что делает <code>place()</code></div>

- проверяет email и количество → **поддержка**
- считает сумму товаров → **бухгалтерия**
- применяет промокод → **маркетинг**
- считает доставку → **логистика**
- накидывает страховку → **бухгалтерия**
- кладёт `Object[]` в `HashMap` → **эксплуатация**
- выбирает SMS или email → **поддержка**

</div>

<div>

<div class="text-2xl mt-8 mb-4">
<code>OrderService</code> → <b class="bad">5 причин</b> изменения
</div>

<div class="text-sm leading-relaxed">

Тест: **кто напишет вам письмо**, если это придётся поменять?

Одна роль → один класс. Не «один метод» и не «одна операция».

</div>

</div>

</div>

<!--
Частая ошибка: SRP = «у класса один метод». Нет — ответственность определяется тем, КТО требует изменения.
Бухгалтерия встречается дважды: сумма и страховка, вероятно, должны жить вместе.
-->

---
layout: default
---

# Из литературы: ответственность

<div class="quote">

> *The UML defines a responsibility as "a contract or obligation of a classifier".*

UML определяет ответственность как «контракт или обязательство классификатора». <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Larman C. Applying UML and Patterns, 2nd ed. — Prentice Hall, 2002. ISBN 0-13-092569-1. Ch. 16, sect. 16.1 «Responsibilities and Methods», p. 216</div>

</div>

<div class="quote">

> *A module should be responsible to one, and only one, actor.*

Модуль должен отвечать перед одним и только одним актором (actor — группа людей, которая требует изменений). <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Martin R. C. Clean Architecture. — Prentice Hall, 2017. ISBN 978-0-13-449416-6. Ch. 7 «SRP: The Single Responsibility Principle»</div>

</div>

---
layout: two-cols-header
commit: "c491f5f"
---

# Деньги — не `double`

::left::

<div class="text-lg font-bold mb-2 bad">Что делает legacy</div>

```java
// place(): страховка 3%
total = total * 1.03;   // 3490 → 3594.7000000000003

// pay(): клиент платит ровно по чеку
if (amount != total) {  // 3594.70 != 3594.7000000000003
    System.out.println("Ошибка: оплата ...");
    return false;
}
```

<div class="text-sm opacity-60 mt-2">
Клиент заплатил правильно. Заказ не оплачен.
</div>

::right::

<div class="text-lg font-bold mb-2 good">Почему так</div>

<div class="text-base leading-relaxed">

`double` — двоичная дробь. `0.1`, `1.03`, `3594.70` в ней **непредставимы точно**.

`0.1 + 0.2 == 0.30000000000000004`

Деньги считают в **целых копейках** — и прячут это решение внутри одного типа.

</div>

<!--
Это настоящий баг, его воспроизводили в IDEA (следующий слайд).
BigDecimal — тоже вариант; копейки в long проще и достаточны для рублей.
-->

---
layout: default
commit: "c491f5f"
---

<div class="text-sm accent font-bold mb-2">В IDEA делали · шаг 2</div>

# Воспроизвести баг

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

1. запустить `legacy.Main`
2. найти в выводе: заказ Бориса **не оплачен**
3. поставить точку останова в `pay()`, посмотреть `total`

</div>

<div>

```text
[EMAIL anna@example.ru] Заказ ORD-1 оплачен
...
Ошибка: оплата 3594.7 не совпадает
        с суммой заказа 3594.7000000000003
...
Заказов: 3, экспресс: 1, выручка: 62481.0 руб.
```

</div>

</div>

<div class="mt-8 callout text-sm">

Выручка тоже посчитана в `double` — и в отчёте для бухгалтерии будет то же самое, только незаметнее.

</div>

<!--
Обратите внимание: сообщение об ошибке выглядит как ошибка клиента и маскирует настоящую причину — округление double.
-->

---
layout: two-cols
commit: "c04ab98"
---

# `Money`: решение о том, что скрыть

```java
public record Money(long kopecks)
        implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    public Money {
        if (kopecks < 0) {
            throw new IllegalArgumentException(
                "Сумма не может быть отрицательной: "
                + kopecks);
        }
    }

    public static Money rubles(long rubles) {
        return new Money(Math.multiplyExact(rubles, 100));
    }

    public Money plus(Money other) { ... }
    public Money times(int factor) { ... }
}
```

::right::

<div class="pl-8 pt-16">

<div class="callout">

**Абстракция** — это решение о том, что скрыть, а не новое имя для старых данных.

</div>

<div class="mt-6 text-base">

`Money` скрывает:

- что внутри копейки, а не рубли
- как складывать без переполнения
- что отрицательных сумм не бывает

</div>

<div class="mt-6 text-sm opacity-60">
Не абстракции: <code>OrderManager</code>, <code>DataHelper</code>, <code>Utils</code> — их нельзя объяснить одним предложением.
</div>

</div>

<!--
Проверка абстракции: у неё есть поведение (plus, times), инвариант (≥ 0), и её можно объяснить одним предложением («сумма в рублях, точная до копейки»).
Компактный конструктор record — единственное место, где рождается Money, значит, проверка одна.
-->

---
layout: default
commit: "c04ab98"
---

<div class="text-sm accent font-bold mb-2">В IDEA делали · шаг 3</div>

# Выделить `Money`

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

1. создать `domain/Money` как `record`
2. попробовать `new Money(-500)` — исключение
3. запустить `MoneyTest`
4. `legacy.OrderService`: `double` → `Money`, запустить `legacy.Main` — оплата Бориса проходит

</div>

<div>

```java
@Test
void double_теряет_копейки() {
    assertThat(3490 * 1.03).isNotEqualTo(3594.70);
}

@Test
void отрицательную_сумму_создать_нельзя() {
    assertThatThrownBy(() -> new Money(-500))
        .isInstanceOf(IllegalArgumentException.class);
}
```

</div>

</div>

<!--
minus, percent и isAtLeast появились, потому что их потребовал legacy-код: промокоды, страховка, пороги. Методов «на будущее» нет — гибкость без требования — лишний код.
equals/hashCode/toString record даёт бесплатно.
-->

---
layout: two-cols-header
commit: "df13718"
---

# Примитивозависимость

::left::

<div class="text-lg font-bold mb-2 bad">Было</div>

```java
public String place(String name, String email,
                    String phone, String[] items,
                    int[] qty, double[] prices,
                    String delivery, boolean insurance,
                    String promo)
```

<div class="text-sm opacity-60 mt-2">

Девять параметров, шесть из них — `String` или массив. Поменяйте `email` и `phone` местами — компилятор промолчит.

</div>

::right::

<div class="text-lg font-bold mb-2 good">Стало</div>

```java
public OrderId place(Customer customer,
                     List<OrderItem> items,
                     String delivery,
                     boolean insurance,
                     String promo)
```

<div class="text-sm opacity-60 mt-2">

Покупателя и позиции уже не перепутать: типы разные. Невалидный `Email` не создаётся, три параллельных массива не рассинхронизируются. Но `String delivery` и `String promo` — ещё строки: это следующий шаг.

</div>

<!--
Тип — самая дешёвая документация и самый быстрый тест.
Параллельные массивы items/qty/prices: инвариант «одинаковая длина» нигде не проверяется. OrderItem делает нарушение невыразимым — нельзя иметь цену без товара.
Проверки email и количества ушли из place() в Email и OrderItem: невалидное значение просто не создать.
Дальше сигнатура только сужается: DeliveryTariff вместо строки, Promo вместо "SAVE10", и к концу — PlaceOrder.place(Customer, List<OrderItem>, DeliveryTariff).
-->

---
layout: two-cols
commit: "ed6c60a"
---

# Тариф — объект, а не строка

```java
public interface DeliveryTariff {
    Money cost(Money goods);
    int days();
}

public record CourierDelivery()
        implements DeliveryTariff {

    private static final Money PRICE =
        Money.rubles(300);
    private static final Money FREE_FROM =
        Money.rubles(5000);

    public Money cost(Money goods) {
        return goods.isAtLeast(FREE_FROM)
            ? Money.ZERO : PRICE;
    }

    public int days() { return 3; }
}
```

::right::

<div class="pl-8 pt-16">

### Что исчезло из legacy

- ветка `if ("express"...)` в `place()`
- «Ошибка: неизвестный тип доставки» — неизвестного тарифа не бывает
- `deliveryDays()` — срок знает сам тариф
- строки `"express"` / `"courier"` в `Main`

<div class="mt-6 text-sm opacity-60">

Осталось: `instanceof ExpressDelivery` в тексте уведомления и в `report()`. Это знание поддержки и бухгалтерии — разнесём по ролям чуть позже.

</div>

</div>

<!--
Стоимость и срок — вопросы логистики, и теперь они в одном месте: в тарифе.
Код, который считает доставку, больше не знает, какие тарифы бывают. Подробно о полиморфизме — на занятии 2.
-->

---
layout: two-cols-header
commit: "bdc054b"
---

# Инкапсуляция — это не `private`

::left::

<div class="text-lg font-bold mb-2 bad">Поля private, инвариант утёк</div>

```java
public Order(..., List<OrderItem> items, ...) {
    if (items.isEmpty()) {
        throw new IllegalArgumentException(
            "Заказ не может быть пустым");
    }
    this.items = items;          // чужой список
}

public List<OrderItem> items() {
    return items;                // отдаём свой
}

// где-то в отчёте:
order.items().clear();           // пустой заказ
```

::right::

<div class="text-lg font-bold mb-2 good">Инвариант держит сам объект</div>

```java
public Order(..., List<OrderItem> items, ...) {
    if (items.isEmpty()) {
        throw new IllegalArgumentException(
            "Заказ не может быть пустым");
    }
    this.items = List.copyOf(items);
}

public List<OrderItem> items() {
    return items;   // неизменяемая копия
}
```

<div class="text-sm opacity-60 mt-4">

Инкапсуляция — это **невозможность нарушить инвариант**, а не отсутствие публичных полей.

</div>

<!--
Распространённое убеждение «инкапсуляция = private + геттеры» неверно.
Две дыры слева: снаружи можно изменить исходный список и очистить полученный. List.copyOf закрывает обе: копия при входе, неизменяемость при выходе.
-->

---
layout: default
---

# Из литературы: инкапсуляция

<div class="quote">

> *The result of hiding a representation and implementation in an object. The representation is not visible and cannot be accessed directly from outside the object.*

[Инкапсуляция —] результат сокрытия представления и реализации в объекте. Представление не видно и недоступно напрямую извне объекта. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Glossary, «encapsulation»</div>

</div>

<div class="quote">

> *Every object has a boundary separating public and private realms. … Encapsulation defines the "insides" (structural definition and enabling mechanisms) of an object as private space, to or of which no one except the object itself should have access or knowledge.*

У каждого объекта есть граница, разделяющая публичную и частную области. … Инкапсуляция объявляет «внутренности» объекта (его структуру и механизмы работы) частным пространством, к которому никто, кроме самого объекта, не должен иметь ни доступа, ни знания о нём. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">West D. Object Thinking. — Microsoft Press, 2004. ISBN 978-0-7356-3751-1. Ch. 5, p. 141</div>

</div>

<div class="text-sm opacity-70 mt-2">

Список `items`, отданный наружу без копии, — это доступ к «внутренностям» `Order`.

</div>

---
layout: default
commit: "42c8942"
---

<div class="text-sm accent font-bold mb-2">В IDEA делали · шаг 4</div>

# Собрать `Order` и сломать его

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

1. `Order` с `this.items = items` — **сначала с дырой**
2. написать тест, который чистит `items()` — он **проходит**, заказ пуст
3. заменить на `List.copyOf` — тест падает с `UnsupportedOperationException`
4. переписать тест: теперь он проверяет, что очистить **нельзя**

</div>

<div>

```java
@Test
void позиции_нельзя_удалить_снаружи() {
    var order = new Order(id, customer, items,
                          new CourierDelivery());

    assertThatThrownBy(() -> order.items().clear())
        .isInstanceOf(
            UnsupportedOperationException.class);
}
```

</div>

</div>

<!--
Главный момент демо: private не спас. Так пишут почти все.
legacy при этом не меняется: он уже хранит Order, и исправление в одном классе закрывает дыру для всех, кто его использует.
-->

---
layout: default
commit: "c5236c4"
---

# Значимый объект и сущность

<div class="grid grid-cols-2 gap-12 mt-6">

<div>

### Значимый объект <span class="text-sm opacity-60 font-normal">value object</span>

- равенство **по значению**
- неизменяемый, копируется свободно
- в Java — обычно `record`

<div class="mt-4 text-lg">

`Money` `OrderId` `Email` `OrderItem`

</div>

</div>

<div>

### Сущность <span class="text-sm opacity-60 font-normal">entity</span>

- равенство **по идентификатору**
- состояние меняется, важен жизненный цикл
- в Java — `final class` с `equals` по `id`

<div class="mt-4 text-lg">

`Order` `Customer`

</div>

</div>

</div>

```java
@Override
public boolean equals(Object o) {           // Order
    return o instanceof Order other && id.equals(other.id);
}
```

<div class="callout text-sm mt-2">

Проверка: два объекта с одинаковыми данными — **один и тот же или разные?** Два `rubles(100)` неразличимы. Два заказа на 100 ₽ — разные заказы.

</div>

<!--
Спорный случай: Customer у нас record, но он сущность (есть CustomerId) — сознательное упрощение занятия 1, к корректным equals/hashCode вернёмся на занятии 2.
Деление определяет, где объект можно делать изменяемым, а где нельзя.
-->

---
layout: default
---

# Из литературы: значимый объект

<div class="quote">

> *Objects that are equal due to the value of their properties, in this case their x and y coordinates, are called value objects.*

Объекты, равенство которых определяется значениями их свойств — в данном случае координат x и y, — называются значимыми объектами (value objects). <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Fowler M. ValueObject. — <a href="https://martinfowler.com/bliki/ValueObject.html">martinfowler.com/bliki/ValueObject.html</a> (сетевые ресурсы курса)</div>

</div>

<div class="text-sm opacity-70 mt-2">

Там же Фаулер приводит деньги — сумму и валюту — как типичный значимый объект. Подробнее — в его книге Patterns of Enterprise Application Architecture (паттерны Value Object и Money).

</div>

---
layout: two-cols
commit: "c5236c4"
---

# Инварианты заказа

<div class="text-sm opacity-60 mb-4">Сначала спецификация, потом код</div>

- в заказе **хотя бы одна** позиция
- количество в позиции **> 0**
- статус только вперёд:<br>`NEW → PAID → SHIPPED`
- отправленный заказ **не отменить**

<div class="mt-6 text-sm">

Где проверяем:

1. **конструктор** — объект рождается валидным
2. **единственная точка мутации** — не портится потом

</div>

::right::

<div class="pl-6 pt-16">

```java
public void pay() {
    requireStatus(OrderStatus.NEW);
    status = OrderStatus.PAID;
}

public void ship() {
    requireStatus(OrderStatus.PAID);
    status = OrderStatus.SHIPPED;
}

private void requireStatus(OrderStatus expected) {
    if (status != expected) {
        throw new IllegalStateException(
            "Заказ " + id + " в статусе " + status
            + ", ожидался " + expected);
    }
}
```

<div class="mt-3 text-sm opacity-60">
Сеттера <code>setStatus()</code> нет. Совсем.
</div>

</div>

<!--
Список инвариантов — это АТД: операции + инварианты до кода.
В legacy статус — row[5] = "PAID", писать туда может кто угодно и что угодно, включая "PAYED".
Если инвариант проверяется «где-то в сервисе» — его нет: сервисов будет три, проверка — в одном.
-->

---
layout: two-cols
commit: "44f50aa"
---

# Пять ролей — пять мест

| Роль         | Куда переехало                             |
|--------------|--------------------------------------------|
| бухгалтерия  | `Money`, `Order.total()`, `SalesReport`    |
| маркетинг    | `Promo`                                    |
| логистика    | `DeliveryTariff`                           |
| поддержка    | `Notifier` → `ConsoleNotifier`             |
| эксплуатация | `OrderRepository` → `InMemoryOrderRepository` |

<div class="text-sm opacity-60 mt-4">
Пакет <code>legacy</code> удалён: <code>domain</code> — правила, <code>app</code> — сценарии и порты, <code>infra</code> — консоль и память.
</div>

::right::

<div class="pl-6 pt-16">

```java
public final class PlaceOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public PlaceOrder(OrderRepository orders,
                      Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    public Order place(Customer customer,
                       List<OrderItem> items,
                       DeliveryTariff delivery) { ... }
}
```

<div class="callout text-sm mt-3">

Зависимости — **через конструктор**. В тесте вместо консоли — `RecordingNotifier`, вместо базы — память.

</div>

</div>

<!--
Это ответ на таблицу «Требования за следующий год»: те же пять ролей, но у каждой теперь своё место.
Telegram — новая реализация Notifier, PostgreSQL — новая реализация OrderRepository; сценарии и Order не меняются.
Сценарии рядом с PlaceOrder: PayOrder, ShipOrder, CancelOrder. Вывод app.Main совпадает с выводом legacy.Main символ в символ — это рефакторинг, а не переписывание.
-->

---
layout: two-cols
commit: "538029a"
---

# Самовывоз: один новый класс

```java
/** Самовывоз: бесплатно, в день заказа. */
public record Pickup() implements DeliveryTariff {

    @Override
    public Money cost(Money goods) {
        return Money.ZERO;
    }

    @Override
    public int days() {
        return 0;
    }
}
```

```text
$ git show --stat
 .../app/Main.java      |  4 ++++
 .../domain/Pickup.java | 15 +++++++++++++++
 .../OrderTest.java     |  7 +++++++
 3 files changed, 26 insertions(+)
```

::right::

<div class="pl-8 pt-16">

### То самое требование логистики

|               | legacy       | сейчас      |
|---------------|--------------|-------------|
| мест править  | 5            | 1 новый класс |
| подскажет компилятор | нет   | да          |
| старый код    | меняем       | не трогаем  |

<div class="callout mt-6">

Дизайн — это управление **ценой изменения**.

</div>

</div>

<!--
Возвращаемся к первому слайду части: «Добавьте самовывоз». В legacy это пять мест в OrderService, и ни одно не подскажет компилятор.
Здесь Order, сценарии, уведомления и отчёт не тронуты, старые тесты зелёные. Для практики: где ещё осталось знание о конкретном тарифе? (instanceof ExpressDelivery в ConsoleNotifier и SalesReport.)
-->

---
layout: two-cols
---

# Связность и сцепление

<div class="text-sm opacity-60 mb-4">Как измерить на нашем коде</div>

### Связность (cohesion)

Насколько содержимое класса — про одно.

<div class="text-sm mt-2">

**Тест:** уберите поле — сколько методов перестанет компилироваться?<br>
В `OrderService` поле `store` нужно всем — но **по разным причинам**.

</div>

::right::

<div class="pl-8 pt-16">

### Сцепление (coupling)

Насколько класс знает устройство соседей.

<div class="text-sm mt-2">

**Тест:** сколько классов надо открыть, чтобы понять этот?

`row[5]` — это статус. Об этом знают `OrderService`, `InMemoryStore` и комментарий в шапке. Поменяйте порядок полей — сломается всё молча.

</div>

<div class="mt-6 text-sm opacity-70">

> Максимизируем связность, минимизируем сцепление. Ноль сцепления недостижим: система из ничего не работает.

</div>

</div>

<!--
Order после рефакторинга: все поля нужны total/pay/ship — высокая связность.
InMemoryStore с package-private полем orders — пример сцепления по устройству: OrderService лезет прямо в HashMap. Вернёмся к этому на занятиях 4 и 7.
-->


---
layout: default
---

# Из литературы: сцепление и связность

<div class="quote">

> *Coupling is a measure of how strongly one element is connected to, has knowledge of, or relies on other elements.*

Сцепление — мера того, насколько сильно элемент связан с другими элементами, знает о них или от них зависит. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Larman C. Applying UML and Patterns, 2nd ed. — Prentice Hall, 2002. ISBN 0-13-092569-1. Ch. 16, sect. 16.8 «Low Coupling», p. 229</div>

</div>

<div class="quote">

> *In terms of object design, cohesion (or more specifically, functional cohesion) is a measure of how strongly related and focused the responsibilities of an element are.*

В объектном дизайне связность (точнее, функциональная связность) — мера того, насколько тесно связаны между собой и сфокусированы обязанности элемента. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Larman C. Applying UML and Patterns, 2nd ed. — Prentice Hall, 2002. ISBN 0-13-092569-1. Ch. 16, sect. 16.9 «High Cohesion», p. 232</div>

</div>

---
layout: default
---

# Что уносим с занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> Класс — <b>данные + поведение</b>; публичные методы — его <b>контракт</b>, интерфейс — контракт без реализации.</div>

<div class="mb-5"><span class="accent font-bold">②</span> <b>Полиморфизм</b>: тип переменной решает, что можно вызвать, объект — что выполнится. Тип выбираем на нужном уровне абстракции.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Мера дизайна — <b>цена изменения</b>: сколько мест придётся открыть.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Ответственность определяется тем, <b>кто попросит изменения</b>, а не тем, что класс умеет.</div>

<div class="mb-5"><span class="accent font-bold">⑤</span> Абстракция — <b>решение о том, что скрыть</b>; инкапсуляция — <b>невозможность нарушить инвариант</b>.</div>

</div>

---
layout: end
---

# Дальше

Занятие 2 · Подстановка и паттерны поведения

Вопросы по материалу — в чате курса
