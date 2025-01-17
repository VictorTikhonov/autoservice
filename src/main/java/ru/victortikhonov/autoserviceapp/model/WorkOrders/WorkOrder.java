package ru.victortikhonov.autoserviceapp.model.WorkOrders;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Request.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "work_orders")
@Data
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @OneToOne
    @JoinColumn(name = "request_id")
    private Request request;


    @OneToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;


    @Column(name = "work_order_status")
    private WorkOrderStatus workOrderStatuses;


    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;


    @OneToMany(mappedBy = "workOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<WorkOrderAutoGood> autoGoods = new ArrayList<>();


    @OneToMany(mappedBy = "workOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<WorkOrderService> services = new ArrayList<>();


    @Column(name = "start_date", updatable = false, insertable = false)
    @Setter(AccessLevel.NONE)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startDate;


    @Column(name = "end_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endDate;


    public void addAutoGood(WorkOrderAutoGood autoGood) {
        if (autoGood != null) {
            autoGood.setWorkOrder(this);
            autoGoods.add(autoGood);
        }
    }


    public void addService(WorkOrderService service) {
        if (service != null) {
            service.setWorkOrder(this);
            services.add(service);
        }
    }


    public List<WorkOrderAutoGood> getAutoGoods() {
        return Collections.unmodifiableList(autoGoods);
    }


    public List<WorkOrderService> getServices() {
        return Collections.unmodifiableList(services);
    }


    public WorkOrder() {
    }


    public WorkOrder(Request request, Mechanic mechanic, WorkOrderStatus workOrderStatuses) {
        this.request = request;
        this.mechanic = mechanic;
        this.workOrderStatuses = workOrderStatuses;
    }
}
