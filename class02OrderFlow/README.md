# class02OrderFlow. Паттерны поведения на примере OrderFlow

Код к Части 2 слайдов `slides/class02.md`. Стартует с конечного состояния `class01OrderFlow`.
История коммитов повторяет демо по шагам: у слайдов внизу хэш коммита, `git checkout <hash>` —
состояние кода для слайда.

| Пакет | Что это |
|---|---|
| `domain` | `Money`, `OrderId`, `Email`, `Customer`, `OrderItem`, тарифы доставки, `Promo`, `OrderStatus`, `Order` с `Order.Builder` |
| `app` | Сценарии `PlaceOrder`, `PayOrder`, `ShipOrder`, `CancelOrder`, отчёты `SalesReport` → `TextSalesReport`, `CsvSalesReport`, порты `OrderRepository` и `Notifier`, `Main` |
| `infra` | `InMemoryOrderRepository`, `ConsoleNotifier` |

## Тезисы:
* `Customer` — сущность: `equals` и `hashCode` по идентификатору, а не по всем полям
* Стратегия: `DeliveryTariff` — сменяемый алгоритм доставки; `Comparator` — стратегия сравнения
* Состояние: `OrderStatus` сам знает разрешённые переходы, в `Order` нет проверок статуса
* Шаблонный метод: `SalesReport.render()` — скелет, форматы текста и CSV — шаги в подклассах
* Итератор: `Order implements Iterable<OrderItem>` — позиции можно обойти, но не изменить
* Строитель: `Order.builder()` вместо конструктора на шесть аргументов и `boolean` пятым параметром

## Для практики

На занятии у экрана: промокоды с параметрами («N%», «минус X от Y») — `Promo` из `enum` в стратегию.

`instanceof ExpressDelivery` по-прежнему в `ConsoleNotifier` (текст «привезём завтра») и в `SalesReport`
(число экспресс-заказов). Это знание о конкретном тарифе за пределами тарифа: что сломается,
если появится «экспресс в день заказа»?

## Запуск

```text
...
[EMAIL anna@example.ru] Анна, заказ ORD-4 принят. Сумма: 3190.00 руб.
ORD-1  SHIPPED   3490.00 руб.
ORD-2  PAID      3594.70 руб.
ORD-3  SHIPPED   58991.00 руб.
ORD-4  NEW       3190.00 руб.
Заказов: 4, экспресс: 1, выручка: 66075.70 руб., крупнейший: ORD-3

id;status;total_kopecks
ORD-1;SHIPPED;349000
...
TOTAL;4;6607570
```

```shell
./mvnw -pl class02OrderFlow test                      # все тесты
docker compose run --rm --build class02orderflow      # app.Main
```
