# class03OrderFlow. Паттерны создания и обёртки на примере OrderFlow

Код к слайдам `slides/class03.md` про OrderFlow. Стартует с конечного состояния `class02OrderFlow`.
История коммитов повторяет демо по шагам: у слайдов внизу хэш коммита, `git checkout <hash>` —
состояние кода для слайда.

| Пакет | Что это |
|---|---|
| `domain` | Без изменений с занятия 2: `Money`, `Customer`, `Order` с `Order.Builder`, тарифы, `Promo`, `OrderStatus` |
| `app` | Сценарии, отчёты, порты `OrderRepository` и `Notifier`; абстрактная фабрика `Infrastructure`; компоновщик `CompositeNotifier`; `Main` |
| `infra` | `InMemoryOrderRepository`, `ConsoleNotifier`, `RefundNotifier`, семейство `ConsoleInfrastructure` |
| `vendor.sms` | Условно сторонний клиент SMS-шлюза `SmsGatewayClient` — для совместной задачи, менять нельзя |

## Тезисы:
* Фабричные методы в OrderFlow уже есть: `OrderRepository.nextId()`, `Order.builder()`, `Money.rubles(...)`
* Абстрактная фабрика: `Infrastructure` отдаёт согласованное семейство (хранилище и нотификатор),
  конкретное семейство названо только в `Main`
* В тестах — своё семейство `TestInfrastructure`: хранилище в памяти и записывающий нотификатор
* Компоновщик: `CompositeNotifier` — несколько нотификаторов под видом одного; сценарии этого не замечают
* Возвраты — ответственность бухгалтерии, а не поддержки: строка «Возврат …» переехала из `ConsoleNotifier`
  в `RefundNotifier`
* Вывод `Main` не изменился: паттерны поменяли устройство, а не поведение

## Для практики

На занятии у экрана — `vendor.sms.SmsGatewayClient`, интерфейс которого не совпадает с `Notifier`:
* номер `79000000001` без `+`, текст не длиннее 160 символов, проверяемое `GatewayTimeoutException`;
* **адаптер**: `SmsNotifier implements Notifier` поверх шлюза;
* **декоратор** повтора при сбое: каждый N-й вызов шлюза падает, повтор проходит;
* **декоратор** логирования: что отправили и чем кончилось.

Над чем подумать: в каком порядке оборачивать — повтор внутри логирования или наоборот? Что делать
с `GatewayTimeoutException`, если `Notifier` исключений не объявляет? Где теперь живёт правило
«крупные заказы — по SMS»?

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
./mvnw -pl class03OrderFlow test                      # все тесты
docker compose run --rm --build class03orderflow      # app.Main
```
