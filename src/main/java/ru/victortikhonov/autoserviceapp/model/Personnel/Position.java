package ru.victortikhonov.autoserviceapp.model.Personnel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "positions")
@Data
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Название должности не может быть пустым")
    @Size(max = 25, message = "Название должности не должно превышать 25 символов")
    @Column(name = "position_name", unique = true)
    private String positionName;

    // TODO надо ли это поле?
    @OneToMany(mappedBy = "positionId")
    private Set<Employee> employees = new HashSet<>();
}

