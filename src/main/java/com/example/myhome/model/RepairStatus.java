package com.example.myhome.model;

public enum RepairStatus {
    ACCEPTED("Новое"),
    IN_WORK("В работе"),
    COMPLETED("Выполнено");

    private final String name;

    RepairStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
