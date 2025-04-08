package ru.victortikhonov.autoserviceapp.model.ClientsAndCars;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import java.time.Year;

@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9]{1,20}$", message = "Госномер введен не верно")
    @Column(name = "state_number")
    private String stateNumber;


    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Некорректный VIN (17 символов)")
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


    @PastOrPresent(message = "Год выпуска не может быть больше текущего")
    @NotNull(message = "Год выпуска не может быть пустым")
    @Column(name = "year_of_manufacture")
    private Year yearOfManufacture;
}