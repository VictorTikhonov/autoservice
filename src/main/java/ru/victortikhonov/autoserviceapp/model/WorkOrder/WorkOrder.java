package ru.victortikhonov.autoserviceapp.model.WorkOrder;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.victortikhonov.autoserviceapp.NumberGenerator;
import ru.victortikhonov.autoserviceapp.model.Task;
import ru.victortikhonov.autoserviceapp.model.TaskStatus;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
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
public class WorkOrder implements Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @Column(name = "work_order_number")
    @Setter(AccessLevel.NONE)
    private String workOrderNumber;


    @OneToOne
    @JoinColumn(name = "request_id")
    private Request request;


    @OneToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;


    @Column(name = "work_order_status")
    @Enumerated(EnumType.STRING)
    private WorkOrderStatus workOrderStatuses;


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


    public WorkOrder() {
    }


    public WorkOrder(Request request, Mechanic mechanic,
                     WorkOrderStatus workOrderStatuses, String workOrderNumber) {
        this.request = request;
        this.mechanic = mechanic;
        this.workOrderStatuses = workOrderStatuses;
        this.workOrderNumber = workOrderNumber;
    }


    public void addAutoGood(WorkOrderAutoGood autoGood) {

        if (autoGood == null || autoGood.getAutoGood() == null || autoGood.getQuantity() <= 0) {
            return;  // Если товар или его количество некорректны, не добавляю
        }

        // Проверяю, существует ли товар уже в списке
        for (WorkOrderAutoGood existingAutoGood : autoGoods) {
            if (existingAutoGood.getAutoGood().equals(autoGood.getAutoGood())) {

                // Если товар уже существует, увеличиваю его количество
                existingAutoGood.setQuantity(existingAutoGood.getQuantity() + autoGood.getQuantity());
                return;
            }
        }

        // Если товара нет в списке, добавляю новый
        autoGood.setWorkOrder(this);  // Устанавливаем ссылку на текущий заказ
        autoGoods.add(autoGood);
    }


    public void addService(WorkOrderService service) {

        if (service == null || service.getService() == null || service.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return;
        }

        // Проверяю, существует ли услуга в списке
        for (WorkOrderService existingService : services) {
            if (existingService.getService().equals(service.getService())) {
                // Если услуга уже существует, обновляю цену
                existingService.setPrice(service.getPrice());
                return;
            }
        }

        // Если услуги нет в списке, добавляю новую
        service.setWorkOrder(this);  // Устанавливаю ссылку на текущий заказ
        services.add(service);
    }


    public List<WorkOrderAutoGood> getAutoGoods() {
        return Collections.unmodifiableList(autoGoods);
    }


    public List<WorkOrderService> getServices() {
        return Collections.unmodifiableList(services);
    }


    public BigDecimal calculatePrice() {
        return this.calculateTotalPriceAutoGoods().
                add(this.calculateTotalPriceServices());
    }


    public BigDecimal calculateTotalPriceAutoGoods() {

        BigDecimal price = BigDecimal.ZERO;

        for (WorkOrderAutoGood autoGood : this.autoGoods) {
            price = price.add(autoGood.getPriceOneUnit().multiply(new BigDecimal(autoGood.getQuantity())));
        }

        return price;
    }


    public BigDecimal calculateTotalPriceServices() {

        BigDecimal price = BigDecimal.ZERO;

        for (WorkOrderService service : this.services) {
            price = price.add(service.getPrice());
        }

        return price;
    }

    @Override
    public TaskStatus getStatus() {
        return this.workOrderStatuses;
    }

    @Override
    public Employee getEmployee() {
        return this.mechanic;
    }

    public String getWorkOrderNumber() {
        return NumberGenerator.toRussian(this.workOrderNumber);
    }
}
