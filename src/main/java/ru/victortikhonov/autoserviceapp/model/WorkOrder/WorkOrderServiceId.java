package ru.victortikhonov.autoserviceapp.model.WorkOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Embeddable
@Data
public class WorkOrderServiceId implements Serializable {

    @Column(name = "work_order_id")
    private Long workOrderId;


    @Column(name = "service_id")
    private Long serviceId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkOrderServiceId workOrderServiceIdId = (WorkOrderServiceId) o;

        if (!workOrderId.equals(workOrderServiceIdId.workOrderId)) return false;
        return serviceId.equals(workOrderServiceIdId.serviceId);
    }


    @Override
    public int hashCode() {
        int result = workOrderId.hashCode();
        result = 31 * result + serviceId.hashCode();
        return result;
    }


    public WorkOrderServiceId() {
    }

    public WorkOrderServiceId(Long workOrderId, Long serviceId) {
        this.workOrderId = workOrderId;
        this.serviceId = serviceId;
    }
}
