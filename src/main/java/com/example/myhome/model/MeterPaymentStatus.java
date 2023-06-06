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

    public static MeterPaymentStatus getType(String typeName) {
        switch(typeName) {
            case "NEW": return MeterPaymentStatus.NEW;
            case "COUNTED": return MeterPaymentStatus.COUNTED;
            case "COUNTED_AND_PAID": return MeterPaymentStatus.COUNTED_AND_PAID;
            case "PAID": return MeterPaymentStatus.PAID;
            default: return MeterPaymentStatus.NEW;
        }
    }
}
