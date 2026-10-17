package ru.victortikhonov.autoserviceapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;


@Component
public class RequestStatusConverter implements Converter<String, RequestStatus> {

    @Override
    public RequestStatus convert(String str) {
        return RequestStatus.valueOf(str);
    }
}
