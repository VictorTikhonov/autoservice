package ru.victortikhonov.autoserviceapp.model.WorkOrders;

import lombok.Data;

import java.util.List;

@Data
public class WorkOrderItemsDTO {

    private Long workOrderId;


    private List<AutoGoodQuantity> autoGoodQuantity;


    private List<ServicePrice> servicePrice;
}