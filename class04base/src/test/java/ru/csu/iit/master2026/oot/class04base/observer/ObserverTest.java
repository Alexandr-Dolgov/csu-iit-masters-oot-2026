package ru.csu.iit.master2026.oot.class04base.observer;

import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObserverTest {

    private final Stock stock = new Stock();

    @Test
    void все_подписчики_узнают_об_изменении() {
        ShopWindow window = new ShopWindow();
        RestockAlert purchasing = new RestockAlert(5);
        stock.subscribe(window);
        stock.subscribe(purchasing);

        stock.set("kettle", 10);
        stock.set("kettle", 3);
        stock.set("kettle", 0);

        assertThat(window.shows("kettle")).isFalse();
        assertThat(purchasing.requests()).containsExactly("пополнить kettle");
    }

    @Test
    void новый_подписчик_без_правки_склада() {
        List<String> analytics = new ArrayList<>();
        stock.subscribe((sku, before, after) -> analytics.add(sku + ": " + before + " → " + after));

        stock.set("mug", 7);

        assertThat(analytics).containsExactly("mug: 0 → 7");
    }

    @Test
    void отписавшийся_больше_не_получает() {
        List<String> seen = new ArrayList<>();
        StockListener listener = (sku, before, after) -> seen.add(sku);
        stock.subscribe(listener);
        stock.set("mug", 1);

        stock.unsubscribe(listener);
        stock.set("mug", 2);

        assertThat(seen).containsExactly("mug");
    }

    @Test
    void упавший_подписчик_лишает_уведомления_следующих() {
        List<String> seen = new ArrayList<>();
        stock.subscribe((sku, before, after) -> {
            throw new IllegalStateException("аналитика недоступна");
        });
        stock.subscribe((sku, before, after) -> seen.add(sku));

        // уведомление — обычный синхронный вызов: исключение подписчика долетает до издателя
        assertThatThrownBy(() -> stock.set("mug", 1)).isInstanceOf(IllegalStateException.class);
        assertThat(seen).isEmpty();
    }

    /** JavaBeans: тот же паттерн в JDK, издатель — PropertyChangeSupport. */
    static final class PriceTag {
        private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
        private int price;

        PropertyChangeSupport listeners() {
            return listeners;
        }

        void price(int newPrice) {
            int old = price;
            price = newPrice;
            listeners.firePropertyChange("price", old, newPrice);
        }
    }

    @Test
    void наблюдатель_в_jdk_property_change_support() {
        PriceTag tag = new PriceTag();
        List<String> seen = new ArrayList<>();
        tag.listeners().addPropertyChangeListener((PropertyChangeEvent e) ->
                seen.add(e.getPropertyName() + ": " + e.getOldValue() + " → " + e.getNewValue()));

        tag.price(2490);
        tag.price(2490);   // не изменилось — событие не рассылается

        assertThat(seen).containsExactly("price: 0 → 2490");
    }
}
