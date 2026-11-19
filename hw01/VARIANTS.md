# ДЗ1. Варианты задачи 2

Номер варианта — номер в списке группы. Часть А — отчёт, часть Б — статус. Условие задачи — в [README.md](README.md).

| № | Часть А | Часть Б |
|---|---|---|
| 1 | А1 `MarkdownSalesReport` | Б1 `DELIVERED` |
| 2 | А2 `JsonSalesReport` | Б1 `DELIVERED` |
| 3 | А3 `HtmlSalesReport` | Б1 `DELIVERED` |
| 4 | А4 `XmlSalesReport` | Б1 `DELIVERED` |
| 5 | А5 `ByStatusSalesReport` | Б1 `DELIVERED` |
| 6 | А6 `PaidOnlySalesReport` | Б1 `DELIVERED` |
| 7 | А1 `MarkdownSalesReport` | Б2 `RETURNED` |
| 8 | А2 `JsonSalesReport` | Б2 `RETURNED` |
| 9 | А3 `HtmlSalesReport` | Б2 `RETURNED` |
| 10 | А4 `XmlSalesReport` | Б2 `RETURNED` |
| 11 | А5 `ByStatusSalesReport` | Б2 `RETURNED` |
| 12 | А6 `PaidOnlySalesReport` | Б2 `RETURNED` |
| 13 | А1 `MarkdownSalesReport` | Б3 `ASSEMBLING` |
| 14 | А2 `JsonSalesReport` | Б3 `ASSEMBLING` |
| 15 | А3 `HtmlSalesReport` | Б3 `ASSEMBLING` |
| 16 | А4 `XmlSalesReport` | Б3 `ASSEMBLING` |
| 17 | А5 `ByStatusSalesReport` | Б3 `ASSEMBLING` |
| 18 | А6 `PaidOnlySalesReport` | Б3 `ASSEMBLING` |
| 19 | А1 `MarkdownSalesReport` | Б4 `ON_HOLD` |
| 20 | А2 `JsonSalesReport` | Б4 `ON_HOLD` |
| 21 | А3 `HtmlSalesReport` | Б4 `ON_HOLD` |
| 22 | А4 `XmlSalesReport` | Б4 `ON_HOLD` |
| 23 | А5 `ByStatusSalesReport` | Б4 `ON_HOLD` |
| 24 | А6 `PaidOnlySalesReport` | Б4 `ON_HOLD` |


## Часть А. Отчёт

Класс в пакете `app`: `public final class <Имя> extends SalesReport` с конструктором `(OrderRepository orders)`.
Вывод `render()` должен совпасть с примером символ в символ, включая переводы строк в конце (или их отсутствие).

Примеры построены на трёх заказах — так же, как в `SalesReportTest`, плюс третий заказ:

```java
Order paid = placeOrder.place(Order.builder()
        .customer(anna).item("Ноутбук", 1, rubles(64990)).delivery(new ExpressDelivery()));
paid.pay();                                                        // ORD-1  PAID     65490.00
placeOrder.place(Order.builder()
        .customer(anna).item("Кружка", 1, rubles(350)).delivery(new CourierDelivery()));  // ORD-2  NEW  650.00
Order shipped = placeOrder.place(Order.builder()
        .customer(anna).item("Кружка", 2, rubles(350)).delivery(new Pickup()));
shipped.pay();
shipped.ship();                                                    // ORD-3  SHIPPED  700.00
```

Итоги те же, что у `TextSalesReport`: заказов 3, экспресс 1, выручка (оплаченные) 66190.00 руб., крупнейший ORD-1.

### А1. `MarkdownSalesReport` — таблица Markdown

```text
| id | status | total |
|---|---|---|
| ORD-1 | PAID | 65490.00 руб. |
| ORD-2 | NEW | 650.00 руб. |
| ORD-3 | SHIPPED | 700.00 руб. |

Заказов: 3, экспресс: 1, выручка: 66190.00 руб., крупнейший: ORD-1
```

Последняя строка заканчивается переводом строки. Пустое хранилище — шапка таблицы, пустая строка и
`Заказов: 0, экспресс: 0, выручка: 0.00 руб., крупнейший: —`.

### А2. `JsonSalesReport` — JSON

```json
{
  "orders": [
    {"id": "ORD-1", "status": "PAID", "totalKopecks": 6549000},
    {"id": "ORD-2", "status": "NEW", "totalKopecks": 65000},
    {"id": "ORD-3", "status": "SHIPPED", "totalKopecks": 70000}
  ],
  "count": 3,
  "express": 1,
  "revenueKopecks": 6619000,
  "largest": "ORD-1"
}
```

После `}` — перевод строки. Запятые — только между заказами, после последнего запятой нет.
Пустое хранилище:

```json
{
  "orders": [
  ],
  "count": 0,
  "express": 0,
  "revenueKopecks": 0,
  "largest": null
}
```

### А3. `HtmlSalesReport` — HTML-таблица

```html
<table>
<tr><th>id</th><th>status</th><th>total</th></tr>
<tr><td>ORD-1</td><td>PAID</td><td>65490.00 руб.</td></tr>
<tr><td>ORD-2</td><td>NEW</td><td>650.00 руб.</td></tr>
<tr><td>ORD-3</td><td>SHIPPED</td><td>700.00 руб.</td></tr>
<tr><td colspan="2">Заказов: 3, экспресс: 1, крупнейший: ORD-1</td><td>66190.00 руб.</td></tr>
</table>
```

После `</table>` — перевод строки. Пустое хранилище — без строк заказов, в итогах `0`, `0`, `—` и `0.00 руб.`.

### А4. `XmlSalesReport` — XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<report>
  <order id="ORD-1" status="PAID" totalKopecks="6549000"/>
  <order id="ORD-2" status="NEW" totalKopecks="65000"/>
  <order id="ORD-3" status="SHIPPED" totalKopecks="70000"/>
  <totals count="3" express="1" revenueKopecks="6619000" largest="ORD-1"/>
</report>
```

После `</report>` — перевод строки. Пустое хранилище — без `<order>`, а у `<totals>` нет атрибута `largest`:
`<totals count="0" express="0" revenueKopecks="0"/>`.

### А5. `ByStatusSalesReport` — итоги по статусам

Строки — как у `TextSalesReport`; перед общими итогами — строка на каждый статус: сколько заказов и на какую сумму
(все заказы этого статуса, не только оплаченные). Статусы — в порядке первого появления в отчёте; статусов
без заказов нет.

```text
ORD-1  PAID      65490.00 руб.
ORD-2  NEW       650.00 руб.
ORD-3  SHIPPED   700.00 руб.
PAID: 1, 65490.00 руб.
NEW: 1, 650.00 руб.
SHIPPED: 1, 700.00 руб.
Заказов: 3, экспресс: 1, выручка: 66190.00 руб., крупнейший: ORD-1
```

Перевода строки в конце нет, как у `TextSalesReport`. Два новых заказа по 650.00 дают строку `NEW: 2, 1300.00 руб.`.

### А6. `PaidOnlySalesReport` — только оплаченные

В отчёт попадают только оплаченные заказы, и итоги тоже считаются только по ним: «заказов» — это оплаченных
заказов, «крупнейший» — среди оплаченных. `TextSalesReport` и `CsvSalesReport` по-прежнему показывают все заказы.

```text
ORD-1  PAID      65490.00 руб.
ORD-3  SHIPPED   700.00 руб.
Оплаченных заказов: 2, экспресс: 1, выручка: 66190.00 руб., крупнейший: ORD-1
```

Перевода строки в конце нет. Пустое хранилище — `Оплаченных заказов: 0, экспресс: 0, выручка: 0.00 руб., крупнейший: —`.


## Часть Б. Статус

Сейчас: `NEW → PAID → SHIPPED`, `NEW` и `PAID → CANCELLED`. Всё, что не разрешено вариантом, по-прежнему запрещено
(`IllegalStateException`, статус не меняется). Методы ниже — публичные методы `Order`.

### Б1. `DELIVERED` — вручён

* `order.deliver()`: `SHIPPED → DELIVERED`. Из других статусов — нельзя.
* Из `DELIVERED` ничего нельзя: ни оплатить, ни отправить, ни отменить, ни вручить ещё раз.
* Вручённый заказ оплачен (`status().paid()`) и входит в выручку отчётов.

### Б2. `RETURNED` — возврат

* `order.returnOrder()`: `SHIPPED → RETURNED`. Из других статусов — нельзя.
* Сценарий `app.ReturnOrder` с конструктором `(OrderRepository, Notifier)` и методом `returnOrder(OrderId)`:
  переводит заказ в `RETURNED`, сохраняет и сообщает клиенту через `notifier.orderCancelled(order, refund)`,
  где `refund` — полная сумма заказа. Если вернуть нельзя — `IllegalStateException`, уведомления нет.
* Из `RETURNED` ничего нельзя. Возвращённый заказ не оплачен (`status().paid()` — `false`) и в выручку не входит.

### Б3. `ASSEMBLING` — сборка на складе

* `order.assemble()`: `PAID → ASSEMBLING`. Из других статусов — нельзя.
* `ASSEMBLING → SHIPPED` тем же `ship()`. Отправить `PAID` без сборки по-прежнему можно.
* Собираемый заказ оплачен и входит в выручку.
* Отмена через `CancelOrder` из `ASSEMBLING` разрешена, но клиенту возвращается сумма за вычетом 5% за сборку
  (`Money.percent`): заказ на 650.00 → возврат 617.50 руб. Отмена из `PAID` по-прежнему возвращает всё.

### Б4. `ON_HOLD` — приостановлен

* `order.hold()`: `NEW` или `PAID → ON_HOLD`. Из других статусов (и из самого `ON_HOLD`) — нельзя.
* `order.resume()`: `ON_HOLD →` тот статус, из которого заказ приостановили. Из других статусов — нельзя.
* В `ON_HOLD` нельзя оплатить и отправить, но можно отменить. Отмена через `CancelOrder` возвращает деньги,
  если заказ был оплачен до приостановки, и ничего — если не был.
* Приостановленный оплаченный заказ остаётся оплаченным: входит в выручку отчётов.
