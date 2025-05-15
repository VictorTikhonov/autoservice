package ru.victortikhonov.autoserviceapp.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.victortikhonov.autoserviceapp.model.ClientAndCar.*;
import java.io.Serial;
import java.io.Serializable;

@Data
public class RequestForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Valid
    private Client client;

    @Valid
    private Car car;

    @NotBlank(message = "Поле не может быть пустым")
    private String complaint;
}
