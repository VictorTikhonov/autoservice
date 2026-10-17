package ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod;

import jakarta.persistence.*;
import lombok.Data;
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
