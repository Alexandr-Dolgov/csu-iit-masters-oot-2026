package ru.csu.iit.master2026.oot.class01orderflow.legacy;

/** Консольный сценарий стартового проекта. Запуск: Run 'Main.main()'. */
public class Main {

    public static void main(String[] args) {
        OrderService service = new OrderService();

        String first = service.place("Анна", "anna@example.ru", "+79000000001",
                new String[]{"Чайник", "Кружка"}, new int[]{1, 2}, new double[]{2490, 350},
                "courier", false, null);
        service.pay(first, 3490.00);
        service.ship(first);

        // Тот же заказ со страховкой: 3490 * 1.03 = 3594.70 — клиент платит ровно столько
        String second = service.place("Борис", "boris@example.ru", "+79000000002",
                new String[]{"Чайник", "Кружка"}, new int[]{1, 2}, new double[]{2490, 350},
                "courier", true, null);
        service.pay(second, 3594.70);

        String third = service.place("Вера", "vera@example.ru", "+79000000003",
                new String[]{"Ноутбук"}, new int[]{1}, new double[]{64990},
                "express", false, "SAVE10");
        service.pay(third, 58991.00);
        service.ship(third);

        System.out.println(service.report());
    }
}
