package ru.victortikhonov.autoserviceapp.model.WorkOrder;

import lombok.Data;

import java.util.List;

@Data
public class WorkOrderItemsDTO {

    private Long workOrderId;


    private List<AutoGoodQuantity> autoGoodQuantity;


    private List<ServicePrice> servicePrice;
}