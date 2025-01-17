package ru.victortikhonov.autoserviceapp.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderStatus;

import java.util.Arrays;

@Converter(autoApply = true)
public class WorkOrderStatusConverterJPA implements AttributeConverter<WorkOrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(WorkOrderStatus status) {

        return (status == null ? null : status.getDescription());
    }

    @Override
    public WorkOrderStatus convertToEntityAttribute(String description) {

        if(description == null)
            return null;

        return Arrays.stream(WorkOrderStatus.values())
                .filter(status -> status.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное описание статуса заказ-наряда: " + description));
    }
}