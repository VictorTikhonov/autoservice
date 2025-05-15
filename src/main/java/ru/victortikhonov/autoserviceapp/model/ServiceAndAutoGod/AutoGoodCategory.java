package ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "categories_auto_goods")
@Data
@ToString(exclude = "autoGoods")
public class AutoGoodCategory extends Category{

    @OneToMany(mappedBy = "category")
    private List<AutoGood> autoGoods;


    public AutoGoodCategory(String name) {
        super(name);
    }


    public AutoGoodCategory() {
    }
}