package ru.victortikhonov.autoserviceapp.service;


import org.springframework.stereotype.Service;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.*;
import ru.victortikhonov.autoserviceapp.repository.AutoGoodRepository;
import ru.victortikhonov.autoserviceapp.repository.ServiceRepository;
import ru.victortikhonov.autoserviceapp.repository.WorkOrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WorkOrderItemService {
    private final WorkOrderRepository workOrderRepository;
    private final AutoGoodRepository autoGoodRepository;
    private final ServiceRepository serviceRepository;


    public WorkOrderItemService(WorkOrderRepository workOrderRepository,
                                AutoGoodRepository autoGoodRepository, ServiceRepository serviceRepository) {

        this.workOrderRepository = workOrderRepository;
        this.autoGoodRepository = autoGoodRepository;
        this.serviceRepository = serviceRepository;
    }


    public Optional<WorkOrder> findWorkOrder(Long workOrderId) {
        return workOrderRepository.findById(workOrderId);
    }


    public Iterable<AutoGood> getAllAutoGoods() {
        return autoGoodRepository.findAll();
    }


    public Iterable<ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service> getAllServices() {
        return serviceRepository.findAll();
    }


    public WorkOrder saveWorkOrder(WorkOrder workOrder) {
        return workOrderRepository.save(workOrder);
    }


    public WorkOrder addAutoGoods(List<AutoGoodQuantity> autoGoods, WorkOrder workOrder) {

        for (AutoGoodQuantity autoGoodQuantity : autoGoods) {
            AutoGood autoGood = autoGoodRepository.findById(autoGoodQuantity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Товар с ID " + autoGoodQuantity.getId() + " не найден"));

            WorkOrderAutoGood workOrderAutoGood = new WorkOrderAutoGood(workOrder, autoGood,
                    autoGoodQuantity.getQuantity(), autoGood.getPriceOneUnit());

            autoGood.minusQuantity(autoGoodQuantity.getQuantity());

            workOrder.addAutoGood(workOrderAutoGood);
        }

        return workOrderRepository.save(workOrder);
    }


    public WorkOrder addServices(List<ServicePrice> servicePrices, WorkOrder workOrder) {

        for (ServicePrice servicePrice : servicePrices) {
            ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service service = serviceRepository.findById(servicePrice.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Услуга с ID " + servicePrice.getId() + " не найден"));

            WorkOrderService workOrderService = new WorkOrderService(workOrder, service, servicePrice.getPrice());

            workOrder.addService(workOrderService);
        }

        return workOrderRepository.save(workOrder);
    }
}
