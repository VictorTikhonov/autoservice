package ru.victortikhonov.autoserviceapp.model.ClientsAndCars;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.victortikhonov.autoserviceapp.model.Person;
import ru.victortikhonov.autoserviceapp.model.Request.Request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
public class Client extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;

    @Size(max = 50, message = "Email не должен превышать 50 символов")
    @Email(message = "Некорректный формат email")
    @Column(name = "email")
    private String email;

    @Column(name = "registration_date", updatable = false, insertable = false)
    @Setter(AccessLevel.NONE)
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "client")
    private List<Request> requests = new ArrayList<>();

    @Override
    public String toString() {
        return "Клиент: " + super.getSurname() + " " + super.getName() + " " + super.getPatronymic() +
                "\nДата регистрации: " + this.registrationDate +
                "\nПочта: " + (this.email == null ? "отсутствует" : this.email);
    }

    public List<Request> getEmployees() {
        return Collections.unmodifiableList(requests);
    }

}
