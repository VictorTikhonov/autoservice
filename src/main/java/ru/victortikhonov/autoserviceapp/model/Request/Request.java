package ru.victortikhonov.autoserviceapp.model.Request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.*;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Data
public class Request {

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
    private LocalDate submissionDate;


    @Column(name = "complaints")
    @NotBlank(message = "Поле жалоб не может быть пустым")
    private String complaints;


    public Request() {
    }


    public Request(Client client, Car car, Operator operator, RequestStatus requestStatus, String complaints) {
        this.client = client;
        this.car = car;
        this.operator = operator;
        this.requestStatus = requestStatus;
        this.complaints = complaints;
    }
}
