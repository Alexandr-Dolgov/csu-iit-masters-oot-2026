---
theme: default
title: Занятие 4 · Spring собран из паттернов
info: |
  Занятие 4 курса «Объектно-ориентированные технологии»
  Магистратура ЧелГУ, 1 курс, 1 семестр

  Программы: 09.04.04 «Программная инженерия», 02.04.02 «ФИИТ»
class: text-left
highlighter: shiki
lineNumbers: false
transition: slide-left
slideNumbers: true
exportFilename: ООТ-04
---

# Занятие 4

## Spring собран из паттернов

<div class="pt-10 text-sm opacity-60">
Объектно-ориентированные технологии
</div>

<div class="text-sm opacity-50">
Магистратура. Институт информационных технологий. ЧелГУ
<br> · 09.04.04 «Программная инженерия»
<br> · 02.04.02 «Фундаментальная информатика и информационные технологии»
</div>

<!--
Код: модули class04base (теория, демо — тесты) и class04OrderFlow (OrderFlow).
Хэш внизу слайда — git checkout <hash>, состояние кода для слайда.
Spring — только spring-context и spring-tx, без Spring Boot: видно, что делает контейнер.
-->

---
layout: default
---

# План занятия

<div class="grid grid-cols-2 gap-10 mt-6">

<div>

### Часть 1 · зависимости

- SOLID задним числом: S, O, L уже были
- принцип разделения интерфейса (ISP)
- принцип инверсии зависимостей (DIP)
- инверсия управления и внедрение зависимостей
- самодельный контейнер

</div>

<div>

### Часть 2 · Spring

- контейнер: `BeanFactory`, `@Bean`, singleton
- `@Configuration`, `BeanPostProcessor`, прокси
- `@Transactional` — и где он не работает
- наблюдатель: `@EventListener`
- цепочка обязанностей: `Filter`
- совместная задача: OrderFlow на Spring

</div>

</div>

---
layout: default
---

# С прошлого занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> Кто пишет <code>new X()</code>, тот зависит от <code>X</code>. Пусть это будет <b>одно место</b>: фабрика или сборка в <code>Main</code>.</div>

<div class="mb-5"><span class="accent font-bold">②</span> «Один экземпляр» — решение <b>сборки</b>, не класса.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Обёртка подставляется <b>вместо</b> объекта под тем же интерфейсом — клиент не замечает.</div>

</div>

<div class="callout mt-8">

`Infrastructure` из занятия 3 — самодельная фабрика всего приложения. Сегодня увидим, что Spring — та же фабрика, только универсальная, плюс прокси, наблюдатель и цепочка обязанностей. **Spring построен на паттернах, большинство из которых мы уже разобрали.**

</div>

---
layout: section
---

# Часть 1

## Зависимости

---
layout: default
---

# SOLID задним числом

| Буква | Принцип                                   | Где уже встречали                                                              |
|-------|-------------------------------------------|--------------------------------------------------------------------------------|
| **S** | единственной ответственности (SRP)        | возвраты — бухгалтерии: строка «Возврат …» ушла в `RefundNotifier` (занятие 3) |
| **O** | открытости/закрытости (OCP)               | самовывоз — новый класс `Pickup`, а не новая ветка `if` (занятие 1)            |
| **L** | подстановки (LSP)                         | `Square extends Rectangle`, `ColorPoint.equals` (занятие 2)                    |
| **I** | разделения интерфейса (ISP)               | разбираем сегодня                                                              |
| **D** | инверсии зависимостей (DIP)               | разбираем сегодня — хотя порты в `app` появились ещё на занятии 1              |

<div class="callout text-sm mt-6">

SOLID — пять названий для того, что мы уже делали ради **цены изменения**. Мартин собрал их в набор в 2000-х; аббревиатуру придумал Майкл Фезерс.

</div>

<div class="text-xs opacity-60 mt-3">
Коммиты: S — <a href="https://github.com/Alexandr-Dolgov/csu-iit-masters-oot-2026/commit/eb72e7c" target="_blank" rel="noopener"><code>eb72e7c</code></a>, O — <a href="https://github.com/Alexandr-Dolgov/csu-iit-masters-oot-2026/commit/538029a" target="_blank" rel="noopener"><code>538029a</code></a>, L — <a href="https://github.com/Alexandr-Dolgov/csu-iit-masters-oot-2026/commit/9a1bc00" target="_blank" rel="noopener"><code>9a1bc00</code></a>.
</div>

---
layout: two-cols
commit: "769385e"
---

# Толстый интерфейс

```java
/** Всё, что умеет офисный комбайн. */
public interface Device {
    String print(String document);
    String scan(String paper);
    String fax(String document, String number);
}

/** Обычный принтер: остаётся бросать исключение. */
public final class SimplePrinter implements Device {
    public String print(String document) { ... }

    public String scan(String paper) {
        throw new UnsupportedOperationException();
    }
    public String fax(String d, String n) {
        throw new UnsupportedOperationException();
    }
}
```

::right::

<div class="pl-8 pt-8">

```java
// бухгалтерии нужна только печать
static List<String> printInvoices(Device device) { ... }

// архиву — только сканер
static String archive(Device device) {
    return device.scan("договор");
}

archive(new SimplePrinter());
// компилируется;
// UnsupportedOperationException — у клиента
```

<div class="callout text-sm mt-4">

Бухгалтерия зависит от `scan` и `fax`, которыми не пользуется: поменяли сигнатуру `fax` — перекомпилируй и бухгалтерию. А реализация, которая не умеет всего, **нарушает подстановку**.

</div>

<div class="text-sm opacity-60 mt-3">
Как <code>List.of(...).add(...)</code> на занятии 2: интерфейс обещает, объект отказывается во время выполнения.
</div>

</div>

---
layout: two-cols
commit: "17e161d"
---

# Интерфейс на роль клиента

```java
public interface Printer {
    String print(String document);
}
public interface Scanner {
    String scan(String paper);
}
public interface Fax {
    String fax(String document, String number);
}

public final class SimplePrinter implements Printer { ... }

public final class MultiFunctionDevice
        implements Printer, Scanner, Fax { ... }
```

::right::

<div class="pl-8 pt-16">

```java
static List<String> printInvoices(Printer p) { ... }
static String archive(Scanner s) { ... }

archive(new SimplePrinter());   // не компилируется
archive(new MultiFunctionDevice());   // ок

// узкий интерфейс — двойник одной лямбдой
Printer recording = doc -> { printed.add(doc); return "ok"; };
```

| В JDK          | Что обещает                 |
|----------------|-----------------------------|
| `Iterable`     | только обход                |
| `Comparable`   | только сравнение            |
| `AutoCloseable`| только `close()`            |

<div class="text-sm opacity-60 mt-2">
<code>for (x : xs)</code> нужен <code>Iterable</code>, а не <code>Collection</code> с её <code>add</code> и <code>size</code>.
</div>

</div>

---
layout: two-cols
commit: "0aba264"
---

# ISP в OrderFlow

```java
/** Отчётам нужны только прошлые заказы —
    ни nextId, ни save. */
public interface OrderHistory {
    List<Order> findAll();
}

public interface OrderRepository extends OrderHistory {
    OrderId nextId();
    void save(Order order);
    Optional<Order> find(OrderId id);
}

public abstract class SalesReport {
    protected SalesReport(OrderHistory orders) { ... }
}
```

::right::

<div class="pl-8 pt-16">

```java
// тест: двойник — одна лямбда
OrderHistory history = () -> List.of(archived);

new CsvSalesReport(history).render();
// id;status;total_kopecks
// ORD-7;NEW;279000
// TOTAL;1;0
```

<div class="callout text-sm mt-4">

Ответ на совместную задачу занятия 2. `ReadOnlyOrderRepository` бросал исключения из `nextId` и `save` — нарушал подстановку. Узкий интерфейс честен: у отчёта просто нет метода `save`.

</div>

<div class="text-sm opacity-60 mt-3">
<code>InMemoryOrderRepository</code> подходит отчётам без изменений. Вывод <code>Main</code> не изменился.
</div>

</div>

---
layout: default
---

# Из литературы: ISP

<div class="quote">

> *Clients should not be forced to depend on methods they do not use.*

Клиенты не должны вынужденно зависеть от методов, которыми не пользуются. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Martin R. C., Martin M. Agile Principles, Patterns, and Practices in C#. — Prentice Hall, 2006. ISBN 0-13-185725-8. Ch. «The Interface Segregation Principle (ISP)»</div>

</div>

<div class="grid grid-cols-2 gap-10 mt-6">

<div class="text-sm">

**Что ломается без ISP**

- изменение ненужного метода задевает всех клиентов
- реализации вынуждены заглушать «лишнее» — подстановка ломается
- двойник в тесте реализует то, что тест не проверяет

</div>

<div class="callout text-sm">

Интерфейс принадлежит **клиенту**: его форма определяется тем, что клиенту нужно, а не тем, что умеет реализация. Один класс может реализовать несколько ролей.

</div>

</div>

---
layout: two-cols
commit: "5cda4fa"
---

# Инверсия зависимостей

```text
занятие 1, legacy:
  OrderService ──► HashMap, System.out

сейчас:
  app:    PlaceOrder ──► OrderRepository  (порт)
                    └──► Notifier         (порт)
                              ▲
  infra:  InMemoryOrderRepository ────────┘
          ConsoleNotifier
```

<div class="text-sm opacity-70 mt-4">

Стрелки кода из `infra` идут **в** `app`. Сценарий не знает, где хранятся заказы — и не перекомпилируется, когда появится база (занятие 7).

</div>

::right::

<div class="pl-8">

<div class="quote">

> *a. High-level modules should not depend on low-level modules. Both should depend on abstractions.*
> *b. Abstractions should not depend upon details. Details should depend upon abstractions.*

а. Модули верхнего уровня не должны зависеть от модулей нижнего уровня. Оба должны зависеть от абстракций. б. Абстракции не должны зависеть от деталей. Детали должны зависеть от абстракций. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Martin R. C., Martin M. Agile Principles, Patterns, and Practices in C#. Ch. «The Dependency-Inversion Principle (DIP)»</div>

</div>

<div class="callout text-sm">

«Инверсия» — в том, **кому принадлежит** интерфейс: `OrderRepository` лежит рядом со сценариями, а не рядом с хранилищем.

</div>

</div>

---
layout: default
---

# Три похожих названия

<div class="grid grid-cols-3 gap-6 mt-6">

<div>

### DIP

**Принцип**: от чего зависит код. Политика и детали зависят от абстракции, абстракция принадлежит политике.

<div class="text-sm opacity-60 mt-2">
<code>OrderRepository</code> в <code>app</code>
</div>

</div>

<div>

### Внедрение зависимостей

**Приём**: зависимости приходят снаружи, обычно через конструктор, — объект их не создаёт и не ищет.

<div class="text-sm opacity-60 mt-2">
<code>new PlaceOrder(orders, notifier)</code>
</div>

</div>

<div>

### Инверсия управления

**Устройство**: фреймворк вызывает мой код: создаёт, связывает, вызывает.

<div class="text-sm opacity-60 mt-2">
шаблонный метод, контейнер, JUnit
</div>

</div>

</div>

<div class="quote mt-8">

> *Template methods lead to an inverted control structure that's sometimes referred to as "the Hollywood principle," that is, "Don't call us, we'll call you".*

Шаблонные методы приводят к инвертированной структуре управления, которую иногда называют «голливудским принципом»: «Не звоните нам, мы вам позвоним». <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Template Method, «Consequences»</div>

</div>

---
layout: two-cols
commit: "5c70fa4"
---

# Самодельный контейнер

```java
/** Infrastructure из занятия 3,
    обобщённый на любые типы. */
public final class Container {

    private final Map<Class<?>, Function<Container, ?>>
            recipes = new HashMap<>();
    private final Map<Class<?>, Object>
            instances = new HashMap<>();

    public <T> void register(Class<T> type,
            Function<Container, ? extends T> recipe) {
        recipes.put(type, recipe);
    }

    /** Создаёт при первом запросе, потом
        возвращает тот же. */
    public <T> T get(Class<T> type) { ... }
}
```

::right::

<div class="pl-8 pt-16">

```java
// сборка: единственное место, где названы классы
Container container = new Container();
container.register(Sequence.class,
        c -> new Sequence());
container.register(Invoices.class,
        c -> new Invoices("INV", c.get(Sequence.class)));

Invoices invoices = container.get(Invoices.class);
invoices.issue();                  // INV-1
container.created();               // [Sequence, Invoices]
```

<div class="callout text-sm mt-4">

`Sequence` и `Invoices` о контейнере не знают: зависимость — в конструкторе. Контейнер решает, **когда** создать и **сколько** экземпляров. Одиночка из занятия 3 без глобального состояния: у каждого теста свой контейнер.

</div>

</div>

---
layout: default
---

# Итоги части 1

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> S, O, L мы уже применяли; SOLID — их имена, мера одна — <b>цена изменения</b>.</div>

<div class="mb-5"><span class="accent font-bold">②</span> ISP: интерфейс — по роли <b>клиента</b>. Узкий интерфейс проверяет компилятор и подменяет лямбда.</div>

<div class="mb-5"><span class="accent font-bold">③</span> DIP: абстракция принадлежит политике. Порты лежат в <code>app</code>, адаптеры зависят от них.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Внедрение зависимостей через конструктор — и сборку можно отдать <b>контейнеру</b>, не трогая ни одного класса.</div>

</div>

---
layout: section
---

# Часть 2

## Spring собран из паттернов

---
layout: two-cols
commit: "1a3d0ee"
---

# Контейнер Spring

```java
/** Те же рецепты, методами @Bean.
    Sequence и Invoices о Spring не знают. */
@Configuration
public class InvoiceConfig {

    @Bean
    public Sequence sequence() {
        return new Sequence();
    }

    /** Зависимость — параметром. */
    @Bean
    public Invoices refunds(Sequence sequence) {
        return new Invoices("REF", sequence);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Invoices drafts() { ... }
}
```

::right::

<div class="pl-8 pt-16">

```java
try (var context = new AnnotationConfigApplicationContext(
        InvoiceConfig.class)) {

    BeanFactory beans = context;   // клиент знает интерфейс
    Invoices refunds =
            beans.getBean("refunds", Invoices.class);

    context.getBean("sequence")
        == context.getBean("sequence");   // true
    context.getBean("drafts")
        == context.getBean("drafts");     // false
}
```

<div class="callout text-sm mt-4">

`BeanFactory` — **абстрактная фабрика** для всего приложения, `getBean` — **фабричный метод**. Singleton в Spring — один бин на контекст: два контекста — два счётчика, класс при этом обычный.

</div>

</div>

---
layout: two-cols
commit: "1a3d0ee"
---

# `@Configuration` — уже прокси

```java
@Configuration
public class InvoiceConfig {

    @Bean
    public Sequence sequence() { return new Sequence(); }

    /** Вызов метода — а счётчик тот же. */
    @Bean
    public Invoices invoices() {
        return new Invoices("INV", sequence());
    }
}
```

```java
context.getBean(InvoiceConfig.class).getClass();
// InvoiceConfig$$SpringCGLIB$$0 — наследник

invoices.issue();   // INV-1
refunds.issue();    // REF-2 — счётчик общий
```

::right::

<div class="pl-8 pt-16">

```java
@Configuration(proxyBeanMethods = false)
public class LiteInvoiceConfig {
    // те же методы
}
```

```java
invoices.issue();   // INV-1
refunds.issue();    // REF-1 — ждали REF-2
```

<div class="callout text-sm mt-4">

Spring подменяет класс конфигурации **наследником**, который перехватывает вызов `sequence()` и возвращает готовый бин. Без прокси это обычный вызов Java — и второй объект.

</div>

<div class="text-sm opacity-60 mt-3">
Прокси из занятия 3, только класс-обёртку генерирует CGLIB, и он наследует класс, а не реализует интерфейс.
</div>

</div>

---
layout: two-cols
commit: "f9956b9"
---

# `BeanPostProcessor`

```java
/** Каждый созданный бин проходит через этот
    объект — и тот может вернуть другой. */
public final class LoggingPostProcessor
        implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(
            Object bean, String beanName) {
        if (bean.getClass()
                .isAnnotationPresent(Logged.class)) {
            return LoggingProxy.wrap(bean, log);
        }
        return bean;
    }
}

@Logged
public final class WarehousePriceService
        implements PriceService { ... }
```

::right::

<div class="pl-8 pt-16">

```java
PriceService prices =
        context.getBean(PriceService.class);
basket(prices);
// [price(kettle), price(mug), price(mug)]

Proxy.isProxyClass(prices.getClass());   // true

context.getBean(WarehousePriceService.class);
// NoSuchBeanDefinitionException
```

<div class="callout text-sm mt-4">

Контейнер отдаёт клиенту **не тот объект, что создал**. Так появляются `@Transactional`, `@Async`, `@Cacheable`: аннотация — только метка, работу делает прокси.

</div>

<div class="text-sm opacity-60 mt-3">
JDK-прокси реализует только интерфейсы — по классу бин уже не найти. Ещё один довод зависеть от интерфейса.
</div>

</div>

---
layout: two-cols
commit: "09fb03e"
---

# `@Transactional`

```java
/** О транзакциях не знает. Не final —
    прокси будет наследником. */
public class TransferService {

    @Transactional
    public void transfer(String from, String to,
                         int amount) {
        journal.add("списано " + amount + ...);
        if (amount <= 0) {
            throw new IllegalArgumentException(...);
        }
        journal.add("зачислено " + amount + ...);
    }
}

@Configuration
@EnableTransactionManagement
public class TransferConfig { ... }
```

::right::

<div class="pl-8 pt-8">

```text
transfer("anna", "boris", 500)
  begin, списано 500, зачислено 500, commit

transfer("anna", "boris", -1)
  begin, списано -1, rollback

transferChecked(..., 1_000_000)   // проверяемое
  begin, списано 1000000, commit  ← ждали rollback
```

<div class="callout text-sm mt-4">

`begin`/`commit`/`rollback` пишет **прокси** перед сервисом — декоратор, который собрал контейнер. По умолчанию откат только на непроверяемом исключении: иначе нужно `rollbackFor = Exception.class`.

</div>

<div class="text-sm opacity-60 mt-3">
Менеджер транзакций в демо — <code>RecordingTransactionManager</code> записывает begin, commit и rollback в журнал.
</div>

</div>

---
layout: two-cols
commit: "c08b3fe"
---

# Вызов через `this`

```java
public class TransferService {

    @Transactional
    public void transfer(...) { ... }

    /** Без аннотации. */
    public void payAll(String from,
                       List<String> to, int amount) {
        for (String recipient : to) {
            transfer(from, recipient, amount);
            // это this.transfer(...)
        }
    }
}
```

```text
transfers.payAll("anna", [boris, vera], 100)
  списано 100, зачислено 100,
  списано 100, зачислено 100     ← ни одного begin
```

::right::

<div class="pl-8 pt-16">

```text
клиент ──► прокси ──► TransferService
             │            │
         begin/commit     └── this.transfer()
                              мимо прокси
```

```java
/** Отдельный бин — вызов через прокси. */
public final class Payroll {
    private final TransferService transfers;
    public void payAll(...) {
        transfers.transfer(from, recipient, amount);
    }
}
// begin, …, commit, begin, …, commit
```

<div class="callout text-sm mt-4">

Прокси видит только вызовы **снаружи**. Так ведёт себя любая обёртка из занятия 3.

</div>

</div>

---
layout: two-cols
commit: "8e5979c"
---

# Наблюдатель

```java
public interface StockListener {
    void stockChanged(String sku, int before, int after);
}

/** Склад знает только интерфейс подписчика. */
public final class Stock {
    private final List<StockListener> listeners =
            new ArrayList<>();

    public void subscribe(StockListener listener) { ... }

    public void set(String sku, int quantity) {
        int before = ...;
        quantities.put(sku, quantity);
        for (StockListener l : listeners) {
            l.stockChanged(sku, before, quantity);
        }
    }
}
```

::right::

<div class="pl-8 pt-16">

```java
stock.subscribe(new ShopWindow());       // витрина
stock.subscribe(new RestockAlert(5));    // закупки
stock.subscribe((sku, b, a) ->           // аналитика
        analytics.add(sku + ": " + b + " → " + a));
```

```java
// JDK, JavaBeans
PropertyChangeSupport listeners =
        new PropertyChangeSupport(this);
listeners.firePropertyChange("price", old, newPrice);
```

<div class="callout text-sm mt-4">

Новый подписчик — **без правки** склада. Но уведомление — обычный вызов: упал первый подписчик — остальные не узнают, исключение у издателя.

</div>

</div>

---
layout: default
---

# Из литературы: наблюдатель

<div class="quote">

> *Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.*

Определите между объектами зависимость «один ко многим», чтобы при изменении состояния одного объекта все зависящие от него узнавали об этом и обновлялись автоматически. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Observer, «Intent», p. 293</div>

</div>

<div class="quote">

> *All a subject knows is that it has a list of observers, each conforming to the simple interface of the abstract Observer class.*

Всё, что знает субъект, — у него есть список наблюдателей, и каждый соответствует простому интерфейсу абстрактного класса Observer. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, «Consequences», п. 1</div>

</div>

<div class="text-sm opacity-60">
Там же, п. 3 — цена: <i>Unexpected updates.</i> Подписчики не знают друг о друге, и безобидное изменение издателя может вызвать каскад обновлений.
</div>

---
layout: two-cols
commit: "68f4dfc"
---

# Наблюдатель в Spring

```java
/** Событие — обычная запись. */
public record StockChanged(String sku,
                           int before, int after) { }

public final class SpringStock {
    private final ApplicationEventPublisher events;

    public void set(String sku, int quantity) {
        ...
        events.publishEvent(
                new StockChanged(sku, before, quantity));
    }
}

public final class PurchasingListener {
    @EventListener
    @Order(2)
    public void on(StockChanged event) { ... }
}
```

::right::

<div class="pl-8 pt-16">

```text
stock.set("kettle", 10); stock.set("kettle", 0);

  витрина: скрыть kettle
  закупки: пополнить kettle [main]   ← тот же поток
```

<div class="callout text-sm mt-4">

Список подписчиков ведёт **контейнер**: издатель не знает ни одного подписчика, подписчик — ни одного издателя. Подписка — тип параметра метода.

</div>

<div class="text-sm opacity-70 mt-3">

- синхронно: исключение подписчика долетает до `publishEvent`, следующие не узнают
- порядок — `@Order`, без него не гарантирован
- Spring и сам издатель: `ContextRefreshedEvent`, `ContextClosedEvent`

</div>

</div>

---
layout: two-cols
commit: "c6a78d5"
---

# Цепочка обязанностей

```java
/** Как jakarta.servlet.Filter. */
public interface Filter {
    void doFilter(Request request, Response response,
                  FilterChain chain);
}

public final class AuthFilter implements Filter {
    @Override
    public void doFilter(Request request,
            Response response, FilterChain chain) {
        if (request.header("Authorization") == null) {
            response.send(401, "нужен вход");
            return;                 // дальше не передаём
        }
        chain.doFilter(request, response);
    }
}
```

::right::

<div class="pl-8 pt-16">

```text
[LoggingFilter, AuthFilter] → обработчик

  с входом:   → /orders, обработчик, ← 200
  без входа:  → /orders, ← 401

[AuthFilter, LoggingFilter]
  без входа:  (пусто) — отказ не записан
```

<div class="callout text-sm mt-4">

Звено **само решает**: обработать и остановиться или передать дальше. Отправитель не знает, кто ответит. Порядок звеньев — часть поведения.

</div>

<div class="text-sm opacity-60 mt-3">
Новое звено — «технические работы, 503» — вставляется в список, остальные звенья не меняются.
</div>

</div>

---
layout: two-cols
---

# Цепочка в Servlet и Spring Security

```java
// Servlet API: фильтр перед любым сервлетом
public class RequestIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req,
            ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        MDC.put("requestId", UUID.randomUUID().toString());
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove("requestId");
        }
    }
}
```

::right::

<div class="pl-8">

```java
// Spring Security: цепочка собирается конфигурацией
@Bean
SecurityFilterChain api(HttpSecurity http)
        throws Exception {
    return http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .build();
}
```

<div class="callout text-sm mt-4">

`FilterChainProxy` — один servlet-фильтр, внутри — цепочка из десятка фильтров Security: CSRF, аутентификация, исключения, права. `HttpSecurity` — строитель из занятия 2: он и собирает цепочку.

</div>

<div class="quote mt-3">

> *Chain the receiving objects and pass the request along the chain until an object handles it.*

<div class="quote-src">Gamma E. et al. Design Patterns. Chain of Responsibility, «Intent», p. 223</div>

</div>

</div>

<!--
Кода на этом слайде в репозитории нет: Servlet API и Spring Security не подключаем, чтобы не тянуть зависимости. Своя цепочка — в class04base, пакет chain.
-->

---
layout: default
---

# Из литературы: цепочка обязанностей

<div class="quote">

> *Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it.*

Избегайте привязки отправителя запроса к получателю: дайте шанс обработать запрос нескольким объектам. Свяжите получателей в цепочку и передавайте запрос по ней, пока какой-нибудь объект его не обработает. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Gamma E. et al. Design Patterns. — Addison-Wesley, 1994. ISBN 0-201-63361-2. Chain of Responsibility, «Intent», p. 223</div>

</div>

<div class="quote">

> *Receipt isn't guaranteed. Since a request has no explicit receiver, there's no guarantee it'll be handled.*

Обработка не гарантирована. У запроса нет явного получателя, поэтому нет гарантии, что его обработают. <span class="opacity-50 text-xs">(пер. наш)</span>

<div class="quote-src">Там же, «Consequences», п. 3</div>

</div>

<div class="text-sm opacity-60">
У Servlet-фильтров классический GoF чуть изменён: звено может сделать что-то до и после остатка цепочки — как декоратор.
</div>

---
layout: default
---

# Паттерны занятия по категориям GoF

| Паттерн / принцип           | Категория        | В OrderFlow / class04base               | В Spring / JDK                                  |
|-----------------------------|------------------|-----------------------------------------|-------------------------------------------------|
| разделение интерфейса       | принцип (ISP)    | `OrderHistory`, `Printer`/`Scanner`/`Fax` | `Iterable`, `Comparable`, `AutoCloseable`        |
| инверсия зависимостей       | принцип (DIP)    | порты `OrderRepository`, `Notifier`     | SLF4J — занятие 7                               |
| абстрактная фабрика         | порождающий      | `Infrastructure`, `Container`           | `BeanFactory`, `ApplicationContext`             |
| фабричный метод             | порождающий      | `Container.get`                         | `getBean`, `@Bean`-метод                        |
| одиночка (как надо)         | —                | один экземпляр на контейнер             | singleton scope                                 |
| прокси / декоратор          | структурный      | `LoggingProxy`, `LoggingPostProcessor`  | `@Configuration` (CGLIB), `@Transactional`, AOP |
| наблюдатель                 | поведенческий    | `Stock`, `SpringStock`                  | `@EventListener`, `PropertyChangeSupport`       |
| цепочка обязанностей        | поведенческий    | `SimpleFilterChain`                     | Servlet `Filter`, `SecurityFilterChain`         |
| шаблонный метод             | поведенческий    | `SalesReport` (занятие 2)               | голливудский принцип, `JdbcTemplate`            |

<div class="callout text-sm mt-3" style="padding-top: 0.4rem; padding-bottom: 0.4rem">
Spring <b>собран из паттернов</b>, которые мы писали руками. Поэтому его поведение можно предсказать — и понять, где оно сломается.
</div>

---
layout: two-cols
commit: "b9a1f90"
---

<div class="text-sm accent font-bold mb-2">Совместная задача · у экрана</div>

# OrderFlow на Spring-контексте

```java
// сейчас: Main собирает руками
Infrastructure infra = new ConsoleInfrastructure();
PlaceOrder placeOrder =
        new PlaceOrder(infra.orders(), infra.notifier());
PayOrder payOrder =
        new PayOrder(infra.orders(), infra.notifier());
ShipOrder shipOrder = ...;
SalesReport report =
        new TextSalesReport(infra.orders());
...
```

<div class="text-sm opacity-60 mt-2">
<code>spring-context</code> уже в <code>pom.xml</code> модуля <code>class04OrderFlow</code>.
</div>

::right::

<div class="pl-8 pt-12">

<div class="callout text-sm">

Замените ручную сборку на `AnnotationConfigApplicationContext` и класс с методами `@Bean`.

- Сколько сценариев пришлось изменить? Почему?
- Где должен лежать класс `@Configuration`: `app`, `infra`, отдельный пакет? Что он знает?
- Что станет с `Infrastructure` и кто теперь гарантирует, что хранилище одно на всех?
- Отчёты просят `OrderHistory`, а бин — `InMemoryOrderRepository`. Найдёт ли контейнер?
- Вывод `Main` должен остаться прежним.

</div>

</div>

<!--
Решение и разбор — в grading/class04/README.md (не в репозитории).
Docker-образ собирает jar без зависимостей: Spring-версию Main запускать из IDEA.
-->

---
layout: default
---

<div class="text-sm accent font-bold mb-2">Совместная задача · у экрана</div>

# «Заказ оплачен» — событие

<div class="grid grid-cols-2 gap-10 mt-4">

<div>

Сейчас `PayOrder` сам зовёт нотификатор:

```java
order.pay();
orders.save(order);
notifier.orderPaid(order);
```

Нужно: `PayOrder` сообщает, что заказ оплачен, а кто об этом узнает — решают подписчики `@EventListener`. Завтра подпишется бухгалтерия — `PayOrder` не меняется.

</div>

<div>

<div class="callout text-sm">

- `PayOrder` может взять `ApplicationEventPublisher` прямо в конструктор. Чем это плохо? Вспомните DIP.
- Подписчик бросил исключение — что увидит клиент? Заказ уже оплачен?
- В каком порядке подписчики получат событие, и останется ли вывод `Main` тем же?
- У `RefundNotifier` три пустых метода из четырёх. Что изменится, если все вызовы `Notifier` станут событиями? Это какая буква SOLID?
- `CompositeNotifier` тоже рассылает одно событие многим. Чем он отличается от наблюдателя?

</div>

</div>

</div>

---
layout: default
---

# Что уносим с занятия

<div class="mt-6 text-lg leading-relaxed">

<div class="mb-5"><span class="accent font-bold">①</span> Интерфейс — по роли <b>клиента</b> (ISP); абстракция принадлежит <b>политике</b> (DIP).</div>

<div class="mb-5"><span class="accent font-bold">②</span> Внедрение через конструктор делает сборку отдельной задачей — её можно отдать <b>контейнеру</b>, не трогая классы.</div>

<div class="mb-5"><span class="accent font-bold">③</span> Контейнер Spring — фабрика; <code>@Configuration</code>, <code>@Transactional</code>, AOP — <b>прокси</b>; события — <b>наблюдатель</b>; фильтры — <b>цепочка обязанностей</b>.</div>

<div class="mb-5"><span class="accent font-bold">④</span> Знаешь паттерн — знаешь, где фреймворк сломается: вызов через <code>this</code> мимо прокси, синхронный подписчик роняет издателя, порядок фильтров меняет ответ.</div>

</div>

---
layout: end
---

# Дальше

Занятие 5 · Разбор ДЗ1

Вопросы по материалу — в чате курса
