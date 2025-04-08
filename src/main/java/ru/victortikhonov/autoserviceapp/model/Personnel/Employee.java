package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.victortikhonov.autoserviceapp.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Data
public class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    @Valid
    private Account account;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;


    @Column(name = "employment_status")
    private EmployeeStatus employmentStatus;


    @NotNull(message = "Пустое поле")
    @Column(name = "salary")
    private BigDecimal salary;


    @NotNull(message = "Пустое поле")
    @Column(name = "hire_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;


    @Column(name = "dismissal_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dismissalDate;


    @NotNull(message = "Пустое поле")
    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;


    public Employee(String surname, String name, String patronymic, String phoneNumber,
                    Account account, Position position, BigDecimal salary,
                    LocalDate hireDate, LocalDate birthDate) {
        super(surname, name, patronymic, phoneNumber);
        this.account = account;
        this.position = position;
        this.employmentStatus = EmployeeStatus.INACTIVE;
        this.salary = salary;
        this.hireDate = hireDate;
        this.birthDate = birthDate;
    }


    public Employee() {
    }
}
