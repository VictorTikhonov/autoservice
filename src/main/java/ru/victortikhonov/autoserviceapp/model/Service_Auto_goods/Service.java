package ru.victortikhonov.autoserviceapp.model.Service_Auto_goods;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


@Entity
@Table(name = "services")
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;


    @NotBlank(message = "Наименование не может быть пустым")
    @Size(max = 35, message = "Наименование не должно превышать 35 символов")
    @Column(name = "name")
    private String name;


    @NotBlank(message = "Опсиание не должно быть пустым")
    @Column(name = "description")
    private String description;


    @Column(name = "relevance")
    private Boolean relevance = true;


    public Service() {
    }
}