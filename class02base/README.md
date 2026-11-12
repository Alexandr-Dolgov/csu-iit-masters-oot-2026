# class02base. Подстановка. Контракт equals и hashCode. Команда

Код к Части 1 слайдов `slides/class02.md` и к слайдам о команде. Демо — тесты: каждый тест
показывает, что держится и что ломается. У слайдов внизу хэш коммита, `git checkout <hash>` —
состояние кода для слайда.

| Пакет | Что это |
|---|---|
| `shapes` | `Square extends Rectangle` ломает клиента `Layout`; затем неизменяемые `Rectangle` и `Square` рядом под `Shape` |
| `jdk` | Подстановка в JDK: `List.of` и `add`, `Stack extends Vector`, `Timestamp extends Date` |
| `equality` | `Point` и `ColorPoint`: `equals` без `hashCode`, нарушение симметричности, транзитивности, подстановки; композиция и записи |
| `command` | `Command`, `Editor` с отменой; `Runnable`, `Callable`, `ExecutorService` |

## Тезисы:
* Подтип обязан выполнять контракт базового типа, а не только его сигнатуры
* `Square extends Rectangle`: компилятор доволен, клиент `Rectangle` получает не то
* Неизменяемость снимает проблему: «поменять ширину» — значит получить новый объект
* JDK тоже нарушает подстановку: optional operations в `List`, `Stack extends Vector`, `Timestamp extends Date`
* Контракт `equals`: рефлексивность, симметричность, транзитивность, согласованность, `x.equals(null) == false`
* Переопределил `equals` — переопредели `hashCode`, иначе `HashSet` и `HashMap` теряют объекты
* Наследник с новым значимым полем не может сохранить контракт `equals` и подстановку одновременно
* `getClass()` в `equals` держит контракт, но ломает подстановку
* Композиция вместо наследования: `ColorPoint` содержит `Point`
* Записи: `equals`, `hashCode`, `toString` по компонентам; запись `final`
* Команда: запрос как объект — его можно сохранить, отложить, отменить

## Запуск

```shell
./mvnw -pl class02base test
```

Тесты запускаются и из IDEA: Run на классе или методе теста.
