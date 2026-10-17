package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.WorkOrderAutoGood;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.WorkOrderAutoGoodId;

import java.util.Optional;

public interface WorkOrderAutoGoodRepository extends CrudRepository<WorkOrderAutoGood, WorkOrderAutoGoodId> {
    Optional<WorkOrderAutoGood> findByWorkOrderIdAndAutoGoodId(Long workOrderId, Long autoGoodId);
}
