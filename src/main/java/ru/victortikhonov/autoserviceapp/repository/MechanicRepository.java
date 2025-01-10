package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;

public interface MechanicRepository extends CrudRepository<Mechanic, Long> {
}
