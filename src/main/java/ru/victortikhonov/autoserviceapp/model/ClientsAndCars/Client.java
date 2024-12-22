package ru.victortikhonov.autoserviceapp.model.ClientsAndCars;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.victortikhonov.autoserviceapp.Person;


import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Data
public class Client extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50, message = "Email не должен превышать 50 символов")
    @Email(message = "Некорректный формат email")
    @Column(name = "email")
    private String email;

    @Column(name = "registration_date", updatable = false, insertable = false)
    private LocalDate registrationDate;
}
