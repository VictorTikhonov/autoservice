package ru.victortikhonov.autoserviceapp.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;

import java.util.Arrays;

@Converter(autoApply = true)
public class EmployeeStatusConverterJPA implements AttributeConverter<EmployeeStatus, String> {

    @Override
    public String convertToDatabaseColumn(EmployeeStatus status) {
        return status == null ? null : status.getDescription();
    }

    @Override
    public EmployeeStatus convertToEntityAttribute(String description) {
        if (description == null)
            return null;

        return Arrays.stream(EmployeeStatus.values())
                .filter(status -> description.equals(status.getDescription()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное описание статуса: " + description));
    }
}

//        for (EmployeeStatus status : EmployeeStatus.values()) {
//            if (description.equals(status.getDescription()))
//                return status;
//        }
//        throw new IllegalArgumentException("Неизвестное описание статуса: " + description);