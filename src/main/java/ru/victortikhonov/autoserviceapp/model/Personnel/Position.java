package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "positions")
@Data
@ToString(exclude = "employees")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;


    @NotBlank(message = "Название должности не может быть пустым")
    @Size(max = 25, message = "Название должности не должно превышать 25 символов")
    @Column(name = "position_name", unique = true)
    private String positionName;


    public Position(String positionName) {
        this.positionName = positionName;
    }


    public Position() {
    }


    @Override
    public String toString() {
        return "Наименование должности: " + positionName;
    }
}

