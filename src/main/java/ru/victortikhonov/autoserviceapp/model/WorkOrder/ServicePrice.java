package ru.victortikhonov.autoserviceapp.model.WorkOrder;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicePrice {

    private Long id;

    private BigDecimal price;
}
