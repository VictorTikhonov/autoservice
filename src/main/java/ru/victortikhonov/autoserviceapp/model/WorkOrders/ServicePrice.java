package ru.victortikhonov.autoserviceapp.model.WorkOrders;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicePrice {

    private Long id;


    private BigDecimal price;
}
