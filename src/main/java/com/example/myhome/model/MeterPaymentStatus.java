package com.example.myhome.model;

public enum MeterPaymentStatus {
    NEW("Новое"),
    COUNTED("Учтено"),
    COUNTED_AND_PAID("Учтено и оплачено"),
    PAID("Оплачено");

    private final String name;

    MeterPaymentStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
