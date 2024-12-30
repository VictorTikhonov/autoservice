package ru.victortikhonov.autoserviceapp.model.Request;



public enum RequestStatus {
    OPEN("В ожидании"),
    IN_PROGRESS("Исполняется"),
    COMPLETED("Завершена"),
    REJECTED("Отклонена");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
