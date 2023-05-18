package com.example.myhome.model;

public enum InvoiceStatus {
    PAID("Оплачена"),
    PARTIALLY_PAID("Частично оплачена"),
    UNPAID("Неоплачена");

    private final String name;

    InvoiceStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
