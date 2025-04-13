package ru.victortikhonov.autoserviceapp.model.Request;


import ru.victortikhonov.autoserviceapp.TaskStatus;

public enum RequestStatus implements TaskStatus {
    ALL("Все"),
    OPEN("В ожидании"),
    IN_PROGRESS("Исполняется"),
    COMPLETED("Завершена"),
    REJECTED("Отклонена");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
