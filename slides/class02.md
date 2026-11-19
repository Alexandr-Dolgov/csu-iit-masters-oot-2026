---
theme: default
title: Занятие 2 · Подстановка и паттерны поведения
info: |
  Занятие 2 курса «Объектно-ориентированные технологии»
  Магистратура ЧелГУ, 1 курс, 1 семестр

  Программы: 09.04.04 «Программная инженерия», 02.04.02 «ФИИТ»
class: text-left
highlighter: shiki
lineNumbers: false
transition: slide-left
slideNumbers: true
exportFilename: ООТ-02
---

# Занятие 2

## Подстановка и паттерны поведения

<div class="pt-10 text-sm opacity-60">
Объектно-ориентированные технологии
</div>

<div class="text-sm opacity-50">
Магистратура. Институт информационных технологий. ЧелГУ
<br> · 09.04.04 «Программная инженерия»
<br> · 02.04.02 «Фундаментальная информатика и информационные технологии»
</div>

<!--
Код: модули class02base (Часть 1, команда) и class02OrderFlow (Часть 2).
Хэш внизу слайда — git checkout <hash>, состояние кода для слайда.
-->

---
layout: default
---

# План занятия

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

### Часть 1 · подстановка

- что обещает тип: контракт подтипа
- `Square extends Rectangle`
- подстановка в JDK: `List.of`, `Stack`, `Timestamp`
- контракт `equals` и `hashCode`
- `Point` и `ColorPoint`: почему наследник ломает `equals`
- композиция и записи
- `Customer` — сущность

</div>

<div>

### Часть 2 · паттерны поведения

- стратегия
- состояние
- шаблонный метод
- итератор
- команда
- строитель
- совместная задача: промокоды

</div>

</div>

---
layout: default
---

# С прошлого занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">②</span> <b>Полиморфизм</b>: тип переменной решает, что можно вызвать, объект — что выполнится.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Мера дизайна — <b>цена изменения</b>: сколько мест придётся открыть.</div>

</div>

<div class="callout mt-8">

Самовывоз добавили одним классом: `Pickup implements DeliveryTariff`, `Order` не тронули.

Это сработало, потому что `Order` **можно подставить любой** тариф. А любой ли? Что должен выполнять подтип, чтобы код, написанный для базового типа, не сломался?

</div>

---
layout: section
---

# Часть 1

## Подстановка

---
layout: default
---

# Что обещает тип

<div class="text-lg mt-2 mb-6 opacity-80">
Сигнатуры методов проверяет компилятор. Обещания — нет.
</div>

| Что               | Кто отвечает | Пример для `Rectangle.setWidth(w)`      |
|-------------------|--------------|-----------------------------------------|
| **предусловие**   | вызывающий   | `w > 0`                                 |
| **постусловие**   | метод        | ширина стала `w`, **высота не изменилась** |
| **инвариант**     | класс        | площадь = ширина × высота               |

<div class="callout text-sm mt-6">

Вместе с сигнатурами это и есть **контракт** из занятия 1. Идея проектирования по контракту (Design by Contract) — у Мейера; в Java контракт живёт в Javadoc и в тестах.

</div>

<!--
Компилятор проверит, что у подкласса есть setWidth(int). Что setWidth не трогает высоту — не проверит никто, кроме теста.
-->

---
layout: two-cols
commit: "2ddf286"
---

# Квадрат — это прямоугольник?

```java
public class Rectangle {

    private int width;
    private int height;

    /** Постусловие: ширина стала width,
        высота не изменилась. */
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) { ... }

    public int area() {
        return width * height;
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
public class Square extends Rectangle {

    public Square(int side) {
        super(side, side);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        super.setHeight(width);
    }

    @Override
    public void setHeight(int height) {
        super.setWidth(height);
        super.setHeight(height);
    }
}
```

<div class="text-sm opacity-60 mt-2">
По геометрии — да. Чтобы остаться квадратом, он меняет обе стороны.
</div>

</div>

---
layout: two-cols
commit: "2ddf286"
---

# Клиент не знает про квадраты

```java
/** Растянуть блок по ширине колонки,
    высоту не трогать. */
public static void fitToColumn(Rectangle block,
                               int columnWidth) {
    block.setWidth(columnWidth);
}
```

<div class="text-sm opacity-60 mt-2">
<code>Layout</code> написан в терминах <code>Rectangle</code> и полагается на его постусловие.
</div>

::right::

<div class="pl-8">

```java
@Test
void растянуть_квадрат_по_колонке() {
    Rectangle block = new Square(5);

    Layout.fitToColumn(block, 10);

    // Клиент ждал 10 × 5 = 50
    assertThat(block.area()).isEqualTo(100);
    assertThat(block.height()).isEqualTo(10);
}
```

<div class="callout text-sm mt-4">

Компилятор доволен, наследование «правильное», а `Layout` получил не то, что ему обещали. Квадрат **ослабил постусловие** `setWidth`.

</div>

</div>

---
layout: default
---

# Из литературы: принцип подстановки

<div class="quote">

> *What is wanted here is something like the following substitution property: If for each object o1 of type S there is an object o2 of type T such that for all programs P defined in terms of T, the behavior of P is unchanged when o1 is substituted for o2 then S is a subtype of T.*

Нужно примерно такое свойство подстановки: если для каждого объекта o1 типа S найдётся объект o2 типа T, такой, что для всех программ P, определённых в терминах T, поведение P не меняется при подстановке o1 вместо o2, то S — подтип T. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Liskov B. Data Abstraction and Hierarchy. SIGPLAN Notices 23(5), 1988 — цит. по: Larman C. Applying UML and Patterns, 2nd ed. — Prentice Hall, 2002. ISBN 0-13-092569-1. Ch. 22 «GRASP: More Patterns for Assigning Responsibilities», p. 336</div>

</div>

<div class="quote">

> *Informally, software (methods, classes, ...) that refers to a type T (some interface or abstract superclass) should work properly or as expected with any substituted implementation or subclass of T.*

Неформально: код, который ссылается на тип T (интерфейс или абстрактный суперкласс), должен работать правильно с любой подставленной реализацией или подклассом T. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же</div>

</div>

---
layout: default
---

# Правила для подтипа

<div class="grid grid-cols-2 gap-10 mt-4">

<div>

Подтип может:

- **ослабить предусловие** — принимать больше
- **усилить постусловие** — обещать больше
- добавить новые методы

Подтип не может:

- требовать от вызывающего больше
- обещать меньше
- нарушить инвариант базового типа
- бросать исключения, которых базовый тип не объявлял

</div>

<div>

<div class="callout text-sm">

**Тест:** возьмите тесты базового типа и прогоните их на подтипе. Упал хоть один — подставлять нельзя.

`RectangleTest` на `Square` падает: квадрат обещает меньше.

</div>

<div class="text-sm opacity-60 mt-6">

«Является» в предметной области (квадрат — прямоугольник) не означает «является» в коде. В коде подтип определяется **поведением**, а не таксономией.

</div>

</div>

</div>

---
layout: two-cols
commit: "14dff8e"
---

# Квадрат — сосед, а не наследник

```java
/** Общее у фигур — только то,
    что верно для всех: площадь. */
public interface Shape {
    int area();
}

public record Rectangle(int width, int height)
        implements Shape {

    public Rectangle withWidth(int width) {
        return new Rectangle(width, height);
    }

    @Override
    public int area() { return width * height; }
}

public record Square(int side) implements Shape {

    public Rectangle toRectangle() {
        return new Rectangle(side, side);
    }
    ...
}
```

::right::

<div class="pl-8 pt-16">

```java
public static Rectangle fitToColumn(
        Rectangle block, int columnWidth) {
    return block.withWidth(columnWidth);
}
```

<div class="callout text-sm mt-4">

Два решения сразу:

- **неизменяемость**: «поменять ширину» — получить новый прямоугольник, ломать нечего;
- общий тип `Shape` обещает **только** то, что верно и для квадрата.

</div>

<div class="text-sm opacity-60 mt-4">
Квадрат, которому нужно растянуться, явно становится прямоугольником: <code>toRectangle()</code>.
</div>

</div>

---
layout: two-cols
commit: "d923a19"
---

# Подстановка в JDK: List.of

```java
/** Клиент, написанный в терминах List:
    по типу ему разрешено добавлять. */
private static void addDefault(List<String> tags) {
    tags.add("new");
}

addDefault(new ArrayList<>(List.of("sale")));  // ok

addDefault(List.of("sale"));
// UnsupportedOperationException

addDefault(Collections.unmodifiableList(...));
// UnsupportedOperationException
```

::right::

<div class="pl-8">

```java
// java.util.List
/**
 * Appends the specified element to the end
 * of this list (optional operation).
 * ...
 * @throws UnsupportedOperationException
 *   if the add operation is not supported
 *   by this list
 */
boolean add(E e);
```

<div class="callout text-sm mt-4">

Контракт `List` **заранее разрешил** реализациям отказаться: «optional operation». Формально подстановка соблюдена, на деле клиент узнаёт о ней в рантайме.

</div>

<div class="text-sm opacity-60 mt-3">
Компромисс авторов коллекций: иначе пришлось бы держать отдельные <code>ReadableList</code> и <code>MutableList</code>.
</div>

</div>

---
layout: two-cols
commit: "d923a19"
---

# JDK: Stack и Timestamp

```java
Stack<String> stack = new Stack<>();
stack.push("a");
stack.push("b");

stack.add(0, "под низ");  // метод Vector

stack.pop();               // "b"
stack.firstElement();      // "под низ"
```

<div class="text-sm opacity-70 mt-2">

`Stack extends Vector`: стек получил все операции списка. Javadoc сам советует другое:

</div>

<div class="text-xs italic opacity-70 mt-1">
A more complete and consistent set of LIFO stack operations is provided by the Deque interface and its implementations, which should be used in preference to this class.
</div>

::right::

<div class="pl-8">

```java
long now = 1_700_000_000_000L;
Date date = new Date(now);
Timestamp timestamp = new Timestamp(now);

date.equals(timestamp);   // true
timestamp.equals(date);   // false
```

<div class="text-sm opacity-70 mt-2">

`java.sql.Timestamp extends java.util.Date` и добавляет наносекунды — `equals` перестаёт быть симметричным.

</div>

<div class="text-xs italic opacity-70 mt-1">
The inheritance relationship between Timestamp and java.util.Date really denotes implementation inheritance, and not type inheritance.
</div>

<div class="callout text-sm mt-4">

Оба класса — из JDK 1.0–1.1. Наследование ради **повторного использования кода**, а не ради подтипа.

</div>

</div>

---
layout: statement
---

# `equals` — тоже контракт

<div class="text-lg opacity-70 mt-4">
и самый частый способ нарушить подстановку незаметно
</div>

---
layout: default
---

# Из литературы: контракт `equals`

<div class="quote">

> *A proper equals( ) must satisfy the following five conditions:*
> 1. *Reflexive: For any x, x.equals(x) should return true.*
> 2. *Symmetric: For any x and y, x.equals(y) should return true if and only if y.equals(x) returns true.*
> 3. *Transitive: For any x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.*
> 4. *Consistent: For any x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the object is modified.*
> 5. *For any non-null x, x.equals(null) should return false.*

Правильный `equals` должен выполнять пять условий: рефлексивность, симметричность, транзитивность, согласованность и `x.equals(null) == false` для любого ненулевого `x`. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. «Containers in Depth», p. 606</div>

</div>

<div class="text-sm opacity-60">
Тот же список — в Javadoc <code>Object.equals</code>. Плюс к нему: равные объекты обязаны иметь равные <code>hashCode</code>.
</div>

---
layout: two-cols
commit: "799d3ec"
---

# equals без hashCode

```java
public class Point {

    private final int x;
    private final int y;
    ...
    @Override
    public boolean equals(Object o) {
        return o instanceof Point p
                && x == p.x && y == p.y;
    }
}
```

::right::

<div class="pl-8">

```java
List<Point> visited = List.of(new Point(1, 2));
visited.contains(new Point(1, 2));   // true

Set<Point> visited = new HashSet<>();
visited.add(new Point(1, 2));
visited.contains(new Point(1, 2));   // false
```

<div class="callout text-sm mt-4">

`List` ищет перебором через `equals`. `HashSet` сначала выбирает **корзину по `hashCode`** и только в ней сравнивает `equals`.

Без `hashCode` у равных точек хэши разные (от `Object`): поиск идёт не в той корзине.

</div>

</div>

---
layout: two-cols
commit: "16dcbbc"
---

# Наследник с новым полем

```java
// Point
@Override
public int hashCode() {
    return 31 * x + y;
}
```

```java
public class ColorPoint extends Point {

    private final String color;
    ...
    @Override
    public boolean equals(Object o) {
        return o instanceof ColorPoint cp
                && super.equals(cp)
                && color.equals(cp.color);
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
Point plain = new Point(1, 2);
ColorPoint red = new ColorPoint(1, 2, "red");

plain.equals(red);   // true  — Point сравнивает координаты
red.equals(plain);   // false — ColorPoint требует ColorPoint
```

<div class="bad font-bold mt-4">Симметричность нарушена.</div>

<div class="text-sm opacity-70 mt-2">

Положите `plain` в `List`, спросите `contains(red)` — ответ зависит от того, с какой стороны вызовут `equals`.

</div>

</div>

---
layout: two-cols
commit: "7088d9e"
---

# Починить симметричность

```java
/** С обычной точкой сравниваемся без цвета —
    ради симметричности. */
@Override
public boolean equals(Object o) {
    if (!(o instanceof Point)) {
        return false;
    }
    if (!(o instanceof ColorPoint cp)) {
        return o.equals(this);
    }
    return super.equals(cp)
            && color.equals(cp.color);
}
```

::right::

<div class="pl-8 pt-16">

```java
ColorPoint red  = new ColorPoint(1, 2, "red");
Point plain     = new Point(1, 2);
ColorPoint blue = new ColorPoint(1, 2, "blue");

red.equals(plain);   // true
plain.equals(blue);  // true
red.equals(blue);    // false
```

<div class="bad font-bold mt-4">Транзитивность нарушена.</div>

<div class="text-sm opacity-70 mt-2">

Хуже: два разных наследника `Point`, сделанные так же, уходят в бесконечную взаимную рекурсию — `StackOverflowError`.

</div>

</div>

---
layout: two-cols
commit: "c2a2725"
---

# getClass вместо instanceof

```java
/** Равны только объекты ровно одного класса:
    симметрично и транзитивно. */
@Override
public boolean equals(Object o) {
    if (o == null || o.getClass() != getClass()) {
        return false;
    }
    Point p = (Point) o;
    return x == p.x && y == p.y;
}
```

```java
/** Новых значимых полей нет —
    это всё ещё точка. */
public class CountedPoint extends Point { ... }
```

::right::

<div class="pl-8 pt-16">

```java
Set<Point> grid = Set.of(new Point(0, 0),
                         new Point(1, 2));

grid.contains(new CountedPoint(1, 2));  // false
```

<div class="callout text-sm mt-4">

Контракт `equals` соблюдён, **подстановка — нет**. `CountedPoint` — подтип `Point`, но клиент, написанный в терминах `Point`, его не находит.

</div>

<div class="text-sm opacity-60 mt-3">
Так ведёт себя каждый <code>equals</code>, сгенерированный IDE по шаблону «getClass». Это осознанный выбор, а не безопасное умолчание.
</div>

</div>

---
layout: default
---

# Из литературы: наследование и `equals`

<div class="quote">

> *There is no way to extend an instantiable class and add a value component while preserving the equals contract, unless you're willing to forgo the benefits of object-oriented abstraction.*

Невозможно расширить инстанцируемый класс, добавив значимое поле, и при этом сохранить контракт `equals` — разве что отказавшись от преимуществ объектно-ориентированной абстракции. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Bloch J. Effective Java, 3rd ed. — Addison-Wesley, 2018. ISBN 978-0-13-468599-1. Item 10 «Obey the general contract when overriding equals»</div>

</div>

<div class="callout mt-6">

Выход, который предлагает Блох там же: **композиция вместо наследования** (Item 18). Цветная точка не *является* точкой, а *содержит* её.

</div>

---
layout: two-cols
commit: "6421ab0"
---

# Композиция и записи

```java
/**
 * Точка — запись: equals, hashCode и toString
 * по компонентам пишет компилятор.
 * Запись final: подкласса, который сломает
 * её equals, не будет.
 */
public record Point(int x, int y) {
}

/** Цветная точка содержит точку,
    а не является ею. */
public record ColorPoint(Point point, String color) {

    public ColorPoint(int x, int y, String color) {
        this(new Point(x, y), color);
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
ColorPoint red = new ColorPoint(1, 2, "red");

red.equals(new ColorPoint(1, 2, "red"));  // true
red.equals(new Point(1, 2));              // false

Set<Point> grid = Set.of(new Point(0, 0),
                         new Point(1, 2));
grid.contains(red.point());               // true
```

<div class="callout text-sm mt-4">

Нужна точка — попросите её явно: `red.point()`. Никакой «почти точки», которая ведёт себя то так, то иначе.

</div>

<div class="text-sm opacity-60 mt-3">
Значимые объекты из занятия 1 — <code>Money</code>, <code>OrderId</code>, <code>Email</code> — уже записи. Теперь понятно, почему это удобно.
</div>

</div>

---
layout: two-cols
commit: "149ec4d"
---

# Обещанное: Customer — сущность

<div class="text-sm opacity-60 mb-2">На занятии 1 <code>Customer</code> был записью — сознательное упрощение</div>

```java
// занятие 1
public record Customer(CustomerId id, String name,
                       Email email, String phone) { }
```

<div class="text-sm mt-2">

Запись сравнивается **по всем полям**: клиент сменил телефон — стал другим клиентом.

</div>

::right::

<div class="pl-8">

```java
/** Клиент — сущность: равенство
    по идентификатору. */
public final class Customer {

    private final CustomerId id;
    private final String name;
    private final Email email;
    private final String phone;
    ...
    @Override
    public boolean equals(Object o) {
        return o instanceof Customer other
                && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
```

<div class="text-sm opacity-60 mt-2">
<code>final</code> — как и у <code>Order</code>: наследников, которые сломают <code>equals</code>, не будет.
</div>

</div>

<!--
Модуль class02OrderFlow, первый шаг после копии конечного состояния class01OrderFlow.
Проверка из занятия 1: два объекта с одинаковыми данными — один и тот же или разные? Для клиента решает id.
-->

---
layout: default
---

# Итоги части 1

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> Подтип обязан выполнять <b>контракт</b> базового типа, а не только его сигнатуры.</div>

<div class="mb-5"><span class="accent font-bold">②</span> Проверка подстановки — <b>тесты базового типа на подтипе</b>.</div>

<div class="mb-5"><span class="accent font-bold">③</span> <code>equals</code> и <code>hashCode</code> — контракт; наследник с новым значимым полем его не сохранит.</div>

<div class="mb-5"><span class="accent font-bold">④</span> <b>Композиция</b> и <b>неизменяемость</b> снимают большую часть проблем; записи делают это дёшево.</div>

</div>

---
layout: section
---

# Часть 2

## Паттерны поведения на примере OrderFlow

---
layout: default
---

# Паттерн — именованное решение повторяющейся проблемы

<div class="text-lg mt-2 mb-6 opacity-80">
Сегодня — паттерны о том, как <b>варьировать поведение</b>
</div>

| Проблема: что меняется                         | Паттерн          | Уже видели                       |
|------------------------------------------------|------------------|----------------------------------|
| алгоритм, выбранный снаружи                    | стратегия        | `DeliveryTariff`, `Promo`        |
| поведение в зависимости от состояния объекта   | состояние        | `OrderStatus` + `requireStatus`  |
| шаги алгоритма при неизменном скелете          | шаблонный метод  | —                                |
| способ обхода, не раскрывая устройство         | итератор         | `List.copyOf` в `Order`          |
| когда и сколько раз выполнить действие         | команда          | —                                |
| как собрать сложный объект                     | строитель        | `Order` на шесть аргументов      |

<div class="text-sm opacity-60 mt-4">
Каждый из них — полиморфизм из занятия 1 и подстановка из Части 1, применённые к конкретной проблеме.
</div>

---
layout: two-cols
commit: "bdb46bc"
---

# Стратегия: она уже есть

```java
/**
 * Тариф доставки: сколько стоит
 * и сколько дней везти.
 *
 * Паттерн «Стратегия»: Order — контекст,
 * тариф — сменяемый алгоритм.
 */
public interface DeliveryTariff {

    Money cost(Money goods);

    int days();
}
```

```java
// Order — контекст
public Money total() {
    Money goods = promo.apply(goods());
    Money total = goods.plus(delivery.cost(goods));
    ...
}
```

::right::

<div class="pl-8 pt-16">

```java
// SalesReport: как сравнивать —
// стратегия, которую передаём в max
orders.findAll().stream()
        .max(Comparator.comparing(Order::total))
        .map(order -> order.id().toString())
        .orElse("—");
```

<div class="callout text-sm mt-4">

Контекст знает **когда** вызвать алгоритм, стратегия — **как** посчитать. Выбирает стратегию тот, кто создаёт контекст.

</div>

<div class="text-sm opacity-60 mt-3">
Самовывоз на занятии 1 — это новая стратегия. Мы применили паттерн, не называя его.
</div>

</div>

---
layout: default
---

# Стратегия в JDK

| Где                                       | Контекст                       | Стратегия                             |
|-------------------------------------------|--------------------------------|---------------------------------------|
| `list.sort(cmp)`, `TreeMap(cmp)`          | алгоритм сортировки, дерево    | `Comparator`                          |
| `new ThreadPoolExecutor(..., handler)`    | пул потоков                    | `RejectedExecutionHandler`: что делать, если очередь полна |
| `Files.walk(...).filter(p)`               | обход                          | `Predicate`                           |
| `Collectors.groupingBy(f)`                | группировка                    | `Function`                            |

<div class="callout text-sm mt-6">

Почти любой параметр функционального типа — стратегия. Лямбда — самый короткий способ записать конкретную стратегию: `Comparator.comparing(Order::total)`.

</div>

<!--
Открыть в IDEA ThreadPoolExecutor и найти AbortPolicy, CallerRunsPolicy, DiscardPolicy, DiscardOldestPolicy — четыре готовые стратегии.
-->

---
layout: default
---

# Из литературы: стратегия

<div class="quote">

> *Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.*

Определите семейство алгоритмов, инкапсулируйте каждый и сделайте их взаимозаменяемыми. Стратегия позволяет менять алгоритм независимо от клиентов, которые им пользуются. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E., Helm R., Johnson R., Vlissides J. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Strategy, «Intent», p. 315</div>

</div>

<div class="quote">

> *Creating a method that behaves differently depending on the argument object that you pass it is called the Strategy design pattern. The method contains the fixed part of the algorithm to be performed, and the Strategy contains the part that varies.*

Метод, который ведёт себя по-разному в зависимости от переданного объекта-аргумента, — это паттерн «Стратегия». Метод содержит неизменную часть алгоритма, стратегия — изменяемую. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Eckel B. Thinking in Java, 4th ed. — Prentice Hall, 2006. ISBN 0-13-187248-6. Ch. «Interfaces», p. 226</div>

</div>

---
layout: two-cols
commit: "149ec4d"
---

# Состояние: было

```java
public void pay() {
    requireStatus(OrderStatus.NEW);
    status = OrderStatus.PAID;
}

public void ship() {
    requireStatus(OrderStatus.PAID);
    status = OrderStatus.SHIPPED;
}

public void cancel() {
    if (status == OrderStatus.SHIPPED) {
        throw new IllegalStateException(...);
    }
    status = OrderStatus.CANCELLED;
}
```

```java
// CancelOrder
order.status() == OrderStatus.PAID ? ...
// SalesReport
order.status() == OrderStatus.PAID
        || order.status() == OrderStatus.SHIPPED
```

::right::

<div class="pl-8 pt-16">

<div class="text-lg font-bold mb-2 bad">Знание о переходах размазано</div>

- каждый метод `Order` проверяет статус по-своему
- `CancelOrder` и `SalesReport` сами решают, какие статусы «оплачены»
- новый статус «ожидает сборки» — правка в `Order`, `CancelOrder`, `SalesReport`

<div class="callout text-sm mt-4">

Тот же симптом, что у тарифа-строки на занятии 1: `if` по значению вместо объекта, который сам знает ответ.

</div>

</div>

---
layout: two-cols
commit: "0115c65"
---

# Состояние: стало

```java
/** Паттерн «Состояние»: каждый статус сам знает,
    куда из него можно перейти. */
public enum OrderStatus {

    NEW {
        @Override OrderStatus pay()    { return PAID; }
        @Override OrderStatus cancel() { return CANCELLED; }
    },
    PAID {
        @Override OrderStatus ship()   { return SHIPPED; }
        @Override OrderStatus cancel() { return CANCELLED; }
        @Override public boolean paid() { return true; }
    },
    SHIPPED {
        @Override public boolean paid() { return true; }
    },
    CANCELLED;

    /** Переходы по умолчанию запрещены. */
    OrderStatus pay()    { throw forbidden("оплатить"); }
    OrderStatus ship()   { throw forbidden("отправить"); }
    OrderStatus cancel() { throw forbidden("отменить"); }

    public boolean paid() { return false; }
}
```

::right::

<div class="pl-8 pt-16">

```java
// Order — контекст: просто делегирует
public void pay() {
    status = status.pay();
}

public void ship() {
    status = status.ship();
}

public void cancel() {
    status = status.cancel();
}
```

```java
// CancelOrder
order.status().paid() ? order.total() : Money.ZERO
```

<div class="callout text-sm mt-4">

Переходы — **в одном месте**. Методы перехода package-private: сменить статус в обход `Order` нельзя.

</div>

</div>

---
layout: default
---

# Из литературы: состояние

<div class="quote">

> *Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.*

Позволяет объекту менять поведение при смене внутреннего состояния. Со стороны кажется, что объект сменил класс. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. State, «Intent», p. 305</div>

</div>

<div class="quote">

> *It localizes state-specific behavior and partitions behavior for different states. [...] An alternative is to use data values to define internal states and have Context operations check the data explicitly. But then we'd have look-alike conditional or case statements scattered throughout Context's implementation.*

Паттерн локализует поведение, зависящее от состояния, и разделяет его по состояниям. [...] Альтернатива — хранить состояние данными и явно проверять их в операциях контекста. Но тогда похожие `if` и `switch` расползутся по всей реализации контекста. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, State, «Consequences», п. 1</div>

</div>

---
layout: default
---

# Стратегия или состояние?

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

### Стратегия

- выбирает **клиент** снаружи: `new ExpressDelivery()`
- за жизнь контекста обычно не меняется
- стратегии друг о друге не знают

</div>

<div>

### Состояние

- меняется **изнутри**: `status = status.pay()`
- меняется много раз за жизнь объекта
- состояния знают, в какие можно перейти

</div>

</div>

<div class="callout mt-8">

Структура одинакова: контекст держит ссылку на объект-интерфейс и делегирует ему. Различает их **намерение** — поэтому паттерн это не диаграмма классов, а проблема плюс решение.

</div>

---
layout: two-cols
commit: "9476809"
---

# Шаблонный метод

```java
/** Шаблонный метод: render — неизменный скелет,
    формат строк и итогов — шаги подклассов. */
public abstract class SalesReport {

    public final String render() {
        StringBuilder out = new StringBuilder();
        header(out);
        for (Order order : orders.findAll()) {
            // посчитать экспресс, выручку
            row(out, order);
        }
        footer(out, new Totals(...));
        return out.toString();
    }

    /** Крючок (hook): по умолчанию заголовка нет. */
    protected void header(StringBuilder out) {
    }

    protected abstract void row(StringBuilder out, Order o);
    protected abstract void footer(StringBuilder out, Totals t);
}
```

::right::

<div class="pl-8 pt-16">

```java
public final class CsvSalesReport
        extends SalesReport {

    @Override
    protected void header(StringBuilder out) {
        out.append("id;status;total_kopecks\n");
    }

    @Override
    protected void row(StringBuilder out,
                       Order order) {
        out.append(order.id()).append(';')
           .append(order.status()).append(';')
           .append(order.total().kopecks())
           .append('\n');
    }
    ...
}
```

<div class="text-sm opacity-60 mt-2">
<code>TextSalesReport</code> — тот же скелет, другие шаги.
</div>

</div>

---
layout: two-cols
commit: "9476809"
---

# Один скелет — два отчёта

```text
ORD-1  SHIPPED   3490.00 руб.
ORD-2  PAID      3594.70 руб.
ORD-3  SHIPPED   58991.00 руб.
ORD-4  NEW       3190.00 руб.
Заказов: 4, экспресс: 1, выручка: 66075.70 руб., крупнейший: ORD-3
```

```text
id;status;total_kopecks
ORD-1;SHIPPED;349000
ORD-2;PAID;359470
ORD-3;SHIPPED;5899100
ORD-4;NEW;319000
TOTAL;4;6607570
```

::right::

<div class="pl-8">

<div class="text-lg font-bold mb-2">Что важно</div>

- обход и подсчёт итогов написаны **один раз**
- `render()` — `final`: подкласс не может пропустить итоги или поменять порядок
- **абстрактные шаги** обязательны, **крючок** `header` — по желанию

<div class="callout text-sm mt-4">

Цена: наследование. Подкласс видит `protected`-члены родителя и зависит от его устройства. Третий формат — ещё один подкласс; два независимых измерения (формат × набор колонок) так уже не разложить.

</div>

</div>

---
layout: default
---

# Шаблонный метод в JDK и фреймворках

| Где                         | Скелет                                            | Шаги подкласса                  |
|-----------------------------|---------------------------------------------------|---------------------------------|
| `AbstractList`              | `iterator`, `contains`, `indexOf`, `equals`, `hashCode` | `get(int)`, `size()`       |
| `InputStream`               | `read(byte[], int, int)` в цикле зовёт `read()`   | `read()`                        |
| `HttpServlet.service`       | разбор метода запроса                             | `doGet`, `doPost`, ...          |
| JUnit 5                     | жизненный цикл: `@BeforeEach` → тест → `@AfterEach` | методы теста                  |

<div class="callout text-sm mt-6">

Javadoc `AbstractList`: *To implement an unmodifiable list, the programmer needs only to extend this class and provide implementations for the get(int) and size() methods.*

</div>

<div class="text-sm opacity-60 mt-4">

Spring `JdbcTemplate` называется «шаблоном», но устроен как **стратегия**: вы не наследуете его, а передаёте колбэк — `RowMapper`. Скелет тот же, точка расширения — композиция вместо наследования.

</div>

<!--
Открыть AbstractList в IDEA: реализовать список на два метода и получить for-each, contains, equals бесплатно.
-->

---
layout: default
---

# Из литературы: шаблонный метод

<div class="quote">

> *Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.*

Определите скелет алгоритма в операции, отложив некоторые шаги до подклассов. Шаблонный метод позволяет подклассам переопределять шаги, не меняя структуру алгоритма. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Template Method, «Intent», p. 325</div>

</div>

<div class="quote">

> *Template methods lead to an inverted control structure that's sometimes referred to as "the Hollywood principle," that is, "Don't call us, we'll call you".*

Шаблонные методы приводят к инвертированной структуре управления, которую иногда называют «голливудским принципом»: «Не звоните нам, мы сами вам позвоним». <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, «Consequences», p. 327</div>

</div>

<div class="text-sm opacity-60">
Это зародыш инверсии управления — к ней вернёмся на занятии 4, когда фреймворком будет Spring.
</div>

---
layout: two-cols
commit: "c6cae86"
---

# Итератор

```java
/** Паттерн «Итератор»: позиции можно обойти,
    но не получить список — ни индексов,
    ни size, ни add. */
public final class Order
        implements Iterable<OrderItem> {

    private final List<OrderItem> items;
    ...
    /** Итератор неизменяемой копии:
        remove бросит исключение. */
    @Override
    public Iterator<OrderItem> iterator() {
        return items.iterator();
    }
}
```

<div class="text-sm opacity-60 mt-2">
Вместо <code>public List&lt;OrderItem&gt; items()</code> из занятия 1.
</div>

::right::

<div class="pl-8 pt-16">

```java
for (OrderItem item : order) {
    quantity += item.quantity();
}

// компилятор разворачивает for-each в:
for (Iterator<OrderItem> it = order.iterator();
     it.hasNext(); ) {
    OrderItem item = it.next();
    quantity += item.quantity();
}
```

<div class="callout text-sm mt-4">

`Iterable` обещает меньше, чем `List`: только обход. Чем меньше обещает тип, тем больше реализаций можно подставить и тем меньше клиент может сломать.

</div>

</div>

---
layout: two-cols
---

# Итератор в JDK: fail-fast

```java
List<String> tags =
        new ArrayList<>(List.of("a", "b", "c"));

for (String tag : tags) {
    if (tag.equals("b")) {
        tags.remove(tag);
    }
}
// ConcurrentModificationException
```

```java
// так можно: удаление через сам итератор
tags.removeIf(tag -> tag.equals("b"));
```

::right::

<div class="pl-8">

<div class="text-xs italic opacity-70">
The iterators returned by this class's iterator and listIterator methods are fail-fast: if the list is structurally modified at any time after the iterator is created, in any way except through the iterator's own remove or add methods, the iterator will throw a ConcurrentModificationException.
</div>

<div class="text-xs opacity-50 mt-1">Javadoc <code>java.util.ArrayList</code></div>

<div class="callout text-sm mt-4">

Итератор ArrayList помнит счётчик изменений списка (`modCount`) и сверяет его на каждом `next()`. Лучше упасть сразу, чем вернуть мусор.

</div>

<div class="text-sm opacity-60 mt-3">
<code>Iterator</code>, <code>Spliterator</code>, <code>Stream</code>, <code>ResultSet</code>, <code>Scanner</code> — всё это итераторы: обход без знания устройства источника.
</div>

</div>

---
layout: default
---

# Из литературы: итератор

<div class="quote">

> *Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.*

Предоставляет способ последовательно обойти элементы составного объекта, не раскрывая его внутреннего представления. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Iterator, «Intent», p. 257</div>

</div>

<div class="callout mt-6">

«Не раскрывая представления» — та же мысль, что инкапсуляция на занятии 1. `Order` может завтра хранить позиции в `Map` по артикулу — клиенты for-each этого не заметят.

</div>

---
layout: two-cols
commit: "e1e77cd"
---

# Команда

```java
/** Запрос как объект: его можно сохранить,
    поставить в очередь, отменить. */
public interface Command {
    void execute();
    void undo();
}

/** Дописать в конец. Для отмены команда
    помнит, куда писала. */
public final class Append implements Command {
    ...
    public void execute() {
        position = document.length();
        document.insert(position, fragment);
    }

    public void undo() {
        document.delete(position, fragment.length());
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
/** Вызывающий: выполняет команды и хранит
    историю. Какие бывают команды — не знает. */
public final class Editor {

    private final Deque<Command> history =
            new ArrayDeque<>();

    public void run(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
```

<div class="text-sm opacity-60 mt-2">
Модуль <code>class02base</code>: в OrderFlow пока нет места, где команду не пришлось бы притягивать.
</div>

</div>

---
layout: two-cols
commit: "e1e77cd"
---

# Команда в JDK

```java
// создали в одном месте, выполнили в другом
List<Runnable> queue = List.of(
        () -> log.add("a"),
        () -> log.add("b"));
queue.forEach(Runnable::run);
```

```java
Callable<Integer> answer = () -> 6 * 7;

try (ExecutorService executor =
             Executors.newSingleThreadExecutor()) {
    Future<Integer> result = executor.submit(answer);
    result.get();   // 42
}
```

::right::

<div class="pl-8">

<div class="quote">

> *Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.*

Инкапсулируйте запрос в объект — тогда клиентов можно параметризовать разными запросами, ставить запросы в очередь, журналировать и отменять. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Command, «Intent», p. 233</div>

</div>

<div class="callout text-sm mt-2">

`ExecutorService` — вызывающий, который не знает, что за задачи ему дали. `Runnable` — команда без отмены, `Callable` — команда с результатом.

</div>

</div>

---
layout: two-cols
commit: "c6cae86"
---

# Строитель: проблема

```java
// PlaceOrder
public Order place(Customer customer,
                   List<OrderItem> items,
                   DeliveryTariff delivery) { ... }

public Order place(Customer customer,
                   List<OrderItem> items,
                   DeliveryTariff delivery,
                   Promo promo,
                   boolean insured) { ... }
```

```java
// Main
placeOrder.place(boris, kettleAndMugs,
        new CourierDelivery(), Promo.NONE, true);
```

::right::

<div class="pl-8 pt-16">

<div class="text-lg font-bold mb-2 bad">Что не так</div>

- `true` пятым аргументом — что это? Без IDE не прочитать
- **телескопические** перегрузки: каждый новый необязательный параметр — ещё одна
- подарочная упаковка, комментарий курьеру, промокод без страховки — комбинаций всё больше
- `Order` дублирует те же два конструктора

<div class="callout text-sm mt-4">

Строитель — **порождающий** паттерн, по программе он на занятии 3. Но проблема уже здесь, а решение — то же «вынести изменяемое в отдельный объект».

</div>

</div>

---
layout: two-cols
commit: "5b2d5ff"
---

# Строитель в OrderFlow

```java
public static final class Builder {

    private Customer customer;
    private final List<OrderItem> items = new ArrayList<>();
    private DeliveryTariff delivery;
    private Promo promo = Promo.NONE;
    private boolean insured = false;

    public Builder customer(Customer customer) {
        this.customer = customer;
        return this;
    }
    // item(...), items(...), delivery(...), promo(...)

    /** Вместо true пятым аргументом — имя. */
    public Builder insured() {
        this.insured = true;
        return this;
    }

    /** id выдаёт хранилище — он приходит последним. */
    public Order build(OrderId id) {
        return new Order(id, this);
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
// Main
placeOrder.place(Order.builder()
        .customer(boris)
        .items(kettleAndMugs)
        .delivery(new CourierDelivery())
        .insured());

// PlaceOrder: одна сигнатура
public Order place(Order.Builder draft) {
    Order order = draft.build(orders.nextId());
    ...
}
```

<div class="callout text-sm mt-4">

Конструктор `Order` закрыт: **недостроенного заказа не бывает**. Строитель копит параметры, инварианты проверяются один раз — в `build`.

</div>

</div>

---
layout: default
---

# Строитель в JDK

| Где                                  | Что собирает                             |
|--------------------------------------|------------------------------------------|
| `HttpClient.newBuilder()`            | HTTP-клиент: таймауты, редиректы, прокси |
| `HttpRequest.newBuilder(uri)`        | запрос: заголовки, метод, тело           |
| `Locale.Builder`                     | локаль с проверкой каждого поля          |
| `Stream.builder()`                   | поток из элементов, добавленных по одному |
| `StringBuilder`                      | строку — но это скорее изменяемый буфер, чем паттерн GoF |

<div class="quote mt-6">

> *Separate the construction of a complex object from its representation so that the same construction process can create different representations.*

Отделите конструирование сложного объекта от его представления, чтобы один и тот же процесс конструирования мог создавать разные представления. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Builder, «Intent», p. 97</div>

</div>

<div class="text-sm opacity-60">
У GoF акцент на «разных представлениях» (один распорядитель, разные строители). Строитель в современной Java — вариант Блоха (Effective Java, Item 2): замена длинному конструктору.
</div>

---
layout: default
---

# Паттерны занятия по категориям GoF

| Паттерн          | Категория GoF  | В OrderFlow / class02base                    | В JDK                               |
|------------------|----------------|----------------------------------------------|-------------------------------------|
| стратегия        | поведенческий  | `DeliveryTariff`, `Comparator` в отчёте      | `Comparator`, `RejectedExecutionHandler` |
| состояние        | поведенческий  | `OrderStatus`                                | —                                   |
| шаблонный метод  | поведенческий  | `SalesReport.render()`                       | `AbstractList`, `InputStream`       |
| итератор         | поведенческий  | `Order implements Iterable`                  | `Iterator`, `Stream`                |
| команда          | поведенческий  | `Command`, `Editor`                          | `Runnable`, `Callable`              |
| строитель        | порождающий    | `Order.Builder`                              | `HttpClient.Builder`                |

<div class="callout text-sm mt-6">

Общее у всех: то, что меняется, вынесено в **отдельный объект за интерфейсом** — и этот объект можно подставить. Без принципа подстановки ни один из них не работает.

</div>

---
layout: default
---

<div class="text-sm accent font-bold mb-2">Совместная задача · у экрана</div>

# Промокоды с параметрами

<div class="grid grid-cols-2 gap-10 mt-4">

<div>

```java
public enum Promo {
    NONE   { ... },
    SAVE10 { ... },   // −10% на товары
    SAVE500 { ... };  // −500 руб. от 3000 руб.

    public abstract Money apply(Money goods);
}
```

<div class="text-sm opacity-70 mt-2">

`Promo` уже стратегия — но закрытая: набор вариантов зашит в `enum`.

</div>

</div>

<div>

Маркетинг хочет:

- «N% на товары» с любым N
- «минус X руб. от Y руб.» с любыми X и Y
- промокоды, созданные из админки, без релиза

<div class="callout text-sm mt-4">

Что станет с `Promo`? Где теперь живут `NONE`, `SAVE10`, `SAVE500`? Какие тесты `OrderTest` должны остаться без изменений?

</div>

</div>

</div>

---
layout: default
---

<div class="text-sm accent font-bold mb-2">Совместная задача · у экрана</div>

# Найдите нарушение подстановки

```java
/** Для отчётов: смотреть можно, менять — нет. */
public final class ReadOnlyOrderRepository implements OrderRepository {

    private final OrderRepository delegate;

    public ReadOnlyOrderRepository(OrderRepository delegate) { this.delegate = delegate; }

    @Override public OrderId nextId()               { throw new UnsupportedOperationException(); }
    @Override public void save(Order order)         { throw new UnsupportedOperationException(); }
    @Override public Optional<Order> find(OrderId id) { return delegate.find(id); }
    @Override public List<Order> findAll()          { return delegate.findAll(); }
}
```

<div class="callout text-sm mt-4">

Кто сломается, если передать его в `PlaceOrder`? Компилятор это пропустит? Чем это лучше или хуже `List.of` из Части 1? Как разделить `OrderRepository`, чтобы `SalesReport` не мог получить `save` вовсе?

</div>

<!--
Подсказка к разбору: SalesReport нужен только findAll — отдельный интерфейс на чтение. Это ISP, о нём подробно на занятии 4.
-->

---
layout: default
---

# Для практики

`instanceof ExpressDelivery` остался в двух местах:

```java
// ConsoleNotifier
if (order.delivery() instanceof ExpressDelivery) {
    text += " Экспресс-доставка: привезём завтра.";
}

// SalesReport.render()
if (order.delivery() instanceof ExpressDelivery) {
    express++;
}
```

<div class="callout mt-6">

Это знание о конкретном тарифе за пределами тарифа. Что сломается, если появится «экспресс в день заказа»? А «экспресс-самовывоз»? Какой из паттернов сегодняшнего занятия здесь подходит — и подходит ли какой-нибудь?

</div>

---
layout: default
---

# Что уносим с занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> Подтип определяется <b>поведением</b>, а не таксономией: тесты базового типа должны проходить на подтипе.</div>

<div class="mb-5"><span class="accent font-bold">②</span> <code>equals</code>/<code>hashCode</code> — контракт; для значимых объектов — записи, для сущностей — <code>final</code> и сравнение по id.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Паттерн — это <b>проблема плюс решение</b>; стратегия и состояние устроены одинаково, но решают разное.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Все паттерны занятия — один приём: то, что меняется, — <b>в отдельный объект за интерфейсом</b>.</div>

</div>

---
layout: end
---

# Дальше

Занятие 3 · Паттерны создания и обёртки

Вопросы по материалу — в чате курса
