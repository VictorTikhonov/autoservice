package ru.victortikhonov.autoserviceapp.model.Personnel;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "employee_statuses")
@Data
public class EmployeeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Название статуса не может быть пустым")
    @Size(max = 35, message = "Название статуса не должно превышать 35 символов")
    @Column(name = "status_name", unique = true)
    private String statusName;
}

