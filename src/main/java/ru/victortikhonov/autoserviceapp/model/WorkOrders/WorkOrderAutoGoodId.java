package ru.victortikhonov.autoserviceapp.model.WorkOrders;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;


@Embeddable
@Data
public class WorkOrderAutoGoodId implements Serializable {

    @Column(name = "work_order_id")
    private Long workOrderId;


    @Column(name = "auto_good_id")
    private Long autoGoodId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkOrderAutoGoodId workOrderAutoGoodId = (WorkOrderAutoGoodId) o;

        if (!workOrderId.equals(workOrderAutoGoodId.workOrderId)) return false;
        return autoGoodId.equals(workOrderAutoGoodId.autoGoodId);
    }


    @Override
    public int hashCode() {
        int result = workOrderId.hashCode();
        result = 31 * result + autoGoodId.hashCode();
        return result;
    }


    public WorkOrderAutoGoodId() {
    }

    public WorkOrderAutoGoodId(Long workOrderId, Long autoGoodId) {
        this.workOrderId = workOrderId;
        this.autoGoodId = autoGoodId;
    }
}