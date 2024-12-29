package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Логин не может быть пустым")
    @Size(max = 30, message = "Логин не должен превышать 30 символов")
    @Column(name = "login", unique = true)
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(max = 100, message = "Пароль не должен превышать 100 символов")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}
