package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.victortikhonov.autoserviceapp.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
public class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account accountId;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position positionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmployeeStatus employmentStatus;

    @NotNull(message = "Зарплата не может быть пустой")
    @Column(name = "salary")
    private BigDecimal salary;

    @NotNull(message = "Дата трудоустройства не может быть пустой")
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "dismissal_date")
    private LocalDate dismissalDate;

    @NotNull(message = "Дата дня рождения не может быть пустой")
    @Column(name = "birth_date")
    private LocalDate birthDate;
}
