package ru.victortikhonov.autoserviceapp.model.Request;

import jakarta.persistence.*;
import lombok.Data;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.*;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;

import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Employee operator;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Column(name = "submission_date", updatable = false, insertable = false)
    private LocalDate submissionDate;

    @Column(name = "complaints")
    private String complaints;

//    public Request() {
//    }


    public Request(Client client, Car car, Employee operator, RequestStatus requestStatus, String complaints) {
        this.client = client;
        this.car = car;
        this.operator = operator;
        this.requestStatus = requestStatus;
        this.complaints = complaints;
    }
}
