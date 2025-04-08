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

    @NotBlank(message = "Пустое поле")
    @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
    @Column(name = "surname")
    private String surname;

    @NotBlank(message = "Пустое поле")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    @Column(name = "name")
    private String name;

    @Size(max = 50, message = "Более 50 символов")
    @Column(name = "patronymic", nullable = true)
    private String patronymic;

    @Pattern(regexp = "^[0-9]{11}$", message = "Укажите 11 цифр")
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

