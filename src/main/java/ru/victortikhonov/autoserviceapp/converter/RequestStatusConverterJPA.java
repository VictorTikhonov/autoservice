package ru.victortikhonov.autoserviceapp.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;

import java.util.Arrays;

@Converter(autoApply = true)
public class RequestStatusConverterJPA implements AttributeConverter<RequestStatus, String> {

    @Override
    public String convertToDatabaseColumn(RequestStatus status) {

        return (status == null ? null : status.getDescription());
    }

    @Override
    public RequestStatus convertToEntityAttribute(String description) {

        if(description == null)
            return null;

        return Arrays.stream(RequestStatus.values())
                .filter(status -> status.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное описание статуса: " + description));
    }
}