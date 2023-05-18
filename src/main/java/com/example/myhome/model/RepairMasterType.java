package com.example.myhome.model;

public enum RepairMasterType {
    ANY("Любой специалист"),
    ELECTRICIAN("Электрик"),
    PLUMBER("Сантехник");

    private final String name;

    RepairMasterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
