package ru.victortikhonov.autoserviceapp.model.Service_Auto_goods;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "categories_services")
@Data
@ToString(exclude = "services")
public class ServiceCategory extends Category{

    @OneToMany(mappedBy = "category")
    private List<Service> services;


    public ServiceCategory() {
    }


    public ServiceCategory(String name) {
        super(name);
    }
}
