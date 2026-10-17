package ru.victortikhonov.autoserviceapp.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.AutoGood;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.*;
import ru.victortikhonov.autoserviceapp.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkOrderItemService {
    private final WorkOrderRepository workOrderRepository;
    private final AutoGoodRepository autoGoodRepository;
    private final ServiceRepository serviceRepository;
    private final WorkOrderAutoGoodRepository workOrderAutoGoodRepository;
    private final WorkOrderServiceRepository workOrderServiceRepository;
    private final RequestRepository requestRepository;


    public WorkOrderItemService(WorkOrderRepository workOrderRepository,
                                AutoGoodRepository autoGoodRepository, ServiceRepository serviceRepository, WorkOrderAutoGoodRepository workOrderAutoGoodRepository, WorkOrderServiceRepository workOrderServiceRepository, RequestRepository requestRepository) {

        this.workOrderRepository = workOrderRepository;
        this.autoGoodRepository = autoGoodRepository;
        this.serviceRepository = serviceRepository;
        this.workOrderAutoGoodRepository = workOrderAutoGoodRepository;
        this.workOrderServiceRepository = workOrderServiceRepository;
        this.requestRepository = requestRepository;
    }


    public Optional<WorkOrder> findWorkOrder(Long workOrderId) {
        return workOrderRepository.findById(workOrderId);
    }


    public Iterable<AutoGood> getAllAutoGoods() {
        return autoGoodRepository.findByRelevanceTrue();
    }


    public Iterable<ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.Service> getAllServices() {
        return serviceRepository.findByRelevanceTrue();
    }


    public WorkOrder saveWorkOrder(WorkOrder workOrder) {

        Request request = workOrder.getRequest();

        if (!request.getRequestStatus().equals(RequestStatus.REJECTED)) {
            request.setRequestStatus(RequestStatus.COMPLETED);
        }

        requestRepository.save(request);

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
            ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.Service service = serviceRepository.findById(servicePrice.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Услуга с ID " + servicePrice.getId() + " не найден"));

            WorkOrderService workOrderService = new WorkOrderService(workOrder, service, servicePrice.getPrice());

            workOrder.addService(workOrderService);
        }

        return workOrderRepository.save(workOrder);
    }

    public int removeAutoGoodFromWorkOrder(Long workOrderId, Long autoGoodId) {

        if (workOrderRepository.findById(workOrderId).isPresent()) {
            Optional<WorkOrderAutoGood> workOrderAutoGoodOptional = workOrderAutoGoodRepository
                    .findByWorkOrderIdAndAutoGoodId(workOrderId, autoGoodId);

            if (workOrderAutoGoodOptional.isPresent()) {
                WorkOrderAutoGood workOrderAutoGood = workOrderAutoGoodOptional.get();

                workOrderAutoGoodRepository.delete(workOrderAutoGood);

                workOrderAutoGood.getAutoGood().plusQuantity(workOrderAutoGood.getQuantity());

                return 0;
            } else {
                return -1;
            }
        } else {
            return -2;
        }
    }

    public int removeServiceFromWorkOrder(Long workOrderId, Long serviceId) {

        if (workOrderRepository.findById(workOrderId).isPresent()) {
            Optional<WorkOrderService> workOrderServiceOptional = workOrderServiceRepository
                    .findByWorkOrderIdAndServiceId(workOrderId, serviceId);

            if (workOrderServiceOptional.isPresent()) {
                WorkOrderService workOrderService = workOrderServiceOptional.get();

                workOrderServiceRepository.delete(workOrderService);

                return 0;
            } else {
                return -1;
            }
        } else {
            return -2;
        }
    }
}
