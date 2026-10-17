package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("MECHANIC")
@Data
public class Mechanic extends Employee{

    public Mechanic(String surname, String name, String patronymic, String phoneNumber,
                    Account accountId, Position positionId, BigDecimal salary,
                    LocalDate hireDate, LocalDate birthDate) {
        super(surname, name, patronymic, phoneNumber, accountId,
                positionId, salary, hireDate, birthDate);
    }


    public Mechanic() {
    }

}
