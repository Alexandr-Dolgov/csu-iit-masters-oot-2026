package ru.csu.iit.master2026.oot.class04base.transactional;

import java.util.List;

/** Отдельный бин: вызывает {@link TransferService} через ссылку, которую дал контейнер, — то есть через прокси. */
public final class Payroll {

    private final TransferService transfers;

    public Payroll(TransferService transfers) {
        this.transfers = transfers;
    }

    public void payAll(String from, List<String> to, int amount) {
        for (String recipient : to) {
            transfers.transfer(from, recipient, amount);
        }
    }
}
