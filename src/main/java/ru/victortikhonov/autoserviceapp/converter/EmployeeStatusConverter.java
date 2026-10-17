package ru.victortikhonov.autoserviceapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;


@Component
public class EmployeeStatusConverter implements Converter<String, EmployeeStatus> {

    @Override
    public EmployeeStatus convert(String str) {
        return EmployeeStatus.valueOf(str);
    }
}
