package ru.victortikhonov.autoserviceapp.model.Request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import ru.victortikhonov.autoserviceapp.Task;
import ru.victortikhonov.autoserviceapp.TaskStatus;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.*;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@ToString(exclude = "workOrder")
public class Request implements Task{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "client_id")
    @Setter(AccessLevel.NONE)
    private Client client;


    @ManyToOne
    @JoinColumn(name = "car_id")
    @Setter(AccessLevel.NONE)
    private Car car;


    @ManyToOne
    @JoinColumn(name = "operator_id")
    @Setter(AccessLevel.NONE)
    private Operator operator;


    @Column(name = "request_status")
    private RequestStatus requestStatus;


    @Column(name = "submission_date", updatable = false, insertable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime submissionDate;


    @Column(name = "complaints")
    @NotBlank(message = "Поле жалоб не может быть пустым")
    private String complaints;


    @OneToOne(mappedBy = "request")
    private WorkOrder workOrder;


    public Request() {
    }


    public Request(Client client, Car car, Operator operator, RequestStatus requestStatus, String complaints) {
        this.client = client;
        this.car = car;
        this.operator = operator;
        this.requestStatus = requestStatus;
        this.complaints = complaints;
    }

    @Override
    public TaskStatus getStatus() {
        return this.requestStatus;
    }

    @Override
    public Employee getEmployee() {
        return this.operator;
    }
}
