package ru.victortikhonov.autoserviceapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
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

@Entity
@Table(name = "categories_auto_goods")
@Data
public class AutoGoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @NotBlank(message = "Наименование не может быть пустым")
    @Size(max = 35, message = "Наименование не должно превышать 35 символов")
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<AutoGood> autoGoods;
}


1 - Теперь нужен функционал для добавления товара
2 - При выборе категории должен быть выпадающий список в котором будет выбор из доступных категорий
3 - Количество устанавливается по умолчанию в 0
4 - relevance заполняется бд так что его не будет в форме
 */
@Controller
@RequestMapping("auto-goods")
public class AutoGoodController {
}
