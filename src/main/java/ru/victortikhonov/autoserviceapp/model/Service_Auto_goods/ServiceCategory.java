package ru.victortikhonov.autoserviceapp.model.Service_Auto_goods;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categories_services")
@Data
public class ServiceCategory {

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
    private List<Service> services;
}
