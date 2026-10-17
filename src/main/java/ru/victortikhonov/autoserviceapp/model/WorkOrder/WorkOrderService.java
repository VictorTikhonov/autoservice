package ru.victortikhonov.autoserviceapp.model.WorkOrder;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.Service;

import java.math.BigDecimal;

@Entity
@Table(name = "work_order_services")
@Data
@ToString(exclude = "workOrder")
public class WorkOrderService {

    @EmbeddedId
    private WorkOrderServiceId id;


    @ManyToOne
    @MapsId("workOrderId")
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;


    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;


    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0", message = "Цена должна быть не отрицательной")
    @Column(name = "price")
    private BigDecimal price;

    public WorkOrderService(WorkOrder workOrder, Service service, BigDecimal price) {
        this.workOrder = workOrder;
        this.service = service;
        this.price = price;

        this.id = new WorkOrderServiceId(workOrder.getId(), service.getId());
    }

    public WorkOrderService() {
    }
}
