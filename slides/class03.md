---
theme: default
title: Занятие 3 · Паттерны создания и обёртки
info: |
  Занятие 3 курса «Объектно-ориентированные технологии»
  Магистратура ЧелГУ, 1 курс, 1 семестр

  Программы: 09.04.04 «Программная инженерия», 02.04.02 «ФИИТ»
class: text-left
highlighter: shiki
lineNumbers: false
transition: slide-left
slideNumbers: true
exportFilename: ООТ-03
---

# Занятие 3

## Паттерны создания и обёртки

<div class="pt-10 text-sm opacity-60">
Объектно-ориентированные технологии
</div>

<div class="text-sm opacity-50">
Магистратура. Институт информационных технологий. ЧелГУ
<br> · 09.04.04 «Программная инженерия»
<br> · 02.04.02 «Фундаментальная информатика и информационные технологии»
</div>

<!--
Код: модули class03base (теория, демо — тесты) и class03OrderFlow (OrderFlow).
Хэш внизу слайда — git checkout <hash>, состояние кода для слайда.
-->

---
layout: default
---

# План занятия

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

### Часть 1 · создание

- чем жёсток `new`
- статические фабрики
- фабричный метод
- абстрактная фабрика
- одиночка — и почему это антипаттерн

</div>

<div>

### Часть 2 · обёртки

- адаптер
- декоратор
- прокси
- компоновщик
- совместная задача: SMS-шлюз для `Notifier`

</div>

</div>

---
layout: default
---

# С прошлого занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">③</span> Паттерн — это <b>проблема плюс решение</b>, а не диаграмма классов.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Всё, что меняется, — <b>в отдельный объект за интерфейсом</b>.</div>

</div>

<div class="callout mt-8">

Сценарии знают только `OrderRepository` и `Notifier`, а `Order` — только `DeliveryTariff`. Но кто-то всё равно пишет `new InMemoryOrderRepository()` и `new ExpressDelivery()`.

Сегодня два вопроса: **кто и как создаёт** объекты за интерфейсом и что можно сделать с готовым объектом, **не меняя его класс**.

</div>

---
layout: section
---

# Часть 1

## Создание

---
layout: default
---

# Чем жёсток `new`

<div class="grid grid-cols-2 gap-10 mt-4">

<div>

```java
new Money(349_000)          // 3490 руб. или 349 000?
Money.rubles(3490)          // понятно без IDE

new Integer(127)            // deprecated
Integer.valueOf(127)        // из кэша

new ArrayList<>(...)        // всегда ArrayList
List.of(...)                // какой — решает List
```

</div>

<div>

Конструктор:

- **всегда** создаёт новый объект
- ровно того класса, что написан после `new`
- имя у него одно — имя класса; различаются только списки параметров

<div class="callout text-sm mt-6">

Кто пишет `new X()`, тот зависит от класса `X`. Все паттерны создания отвечают на один вопрос: **кто решает, какой класс создать**.

</div>

</div>

</div>

---
layout: two-cols
commit: "6fb9de9"
---

# Статические фабрики

```java
List<Integer> two = List.of(1, 2);
List<Integer> three = List.of(1, 2, 3);

two.getClass().getSimpleName();     // "List12"
three.getClass().getSimpleName();   // "ListN"
```

```java
// −128..127 кэшируются
Integer.valueOf(127) == Integer.valueOf(127);  // true
Integer.valueOf(128) == Integer.valueOf(128);  // false

new Integer(127) == new Integer(127);          // false
```

```java
Optional.empty() == Optional.empty();          // true
```

::right::

<div class="pl-8">

<div class="quote">

> *A class can provide a public static factory method, which is simply a static method that returns an instance of the class.*

Класс может предоставить открытый статический фабричный метод — просто статический метод, который возвращает экземпляр класса. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Bloch J. Effective Java, 3rd ed. — Addison-Wesley, 2018. ISBN 978-0-13-468599-1. Item 1 «Consider static factory methods instead of constructors»</div>

</div>

Что может фабрика и не может конструктор:

- иметь **имя**: `Money.rubles`, `Order.builder()`
- вернуть **готовый** объект: кэш, единственный экземпляр
- вернуть **любой подтип**: класс реализации скрыт

<div class="text-sm opacity-60 mt-2">
Это не паттерн GoF, а идиома Java. Но идея та же: решение «что создать» уходит от клиента.
</div>

</div>

---
layout: two-cols
commit: "734ba81"
---

# Фабричный метод: он уже есть

```java
/** Где хранятся заказы, решает тот,
    кто собирает приложение. */
public interface OrderRepository {

    OrderId nextId();
    ...
}

// InMemoryOrderRepository
@Override
public OrderId nextId() {
    counter++;
    return new OrderId("ORD-" + counter);
}
```

```java
// PlaceOrder
Order order = draft.build(orders.nextId());
```

::right::

<div class="pl-8 pt-16">

```java
// занятие 2: Order implements Iterable
@Override
public Iterator<OrderItem> iterator() {
    return items.iterator();
}
```

<div class="callout text-sm mt-4">

`PlaceOrder` просит идентификатор, **не зная, как его создать**: `ORD-1`, UUID или последовательность в базе — решает реализация хранилища.

`iterator()` — то же самое: какой класс итератора создать, решает коллекция.

</div>

<div class="text-sm opacity-60 mt-3">
Метод интерфейса, который создаёт объект, а класс продукта выбирает реализация, — это и есть фабричный метод.
</div>

</div>

---
layout: two-cols
commit: "be67abf"
---

# Фабричный метод

```java
/** Шаблонный метод из занятия 2 +
    фабричный метод для продукта. */
public abstract class Exporter {

    public final String export(List<Line> lines) {
        Formatter formatter = createFormatter();
        StringBuilder out = new StringBuilder();
        for (Line line : lines) {
            out.append(formatter.format(line))
               .append('\n');
        }
        return out.toString();
    }

    /** Новый продукт на каждую выгрузку. */
    protected abstract Formatter createFormatter();
}
```

::right::

<div class="pl-8 pt-16">

```java
public final class TextExporter extends Exporter {

    @Override
    protected Formatter createFormatter() {
        return new NumberingFormatter();
    }

    /** Помнит номер строки. */
    private static final class NumberingFormatter
            implements Formatter {
        private int number = 0;
        ...
    }
}
```

```text
1. Чайник — 2490 руб.
2. Кружка — 350 руб.
```

<div class="text-sm opacity-60 mt-2">
<code>CsvExporter</code> возвращает лямбду: <code>Чайник;2490</code>. Форматтер с состоянием — поэтому на каждый вызов нужен новый, а не поле.
</div>

</div>

---
layout: default
---

# Фабричный метод в JDK

| Где                                 | Кто создаёт                         | Что                                 |
|-------------------------------------|-------------------------------------|-------------------------------------|
| `Collection.iterator()`             | каждая коллекция                    | свой итератор                       |
| `ThreadFactory.newThread(r)`        | фабрика, переданная в пул           | поток с нужным именем, приоритетом  |
| `NumberFormat.getInstance(locale)`  | `NumberFormat`                      | подкласс под локаль                 |
| `Charset.forName("UTF-8")`          | провайдер кодировок                 | реализацию кодировки                |

<div class="quote mt-6">

> *Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.*

Определите интерфейс для создания объекта, но оставьте подклассам решение, какой класс инстанцировать. Фабричный метод позволяет классу передать создание подклассам. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Factory Method, «Intent», p. 107</div>

</div>

<div class="text-sm opacity-60">
Там же, «Consequences»: <i>Factory methods eliminate the need to bind application-specific classes into your code.</i>
</div>

---
layout: two-cols
commit: "734ba81"
---

# Абстрактная фабрика: было

```java
// Main
OrderRepository orders = new InMemoryOrderRepository();
Notifier notifier = new ConsoleNotifier();
PlaceOrder placeOrder = new PlaceOrder(orders, notifier);
PayOrder payOrder = new PayOrder(orders, notifier);
...
```

```java
// OrderScenariosTest
InMemoryOrderRepository repository =
        new InMemoryOrderRepository();
RecordingNotifier notifier = new RecordingNotifier();
PlaceOrder placeOrder =
        new PlaceOrder(repository, notifier);
...
```

::right::

<div class="pl-8 pt-16">

<div class="text-lg font-bold mb-2 bad">Что не так</div>

- зависимости **ходят парами**: хранилище и нотификатор из одного окружения
- перепутать легко: `PayOrder` с другим хранилищем не найдёт заказ
- окружений станет больше: демо, тесты, база (занятие 7), SMS

<div class="callout text-sm mt-4">

Нужен объект, который отдаёт **всё семейство сразу** и гарантирует, что его члены подходят друг к другу.

</div>

</div>

---
layout: two-cols
commit: "25b7605"
---

# Абстрактная фабрика: стало

```java
/** Семейство зависимостей сценариев.
    Члены согласованы: хранилище одно на всех. */
public interface Infrastructure {

    OrderRepository orders();

    Notifier notifier();
}

/** Для демо: в памяти, в консоль. */
public final class ConsoleInfrastructure
        implements Infrastructure {

    private final OrderRepository orders =
            new InMemoryOrderRepository();
    private final Notifier notifier =
            new ConsoleNotifier();
    ...
}
```

::right::

<div class="pl-8 pt-16">

```java
// Main — единственное место,
// где названо конкретное семейство
Infrastructure infra = new ConsoleInfrastructure();
PlaceOrder placeOrder =
        new PlaceOrder(infra.orders(), infra.notifier());
PayOrder payOrder =
        new PayOrder(infra.orders(), infra.notifier());
```

```java
// тесты — своё семейство
final class TestInfrastructure
        implements Infrastructure {
    final InMemoryOrderRepository orders = ...;
    final RecordingNotifier notifier = ...;
}
```

<div class="text-sm opacity-60 mt-2">
Вывод <code>Main</code> не изменился ни на символ.
</div>

</div>

<!--
Подводка к занятию 4: Infrastructure — самодельный BeanFactory. Spring сделает то же самое, только семейство он соберёт сам.
-->

---
layout: two-cols
commit: "60ac37b"
---

# Абстрактная фабрика в JDK

```java
DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
Document document = builder.parse(xml);

document.getDocumentElement()
        .getAttribute("id");          // "ORD-1"
```

```java
factory.getClass().getPackageName();
// com.sun.org.apache.xerces.internal.jaxp
builder.getClass().getPackageName();
// com.sun.org.apache.xerces.internal.jaxp
document.getClass().getPackageName();
// com.sun.org.apache.xerces.internal.dom
```

::right::

<div class="pl-8">

<div class="callout text-sm">

Клиент пишет только в терминах `javax.xml` и `org.w3c.dom`. Реализации — все из одного семейства (Xerces), подменить можно сразу всё семейство.

</div>

| Фабрика                 | Семейство                                  |
|-------------------------|--------------------------------------------|
| JDBC `Connection`       | `Statement`, `PreparedStatement`, транзакции одного драйвера |
| `DocumentBuilderFactory`| парсер, документ, узлы                     |
| `XMLInputFactory`       | потоковые читатели XML                     |
| Spring `BeanFactory`    | все бины приложения — на занятии 4         |

</div>

---
layout: default
---

# Из литературы: абстрактная фабрика

<div class="quote">

> *Provide an interface for creating families of related or dependent objects without specifying their concrete classes.*

Предоставьте интерфейс для создания семейств связанных или зависимых объектов, не указывая их конкретных классов. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Abstract Factory, «Intent», p. 87</div>

</div>

<div class="quote">

> *It promotes consistency among products. When product objects in a family are designed to work together, it's important that an application use objects from only one family at a time.*

Она обеспечивает согласованность продуктов. Если объекты семейства спроектированы для совместной работы, важно, чтобы приложение одновременно использовало объекты только одного семейства. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, «Consequences», п. 3</div>

</div>

<div class="text-sm opacity-60">
Там же, п. 4 — цена: <i>Supporting new kinds of products is difficult.</i> Новый член семейства, например <code>Clock</code>, — правка интерфейса и всех фабрик.
</div>

---
layout: two-cols
commit: "6f2a331"
---

# Одиночка

```java
/** Единственный экземпляр и глобальная
    точка доступа к нему. */
public final class Sequence {

    private static final Sequence INSTANCE =
            new Sequence();

    private int last = 0;

    private Sequence() {
    }

    public static Sequence getInstance() {
        return INSTANCE;
    }

    public int next() {
        last++;
        return last;
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
/** По конструктору не видно, что счёт
    зависит от глобального счётчика. */
public Invoice() {
    this.number = "INV-"
            + Sequence.getInstance().next();
}
```

```java
@Test @Order(1)
void первый_счёт_получает_номер_1() {
    assertThat(new Invoice().number())
            .isEqualTo("INV-1");
}

@Test @Order(2)
void тот_же_тест_получает_номер_2() {
    // ждали INV-1
    assertThat(new Invoice().number())
            .isEqualTo("INV-2");
}
```

<div class="bad font-bold mt-2">Два одинаковых теста — разные ответы.</div>

</div>

---
layout: default
---

# Из литературы: одиночка

<div class="quote">

> *Ensure a class only has one instance, and provide a global point of access to it.*

Гарантируйте, что у класса есть только один экземпляр, и предоставьте к нему глобальную точку доступа. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Singleton, «Intent», p. 127</div>

</div>

<div class="quote">

> *The Singleton pattern is an improvement over global variables. It avoids polluting the name space with global variables that store sole instances.*

Одиночка лучше глобальных переменных: он не засоряет пространство имён глобальными переменными, где хранятся единственные экземпляры. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, «Consequences», п. 2</div>

</div>

<div class="callout text-sm">

«Лучше глобальной переменной» — это и есть диагноз: одиночка с состоянием **остаётся** глобальной переменной, только с красивым именем.

</div>

---
layout: default
---

# Почему одиночка — антипаттерн

<div class="grid grid-cols-2 gap-10 mt-4">

<div>

- **скрытая зависимость**: `new Invoice()` не говорит, что ему нужен счётчик
- **глобальное состояние**: тесты влияют друг на друга, порядок запуска меняет результат
- **не подставить**: `getInstance()` возвращает ровно `Sequence` — ни фейка, ни другой реализации
- два обязательства в одном классе: что он делает и **сколько его экземпляров**

</div>

<div>

<div class="callout text-sm">

Принцип подстановки из занятия 2 работает, только если объект **передают**. Объект, который берут сам через `getInstance()`, подставить нельзя.

</div>

<div class="text-sm opacity-60 mt-6">

Одиночки без состояния безвредны: `Collections.emptyList()`, `Optional.empty()`, `Comparator.naturalOrder()`. Беда начинается, когда у одиночки есть поля, которые меняются.

</div>

</div>

</div>

---
layout: two-cols
commit: "8df2fa3"
---

# Одиночка: исправление

```java
/** Обычный класс. Сколько счётчиков — решает
    тот, кто собирает приложение. */
public final class Sequence {
    private int last = 0;
    public int next() { ... }
}

/** Зависимость видна в конструкторе. */
public Invoice(Sequence numbers) {
    this.number = "INV-" + numbers.next();
}
```

```java
// тест: свой счётчик, порядок не важен
new Invoice(new Sequence()).number();  // INV-1

// приложение: один на всех — решение сборки
Sequence shared = new Sequence();
```

::right::

<div class="pl-8 pt-16">

```java
/** Одиночка-перечисление: ровно один экземпляр
    гарантирует JVM. Для объектов без состояния. */
public enum Rubles {
    FORMAT;
    public String format(int rubles) { ... }
}
```

<div class="text-xs italic opacity-70 mt-2">
a single-element enum type is often the best way to implement a singleton.
</div>
<div class="text-xs opacity-50">Bloch J. Effective Java, 3rd ed. Item 3</div>

<div class="callout text-sm mt-4">

«Один экземпляр» — свойство **сборки**, а не класса. В Spring это `scope = singleton` по умолчанию: класс обычный, единственным его делает контейнер. Занятие 4.

</div>

</div>

---
layout: default
---

# Итоги части 1

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> <code>new</code> привязывает к классу; паттерны создания решают, <b>кто выбирает класс</b>.</div>

<div class="mb-5"><span class="accent font-bold">②</span> Статическая фабрика — имя, кэш, скрытый подтип. Фабричный метод — класс продукта выбирает подкласс или реализация.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Абстрактная фабрика — <b>согласованное семейство</b>; конкретное семейство названо в одном месте.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Одиночка с состоянием — глобальная переменная. Сколько экземпляров, решает <b>сборка</b>, а не класс.</div>

</div>

---
layout: section
---

# Часть 2

## Обёртки

---
layout: default
---

# Обёртка — объект вокруг объекта

<div class="text-lg mt-2 mb-6 opacity-80">
Класс не меняем — оборачиваем готовый объект и подставляем обёртку вместо него
</div>

| Проблема                                              | Паттерн       | Интерфейс обёртки            |
|-------------------------------------------------------|---------------|------------------------------|
| объект есть, но у него не тот интерфейс               | адаптер       | **другой**, нужный клиенту   |
| добавить поведение, не трогая класс и не наследуя     | декоратор     | **тот же**                   |
| управлять доступом: кэш, ленивость, права, журнал     | прокси        | **тот же**                   |
| работать с группой так же, как с одним                | компоновщик   | **тот же**, внутри — много   |

<div class="callout text-sm mt-6">

Все четыре — композиция: обёртка **содержит** объект и делегирует ему. Это то, чем на занятии 2 заменили `ColorPoint extends Point`.

</div>

---
layout: two-cols
commit: "f3fb329"
---

# Адаптер в JDK

```java
// «Чай» в UTF-8 — 6 байт; клиенту нужны символы
InputStream bytes = new ByteArrayInputStream(
        "Чай".getBytes(StandardCharsets.UTF_8));
Reader chars = new InputStreamReader(
        bytes, StandardCharsets.UTF_8);

chars.read(buffer);       // 3
```

```java
Enumeration<String> legacy = vector.elements();
List<String> list = Collections.list(legacy);
```

::right::

<div class="pl-8">

```java
String[] tags = {"sale", "new"};
List<String> list = Arrays.asList(tags);

list.set(0, "hit");
tags[0];                  // "hit" — не копия

list.add("top");
// UnsupportedOperationException
```

<div class="callout text-sm mt-4">

`InputStreamReader`: есть `InputStream` (байты), нужен `Reader` (символы). `Arrays.asList`: есть массив, нужен `List`.

</div>

<div class="text-sm opacity-60 mt-3">
У массива нет «добавить» — и адаптер отказывается через optional operation. Та же подстановка «на честном слове», что у <code>List.of</code> на занятии 2.
</div>

</div>

---
layout: default
---

# Из литературы: адаптер

<div class="quote">

> *Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.*

Преобразуйте интерфейс класса в другой интерфейс, которого ждут клиенты. Адаптер позволяет работать вместе классам, которым иначе мешают несовместимые интерфейсы. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Adapter, «Intent», p. 139</div>

</div>

<div class="callout mt-6">

Адаптер нужен там, где **чужой код менять нельзя**: библиотека, внешний сервис, legacy. В конце занятия адаптируем к `Notifier` клиент SMS-шлюза.

</div>

---
layout: two-cols
commit: "6fd9c2a"
---

# Наследник HashSet

```java
/** Считает попытки добавить элемент. */
public class InstrumentedHashSet<E>
        extends HashSet<E> {

    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
InstrumentedHashSet<String> tags =
        new InstrumentedHashSet<>();
tags.addAll(List.of("sale", "new", "hit"));
tags.addCount();   // 6
```

<div class="bad font-bold mt-4">Ждали 3.</div>

<div class="text-sm opacity-70 mt-2">

`HashSet.addAll` внутри вызывает `add` — а `add` переопределён. Это **деталь реализации** родителя: её нет в контракте, и в следующей версии JDK она может измениться.

</div>

<div class="callout text-sm mt-4">

Подкласс зависит и от того, как родитель вызывает **собственные** методы.

</div>

</div>

---
layout: two-cols
commit: "684a273"
---

# Декоратор

```java
/** Пересылает каждый вызов вложенному Set. */
public class ForwardingSet<E> implements Set<E> {

    private final Set<E> s;

    public ForwardingSet(Set<E> s) { this.s = s; }

    public boolean add(E e) { return s.add(e); }
    public boolean addAll(Collection<? extends E> c) {
        return s.addAll(c);
    }
    // ... ещё 14 методов Set
}
```

```java
public class InstrumentedSet<E>
        extends ForwardingSet<E> {
    // add и addAll — те же, что выше
}
```

::right::

<div class="pl-8 pt-16">

```java
InstrumentedSet<String> tags =
        new InstrumentedSet<>(new HashSet<>());
tags.addAll(List.of("sale", "new", "hit"));
tags.addCount();   // 3

// любое множество, не только HashSet
Set<String> sorted = new InstrumentedSet<>(
        new TreeSet<>(List.of("b", "a")));
```

<div class="callout text-sm mt-4">

`addAll` вложенного множества зовёт **свой** `add`, а не наш. Декоратор видит только публичный контракт `Set` — устройство `HashSet` ему безразлично.

</div>

<div class="text-sm opacity-60 mt-3">
Наследование здесь осталось — но от собственного <code>ForwardingSet</code>, который мы контролируем.
</div>

</div>

---
layout: default
---

# Из литературы: декоратор

<div class="quote">

> *Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.*

Динамически добавляйте объекту новые обязанности. Декораторы — гибкая альтернатива подклассам для расширения функциональности. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Decorator, «Intent», p. 175</div>

</div>

<div class="quote">

> *Unlike method invocation, inheritance violates encapsulation.*

В отличие от вызова метода, наследование нарушает инкапсуляцию. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Bloch J. Effective Java, 3rd ed. — Addison-Wesley, 2018. ISBN 978-0-13-468599-1. Item 18 «Favor composition over inheritance»</div>

</div>

<div class="text-sm opacity-60">
Пример с <code>InstrumentedHashSet</code> — оттуда же. Блох называет <code>InstrumentedSet</code> классом-обёрткой и прямо говорит: это паттерн «Декоратор».
</div>

---
layout: two-cols
commit: "684a273"
---

# Декоратор в JDK: `java.io`

```java
try (BufferedReader reader =
        new BufferedReader(            // + строки
            new InputStreamReader(     // адаптер
                new GZIPInputStream(   // + распаковка
                    new ByteArrayInputStream(file)),
                StandardCharsets.UTF_8))) {

    reader.readLine();   // "Чайник"
}
```

<div class="text-sm opacity-60 mt-2">
Каждый слой — одно умение. Слой сжатия вставили в цепочку, ничего больше не меняя.
</div>

::right::

<div class="pl-8">

```java
List<String> tags = new ArrayList<>(List.of("sale"));
List<String> view =
        Collections.unmodifiableList(tags);
List<String> copy = List.copyOf(tags);

view.add("new");   // UnsupportedOperationException

tags.add("new");
view;              // [sale, new] — видит
copy;              // [sale]      — не видит
```

| Декоратор                            | Что добавляет                  |
|--------------------------------------|--------------------------------|
| `BufferedReader`, `BufferedInputStream` | буфер, `readLine`          |
| `GZIPInputStream`, `CipherInputStream`  | распаковку, расшифровку    |
| `Collections.unmodifiable*`          | запрет изменений               |
| `Collections.synchronized*`          | блокировку на каждый вызов     |

</div>

---
layout: two-cols
commit: "51795ba"
---

# Прокси

```java
/** Настоящий объект: каждый вызов —
    дорогой запрос на склад. */
public final class WarehousePriceService
        implements PriceService { ... }

/** Тот же интерфейс и решение,
    когда обращаться к складу. */
public final class CachingPriceService
        implements PriceService {

    private final PriceService warehouse;
    private final Map<String, Integer> cache =
            new HashMap<>();

    @Override
    public int price(String sku) {
        return cache.computeIfAbsent(
                sku, warehouse::price);
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
/** Клиент: склад перед ним или прокси —
    не знает. */
static int basket(PriceService prices) {
    return prices.price("kettle")
         + prices.price("mug")
         + prices.price("mug");
}

basket(warehouse);         // 3 запроса
basket(caching);
basket(caching);           // 2 запроса на оба вызова
```

<div class="callout text-sm mt-4">

Виды прокси по GoF: **удалённый** (объект в другом процессе), **виртуальный** (создать или сходить, когда понадобится, — и закэшировать), **защищающий** (проверить права).

</div>

</div>

---
layout: two-cols
commit: "51795ba"
---

# Динамический прокси

```java
public static <T> T wrap(Class<T> type, T target,
                         List<String> log) {
    Object proxy = Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[]{type},
            (self, method, args) -> {
                log.add(method.getName()
                        + "(" + describe(args) + ")");
                try {
                    return method.invoke(target, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            });
    return type.cast(proxy);
}
```

::right::

<div class="pl-8 pt-16">

```java
PriceService prices = LoggingProxy.wrap(
        PriceService.class, warehouse, log);
basket(prices);
// [price(kettle), price(mug), price(mug)]

List<String> tags = LoggingProxy.wrap(
        List.class, new ArrayList<>(), log);
tags.add("sale");
// [add(sale)]
```

<div class="callout text-sm mt-4">

Класс-обёртку пишет не программист, а **JDK во время выполнения** — для любого интерфейса. Один обработчик на все методы.

</div>

<div class="text-sm opacity-60 mt-3">
Так устроены <code>@Transactional</code> и AOP в Spring (занятие 4) и моки Mockito (занятие 6).
</div>

</div>

---
layout: default
---

# Из литературы: прокси

<div class="quote">

> *Provide a surrogate or placeholder for another object to control access to it.*

Предоставьте заменитель или заместитель другого объекта, чтобы управлять доступом к нему. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Proxy, «Intent», p. 207</div>

</div>

<div class="quote">

> *Although decorators can have similar implementations as proxies, decorators have a different purpose. A decorator adds one or more responsibilities to an object, whereas a proxy controls access to an object.*

Хотя реализация декоратора может быть похожа на прокси, назначение у них разное. Декоратор добавляет объекту обязанности, прокси управляет доступом к объекту. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, Proxy, «Related Patterns»</div>

</div>

---
layout: default
---

# Декоратор или прокси?

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

### Декоратор

- **добавляет** обязанность: счёт, буфер, сжатие
- слоёв бывает много, порядок важен
- обычно его собирает **клиент**: `new BufferedReader(new ...)`

</div>

<div>

### Прокси

- **управляет доступом**: когда, кому, сколько раз
- обычно один, перед настоящим объектом
- обычно его подсовывает **сборка** или фреймворк — клиент о нём не знает

</div>

</div>

<div class="callout mt-8">

Структура одинакова: тот же интерфейс, ссылка на настоящий объект, делегирование. Как и со стратегией и состоянием на занятии 2, различает их **намерение**.

</div>

---
layout: two-cols
commit: "3671e31"
---

# Компоновщик

```java
/** Один товар или набор — клиенту всё равно. */
public interface Product { String name(); int price(); }

public record Item(String name, int price)
        implements Product { }

/** Набор — тоже Product. */
public record Bundle(String name,
                     List<Product> parts,
                     int discountPercent)
        implements Product {

    @Override
    public int price() {
        int sum = 0;
        for (Product part : parts) {
            sum += part.price();
        }
        return sum * (100 - discountPercent) / 100;
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
Item kettle = new Item("Чайник", 2490);
Item mug = new Item("Кружка", 350);
Bundle tea = new Bundle("Чайный набор",
        List.of(kettle, mug, mug), 10);
Bundle gift = new Bundle("Подарок",
        List.of(tea, new Item("Открытка", 100)), 0);

tea.price();                       // 2871
gift.price();                      // 2971
total(List.of(kettle, gift));      // 5461
```

<div class="callout text-sm mt-4">

Рекурсия спрятана в `Bundle.price()`. Клиент `total` не знает, что бывают наборы, и не проверяет `instanceof`.

</div>

</div>

---
layout: two-cols
commit: "eb72e7c"
---

# Компоновщик в OrderFlow

```java
/** Несколько нотификаторов под видом одного. */
public final class CompositeNotifier
        implements Notifier {

    private final List<Notifier> notifiers;

    public CompositeNotifier(Notifier... notifiers) {
        this.notifiers = List.of(notifiers);
    }

    @Override
    public void orderPlaced(Order order) {
        notifiers.forEach(n -> n.orderPlaced(order));
    }
    ...
}
```

::right::

<div class="pl-8 pt-16">

```java
/** Бухгалтерия: ей важны только возвраты. */
public final class RefundNotifier
        implements Notifier {
    @Override
    public void orderCancelled(Order order,
                               Money refund) {
        if (refund.compareTo(Money.ZERO) > 0) {
            System.out.println("Возврат " + ...);
        }
    }
    // остальные события — пустые
}
```

```java
// ConsoleInfrastructure
new CompositeNotifier(new RefundNotifier(),
                      new ConsoleNotifier());
```

<div class="text-sm opacity-60 mt-2">
Строка «Возврат …» жила в <code>ConsoleNotifier</code> — чужая ответственность. Сценарии не изменились.
</div>

</div>

---
layout: two-cols
commit: "3671e31"
---

# Компоновщик в JUnit

```java
/** Дерево тестов повторяет дерево товаров. */
@TestFactory
DynamicNode у_каждого_товара_положительная_цена() {
    return node(gift);
}

private static DynamicNode node(Product product) {
    if (product instanceof Bundle bundle) {
        return DynamicContainer.dynamicContainer(
                bundle.name(),
                bundle.parts().stream()
                        .map(CompositeTest::node));
    }
    return DynamicTest.dynamicTest(product.name(),
            () -> assertThat(product.price())
                    .isPositive());
}
```

::right::

<div class="pl-8">

```text
Подарок
├── Чайный набор
│   ├── Чайник     ✔
│   ├── Кружка     ✔
│   └── Кружка     ✔
└── Открытка       ✔
```

<div class="quote mt-2">

> *Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.*

Компонуйте объекты в древовидные структуры для представления иерархий «часть — целое». Компоновщик позволяет клиентам единообразно работать с отдельными объектами и их композициями. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Composite, «Intent», p. 163</div>

</div>

<div class="text-sm opacity-60">
<code>DynamicNode</code> — компонент, <code>DynamicContainer</code> — узел, <code>DynamicTest</code> — лист. В AWT так же: <code>Container extends Component</code>.
</div>

</div>

---
layout: default
---

# Паттерны занятия по категориям GoF

| Паттерн              | Категория GoF   | В OrderFlow / class03base                      | В JDK                                   |
|----------------------|-----------------|------------------------------------------------|-----------------------------------------|
| статическая фабрика  | идиома Java     | `Money.rubles`, `Order.builder()`              | `List.of`, `Integer.valueOf`            |
| фабричный метод      | порождающий     | `OrderRepository.nextId()`, `Exporter`         | `Collection.iterator()`, `ThreadFactory`|
| абстрактная фабрика  | порождающий     | `Infrastructure`                               | JDBC `Connection`                       |
| одиночка             | порождающий     | `Sequence` — как не надо                       | `Runtime.getRuntime()`                  |
| адаптер              | структурный     | SMS-шлюз — у экрана                            | `InputStreamReader`, `Arrays.asList`    |
| декоратор            | структурный     | `InstrumentedSet`                              | `java.io`, `Collections.unmodifiable*`  |
| прокси               | структурный     | `CachingPriceService`, `LoggingProxy`          | `java.lang.reflect.Proxy`               |
| компоновщик          | структурный     | `Bundle`, `CompositeNotifier`                  | `DynamicContainer`, AWT `Container`     |

<div class="callout text-sm mt-4">

Порождающие — **кто выбирает класс**, структурные — как **подставить другой объект** под тем же интерфейсом. Оба работают, только если клиент знает интерфейс, а не класс.

</div>

---
layout: two-cols
commit: "caa5914"
---

<div class="text-sm accent font-bold mb-2">Совместная задача · у экрана</div>

# Адаптер SMS-шлюза

```java
// vendor.sms — чужая библиотека, менять нельзя
public record SmsMessage(String msisdn,   // 79000000001
                         String body) {   // ≤ 160
    ...
}

public final class SmsGatewayClient {

    /** Каждый failEvery-й вызов — таймаут. */
    public SmsGatewayClient(int failEvery) { ... }

    /** @return MSG-1, MSG-2, ... */
    public String submit(SmsMessage message)
            throws GatewayTimeoutException { ... }
}
```

::right::

<div class="pl-8 pt-12">

```java
// app — то, что знают сценарии
public interface Notifier {
    void orderPlaced(Order order);
    void orderPaid(Order order);
    void orderShipped(Order order);
    void orderCancelled(Order order, Money refund);
}
```

<div class="callout text-sm mt-4">

Напишите `SmsNotifier implements Notifier` поверх шлюза.

- `+79000000001` → `79000000001`: чья забота?
- Куда деть проверяемое `GatewayTimeoutException`?
- Адаптер — в `app`, `infra` или `vendor`?

</div>

</div>

---
layout: default
---

<div class="text-sm accent font-bold mb-2">Совместная задача · у экрана</div>

# Декораторы `Notifier`

<div class="grid grid-cols-2 gap-10 mt-4">

<div>

Шлюз падает на каждом N-м вызове, повтор проходит. Нужны:

- `RetryingNotifier` — повтор при сбое, не больше K попыток
- `LoggingNotifier` — что отправили и чем кончилось

```java
Notifier sms = new LoggingNotifier(
        new RetryingNotifier(
                new SmsNotifier(gateway), 3));
```

</div>

<div>

<div class="callout text-sm">

- Повтор внутри логирования или логирование внутри повтора — что увидим в журнале в каждом случае?
- Как `RetryingNotifier` узнает о сбое, если адаптер уже «съел» исключение?
- Правило «крупные заказы — по SMS» сейчас в `ConsoleNotifier`. Где оно должно жить, когда SMS и почта — разные нотификаторы? Поможет ли `CompositeNotifier`?
- Какие тесты нужны, чтобы проверить повтор без настоящего шлюза?

</div>

</div>

</div>

<!--
Подсказка к разбору: GatewayTimeoutException — проверяемое, Notifier его не объявляет. Варианты: адаптер оборачивает его в своё непроверяемое исключение, а RetryingNotifier ловит именно его. Тогда повтор зависит от исключения адаптера — обсудить, нормально ли это.
-->

---
layout: default
---

# Что уносим с занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> Кто пишет <code>new X()</code>, тот зависит от <code>X</code>. Пусть это будет <b>одно место</b>: фабрика или сборка в <code>Main</code>.</div>

<div class="mb-5"><span class="accent font-bold">②</span> Одиночка с состоянием — глобальная переменная. «Один экземпляр» — решение <b>сборки</b>, не класса.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Обёртки меняют поведение <b>без правки класса</b>: адаптер меняет интерфейс, декоратор добавляет, прокси управляет доступом, компоновщик объединяет.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Обёртка лучше наследника: она зависит от <b>контракта</b>, а не от устройства родителя.</div>

</div>

---
layout: end
---

# Дальше

Занятие 4 · Spring собран из паттернов

Вопросы по материалу — в чате курса
