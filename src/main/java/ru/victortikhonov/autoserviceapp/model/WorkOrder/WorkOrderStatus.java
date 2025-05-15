package ru.victortikhonov.autoserviceapp.model.WorkOrder;


import ru.victortikhonov.autoserviceapp.model.TaskStatus;

public enum WorkOrderStatus implements TaskStatus {
    ALL("Все"),
    IN_PROGRESS("В процессе"), // Работы начались, но еще не завершены
    COMPLETED("Завершен"),     // Работы успешно завершены
    CANCELED("Отменен");       // Заказ-наряд отменен

    private final String description;


    WorkOrderStatus(String description) {
        this.description = description;
    }


    @Override
    public String getDescription() {
        return description;
    }
}
