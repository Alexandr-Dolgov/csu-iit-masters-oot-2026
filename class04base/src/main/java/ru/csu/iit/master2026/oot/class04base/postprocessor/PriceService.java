package ru.csu.iit.master2026.oot.class04base.postprocessor;

/** Цена товара по артикулу, в рублях. Из занятия 3. */
public interface PriceService {

    int price(String sku);
}
