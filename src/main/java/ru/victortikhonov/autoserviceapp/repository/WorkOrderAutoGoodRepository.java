package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderAutoGood;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderAutoGoodId;

import java.util.Optional;

public interface WorkOrderAutoGoodRepository extends CrudRepository<WorkOrderAutoGood, WorkOrderAutoGoodId> {
    Optional<WorkOrderAutoGood> findByWorkOrderIdAndAutoGoodId(Long workOrderId, Long autoGoodId);
}
