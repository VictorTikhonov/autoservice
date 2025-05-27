package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("ADMIN")
@Data
public class Admin extends Employee{

    private Admin(String surname, String name, String patronymic, String phoneNumber,
                 Account account, Position position, BigDecimal salary,
                 LocalDate hireDate, LocalDate birthDate) {
        super(surname, name, patronymic, phoneNumber, account, position, salary, hireDate, birthDate);
    }

    protected Admin() {
    }
}
