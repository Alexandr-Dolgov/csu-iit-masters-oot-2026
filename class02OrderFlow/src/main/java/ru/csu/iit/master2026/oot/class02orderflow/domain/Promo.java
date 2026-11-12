package ru.csu.iit.master2026.oot.class02orderflow.domain;

/** Промокод маркетинга. Действует только на товары, не на доставку и страховку. */
public enum Promo {

    NONE {
        @Override
        public Money apply(Money goods) {
            return goods;
        }
    },

    /** Скидка 10% на товары. */
    SAVE10 {
        @Override
        public Money apply(Money goods) {
            return goods.minus(goods.percent(10));
        }
    },

    /** Минус 500 руб. на товары от 3000 руб. */
    SAVE500 {
        @Override
        public Money apply(Money goods) {
            return goods.isAtLeast(Money.rubles(3000)) ? goods.minus(Money.rubles(500)) : goods;
        }
    };

    public abstract Money apply(Money goods);
}
