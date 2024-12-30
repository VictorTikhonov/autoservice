package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import ru.victortikhonov.autoserviceapp.model.Request.Request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@DiscriminatorValue("OPERATOR")
@Data
public class Operator extends Employee {

    @OneToMany(mappedBy = "operator")
    private List<Request> requests = new ArrayList<>();

    public Operator(String surname, String name, String patronymic, String phoneNumber,
                    Account accountId, Position positionId, BigDecimal salary,
                    LocalDate hireDate, LocalDate birthDate) {
        super(surname, name, patronymic, phoneNumber, accountId, positionId, salary, hireDate, birthDate);
    }

    public Operator() {
    }

    public List<Request> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public String toString() {

        return "Operator{" +
                "name='" + super.getName() + '\'' +
                '}';
    }

}
