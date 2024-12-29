package ru.victortikhonov.autoserviceapp.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.*;

@Data
public class RequestForm {
    @Valid
    private Client client;

    @Valid
    private Car car;

    @NotBlank(message = "Поле не может быть пустым")
    private String complaint;
}
