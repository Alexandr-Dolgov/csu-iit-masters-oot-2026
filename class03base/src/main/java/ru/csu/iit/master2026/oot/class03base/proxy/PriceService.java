package ru.csu.iit.master2026.oot.class03base.proxy;

/** Цена товара по артикулу, в рублях. */
public interface PriceService {

    int price(String sku);
}
