package ru.victortikhonov.autoserviceapp.model.ClientsAndCars;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9]{1,20}$", message = "Госномер введен не верно")
    @Column(name = "state_number")
    private String stateNumber;

    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN номер должен состоять из 17 символов и соответствовать стандарту")
    @Column(name = "vin")
    private String vin;

    @NotBlank(message = "Марка автомобиля не может быть пустой")
    @Size(max = 50, message = "Марка автомобиля не должна превышать 50 символов")
    @Column(name = "brand")
    private String brand;

    @NotBlank(message = "Модель автомобиля не может быть пустой")
    @Size(max = 50, message = "Модель автомобиля не должна превышать 50 символов")
    @Column(name = "model")
    private String model;

    @NotNull(message = "Год выпуска не может быть пустым")
    @Column(name = "year_of_manufacture")
    private LocalDate yearOfManufacture;
}