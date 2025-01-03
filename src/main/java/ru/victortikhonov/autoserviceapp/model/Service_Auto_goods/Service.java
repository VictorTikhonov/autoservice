package ru.victortikhonov.autoserviceapp.model.Service_Auto_goods;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


@Entity
@Table(
        name = "services",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "category_id"})
)
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private ServiceCategory category;


    @NotBlank(message = "Наименование не может быть пустым")
    @Size(max = 35, message = "Наименование не должно превышать 35 символов")
    @Column(name = "name")
    private String name;


    @NotBlank(message = "Опсиание не должно быть пустым")
    @Column(name = "description")
    private String description;
}


/*
-- Создание таблицы Услуги
CREATE TABLE services (
                          id BIGSERIAL PRIMARY KEY,        -- ID услуги, автоинкремент
                          name VARCHAR(35) NOT NULL,       -- Наименование услуги
                          description TEXT,                -- Описание услуги
                          category_id BIGINT NOT NULL,     -- ID категории услуги

    -- Внешний ключ на таблицу categories_of_services
                          CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_services(id),

    -- Уникальность пары: категория + услуга
                          CONSTRAINT unique_service_name_per_category UNIQUE (name, category_id)
);
 */