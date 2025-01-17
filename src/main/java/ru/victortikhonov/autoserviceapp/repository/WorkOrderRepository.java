package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;

public interface WorkOrderRepository extends CrudRepository<WorkOrder, Long> {
}
