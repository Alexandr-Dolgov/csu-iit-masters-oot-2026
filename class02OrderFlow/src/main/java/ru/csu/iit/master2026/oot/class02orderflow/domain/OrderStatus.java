package ru.csu.iit.master2026.oot.class02orderflow.domain;

/**
 * Статус заказа. Паттерн «Состояние»: каждый статус сам знает, куда из него можно перейти.
 *
 * <p>Переходы: NEW → PAID → SHIPPED; NEW и PAID → CANCELLED. Остальное — исключение.
 */
public enum OrderStatus {

    NEW {
        @Override
        OrderStatus pay() {
            return PAID;
        }

        @Override
        OrderStatus cancel() {
            return CANCELLED;
        }
    },

    PAID {
        @Override
        OrderStatus ship() {
            return SHIPPED;
        }

        @Override
        OrderStatus cancel() {
            return CANCELLED;
        }

        @Override
        public boolean paid() {
            return true;
        }
    },

    SHIPPED {
        @Override
        public boolean paid() {
            return true;
        }
    },

    CANCELLED;

    /** Переходы по умолчанию запрещены: статус разрешает только свои. */
    OrderStatus pay() {
        throw forbidden("оплатить");
    }

    OrderStatus ship() {
        throw forbidden("отправить");
    }

    OrderStatus cancel() {
        throw forbidden("отменить");
    }

    /** Деньги за заказ получены. */
    public boolean paid() {
        return false;
    }

    private IllegalStateException forbidden(String action) {
        return new IllegalStateException("Нельзя " + action + " заказ в статусе " + this);
    }
}
