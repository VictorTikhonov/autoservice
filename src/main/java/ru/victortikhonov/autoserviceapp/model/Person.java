package ru.victortikhonov.autoserviceapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class Person {

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
    @Column(name = "surname")
    private String surname;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    @Column(name = "name")
    private String name;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Column(name = "patronymic", nullable = true)
    private String patronymic;

    @Pattern(regexp = "^[0-9]{11}$", message = "Номер телефона должен состоять из 11 цифр")
    @Column(name = "phone_number")
    private String phoneNumber;

    public Person(String surname, String name, String patronymic, String phoneNumber) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.phoneNumber = phoneNumber;
    }

    public Person() {
    }

}

