# hw01bank. Стартовый код ДЗ1, задача 3

Каркас банковского счёта: `Amount`, `AccountId`, `Account`, команды `Deposit`, `Withdraw`, `Transfer`, журнал `Ledger`.
Готовы `AccountId` и `Command`, остальные методы бросают `UnsupportedOperationException("TODO")`.
Условие — в [hw01/README.md](../hw01/README.md).

## Запуск

```shell
./mvnw -pl hw01bank test
```

`BankExampleTest` выключен (`@Disabled`) — снимите, когда появится реализация.
