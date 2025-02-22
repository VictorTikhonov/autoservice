package ru.victortikhonov.autoserviceapp;

import lombok.Data;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;


@Data
public class OperatorRequestCount {
    private Operator operator;
    private Long requestCount;

    public OperatorRequestCount(Operator operator, Long requestCount) {
        this.operator = operator;
        this.requestCount = requestCount;
    }
}
