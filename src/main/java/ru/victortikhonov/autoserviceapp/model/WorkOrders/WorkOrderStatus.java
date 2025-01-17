package ru.victortikhonov.autoserviceapp.model.WorkOrders;


public enum WorkOrderStatus {
    IN_PROGRESS("В процессе"), // Работы начались, но еще не завершены
    COMPLETED("Завершен"),     // Работы успешно завершены
    CANCELED("Отменен");       // Заказ-наряд отменен

    private final String description;


    WorkOrderStatus(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
}
