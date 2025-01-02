package ru.victortikhonov.autoserviceapp.model.Personnel;



public enum EmployeeStatus {

    ACTIVE("Активен"),

    DISMISSED("Уволен"),
    INACTIVE("Не активен");

    private final String description;

    EmployeeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

