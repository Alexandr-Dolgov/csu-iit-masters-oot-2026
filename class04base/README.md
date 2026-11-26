# class04base. Spring собран из паттернов

Код к слайдам `slides/class04.md`, кроме слайдов про OrderFlow. Демо — тесты: каждый тест
показывает, что держится и что ломается. У слайдов внизу хэш коммита, `git checkout <hash>` —
состояние кода для слайда. Spring — только `spring-context` и `spring-tx`, без Spring Boot.

| Пакет | Что это |
|---|---|
| `isp` | Толстый `Device`: `SimplePrinter` бросает `UnsupportedOperationException`; затем `Printer`, `Scanner`, `Fax` и `MultiFunctionDevice`; узкие интерфейсы JDK — `Iterable`, `Comparable`, `AutoCloseable` |
| `ioc` | Самодельный контейнер `Container`: рецепты `register`, один экземпляр на тип через `get`; `Sequence` и `Invoices` о контейнере не знают |
| `spring` | Те же `Sequence` и `Invoices` в `AnnotationConfigApplicationContext`: `@Bean`, singleton и prototype, `@Configuration` за CGLIB-прокси, `proxyBeanMethods = false` |
| `postprocessor` | `LoggingPostProcessor implements BeanPostProcessor` подменяет бин с `@Logged` динамическим прокси из занятия 3 |
| `transactional` | `@Transactional` и `RecordingTransactionManager`: фиксация, откат на непроверяемом исключении, фиксация на проверяемом; вызов через `this` идёт мимо прокси |
| `observer` | Наблюдатель: `Stock` рассылает изменения `StockListener`; `PropertyChangeSupport` в JDK |
| `events` | Наблюдатель в Spring: `ApplicationEventPublisher`, `@EventListener`, `@Order`, `ContextRefreshedEvent` |
| `chain` | Цепочка обязанностей по образцу Servlet: `Filter`, `FilterChain`, `SimpleFilterChain`, `AuthFilter`, `LoggingFilter` |

## Тезисы:
* ISP: клиент не должен зависеть от методов, которыми не пользуется. Толстый интерфейс заставляет
  реализацию отказываться во время выполнения — подстановка ломается, как у `List.of` на занятии 2
* Узкий интерфейс проверяет компилятор, и его легко подменить лямбдой
* Инверсия управления: зависимости объекта создаёт и связывает контейнер
* Контейнер — абстрактная фабрика и фабричный метод для всего приложения: `getBean` возвращает объект,
  класс которого клиент не называет
* Singleton в Spring — один бин на контекст: класс обычный, решение принимает сборка
* `@Configuration` — наследник CGLIB: вызов `@Bean`-метода перехвачен и возвращает готовый бин;
  без прокси (`proxyBeanMethods = false`) это обычный вызов и второй объект
* `BeanPostProcessor` может вернуть вместо бина обёртку — так Spring подставляет прокси для
  `@Transactional`, `@Async`, `@Cacheable`
* JDK-прокси реализует только интерфейсы: бин больше не найти по классу. Зависеть от интерфейса
* `@Transactional` работает только через прокси: вызов через `this` транзакции не открывает.
  Проверяемое исключение по умолчанию фиксирует транзакцию
* Наблюдатель: издатель знает только интерфейс подписчика; новый подписчик — без правки издателя
* Уведомление — синхронный вызов: исключение подписчика доходит до издателя, следующие подписчики
  события не получают. В Spring так же
* Цепочка обязанностей: звено само решает, обработать запрос или передать дальше; порядок звеньев важен.
  Так устроены Servlet `Filter` и Spring Security `FilterChain`

## Запуск

```shell
./mvnw -pl class04base test
```

Тесты запускаются и из IDEA: Run на классе или методе теста.
