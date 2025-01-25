package ru.victortikhonov.autoserviceapp.model.Service_Auto_goods;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "auto_goods")
@Data
public class AutoGood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private AutoGoodCategory category;


    @NotBlank(message = "Наименование не может быть пустым")
    @Size(max = 35, message = "Наименование не должно превышать 35 символов")
    @Column(name = "name")
    private String name;


    @Column(name = "quantity")
    @NotNull(message = "Количество не может быть пустым")
    @PositiveOrZero(message = "Количество не может быть меньше 0")
    private Integer quantity;


    @Column(name = "price_one_unit")
    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal priceOneUnit;


    @Column(name = "expiration_date")
    //@NotNull(message = "Дата не может быть пустой")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;


    public int minusQuantity(int value) {

        if (value < 0) {
            throw new IllegalArgumentException("Количество для уменьшения должно быть положительным");
        }

        this.quantity = this.quantity - value;

        return this.quantity;
    }

    public int plusQuantity(int value) {

        if (value < 0) {
            throw new IllegalArgumentException("Количество для увеличения должно быть положительным");
        }

        this.quantity = this.quantity + value;

        return this.quantity;
    }
}