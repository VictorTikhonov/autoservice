package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderService;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderServiceId;

import java.util.Optional;

public interface WorkOrderServiceRepository extends CrudRepository<WorkOrderService, WorkOrderServiceId> {
    Optional<WorkOrderService> findByWorkOrderIdAndServiceId(Long workOrderId, Long serviceId);
}