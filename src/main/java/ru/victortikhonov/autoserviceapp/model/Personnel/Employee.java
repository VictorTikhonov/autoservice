package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.victortikhonov.autoserviceapp.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Data
public abstract class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    @Setter(AccessLevel.NONE)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

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

    public Employee(String surname, String name, String patronymic, String phoneNumber) {
        super(surname, name, patronymic, phoneNumber);
    }

    public Employee() {
    }

    public String getRole() {
        return account != null ? account.getRole().name() : null;  // Возвращаем роль из Account
    }
}
