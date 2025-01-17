package ru.victortikhonov.autoserviceapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderStatus;


@Component
public class WorkOrderStatusConverter implements Converter<String, WorkOrderStatus> {

    @Override
    public WorkOrderStatus convert(String str) {
        return WorkOrderStatus.valueOf(str);
    }
}
