package ru.victortikhonov.autoserviceapp.model.WorkOrders;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;

import java.math.BigDecimal;

@Entity
@Table(name = "work_order_auto_goods")
@Data
@ToString(exclude = "workOrder")
public class WorkOrderAutoGood {

    @EmbeddedId
    private WorkOrderAutoGoodId workOrderAutoGoodId;


    @ManyToOne
    @MapsId("workOrderId")
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;


    @ManyToOne
    @MapsId("autoGoodId")
    @JoinColumn(name = "auto_good_id")
    private AutoGood autoGood;


    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть больше нуля")
    @Column(name = "quantity")
    private Integer quantity;


    @NotNull(message = "Цена за единицу не может быть пустой")
    @DecimalMin(value = "0.01", message = "Цена за единицу должна быть больше нуля")
    @Column(name = "price_one_unit")
    private BigDecimal priceOneUnit;

    public WorkOrderAutoGood(WorkOrder workOrder, AutoGood autoGood, Integer quantity, BigDecimal priceOneUnit) {
        this.workOrder = workOrder;
        this.autoGood = autoGood;
        this.quantity = quantity;
        this.priceOneUnit = priceOneUnit;

        this.workOrderAutoGoodId = new WorkOrderAutoGoodId(workOrder.getId(), autoGood.getId());
    }

    public WorkOrderAutoGood() {
    }


    public BigDecimal calculatePrice(){

        return priceOneUnit.multiply(new BigDecimal(quantity));
    }
}