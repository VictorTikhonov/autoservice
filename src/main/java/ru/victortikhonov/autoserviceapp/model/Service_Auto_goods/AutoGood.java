package ru.victortikhonov.autoserviceapp.model.Service_Auto_goods;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import java.math.BigDecimal;


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
    @Setter(AccessLevel.NONE)
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


    @Column(name = "manufacturer")
    @NotBlank(message = "Наименование не может быть пустым")
    @Size(max = 35, message = "Наименование не должно превышать 35 символов")
    private String manufacturer;


    @NotBlank(message = "Опсиание не должно быть пустым")
    @Column(name = "description")
    private String description;


    @Column(name = "relevance")
    private Boolean relevance;
}