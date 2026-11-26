# class04OrderFlow. Spring собран из паттернов: OrderFlow

Код к слайдам `slides/class04.md` про OrderFlow. Стартует с конечного состояния `class03OrderFlow`.
История коммитов повторяет демо по шагам: у слайдов внизу хэш коммита, `git checkout <hash>` —
состояние кода для слайда.

| Пакет | Что это |
|---|---|
| `domain` | Без изменений с занятия 2: `Money`, `Customer`, `Order` с `Order.Builder`, тарифы, `Promo`, `OrderStatus` |
| `app` | Сценарии, отчёты, порты `OrderRepository` и `Notifier`; узкий порт `OrderHistory` для отчётов; `Infrastructure`, `CompositeNotifier`, `Main` |
| `infra` | `InMemoryOrderRepository`, `ConsoleNotifier`, `RefundNotifier`, семейство `ConsoleInfrastructure` |
| `vendor.sms` | Условно сторонний клиент SMS-шлюза `SmsGatewayClient` с занятия 3 |

## Тезисы:
* SOLID задним числом. S: возвраты переехали в `RefundNotifier` (занятие 3). O: самовывоз — новый класс
  `Pickup`, а не новая ветка (занятие 1). L: `ReadOnlyOrderRepository` нарушал контракт (занятие 2)
* ISP: отчётам нужен только `findAll` — они зависят от `OrderHistory`, а не от всего `OrderRepository`;
  `OrderRepository extends OrderHistory`, так что хранилище подходит отчётам без изменений
* Двойник узкого интерфейса — одна лямбда: `OrderHistory history = () -> List.of(order)`
* DIP уже соблюдён: порты лежат в `app` рядом со сценариями, реализации — в `infra`, и `infra`
  зависит от `app`, а не наоборот
* Внедрение зависимостей через конструктор: сценарии получают зависимости, а не создают их.
  Поэтому сборку можно передать контейнеру, не меняя ни одного сценария
* Вывод `Main` не изменился

## Для практики

На занятии у экрана — две задачи, `spring-context` уже в зависимостях:
* **контейнер**: ручную сборку в `Main` и `Infrastructure` заменить на `AnnotationConfigApplicationContext`
  и класс с методами `@Bean`; сценарии не меняются, вывод `Main` — тоже;
* **наблюдатель**: «заказ оплачен» — событие, а не прямой вызов `notifier.orderPaid(order)` в `PayOrder`;
  подписчики узнают о нём через `@EventListener`.

Над чем подумать: должен ли пакет `app` знать о Spring? Где тогда живёт `@Configuration`? В каком
порядке подписчики получат событие и останется ли вывод `Main` прежним? Что будет с `PayOrder`, если
подписчик бросит исключение? Нужны ли `RefundNotifier` пустые методы, если каждый подписчик слушает
только своё событие?

`instanceof ExpressDelivery` в `ConsoleNotifier` и `SalesReport` по-прежнему на месте — это задача 1 из ДЗ1.

## Запуск

```text
[EMAIL anna@example.ru] Анна, заказ ORD-1 принят. Сумма: 3490.00 руб.
...
[SMS +79000000003] Вера, заказ ORD-3 принят. Сумма: 58991.00 руб. Экспресс-доставка: привезём завтра.
...
Заказов: 4, экспресс: 1, выручка: 66075.70 руб., крупнейший: ORD-3

id;status;total_kopecks
...
TOTAL;4;6607570
```

```shell
./mvnw -pl class04OrderFlow test                      # все тесты
docker compose run --rm --build class04orderflow      # app.Main
```
