package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Personnel.Position;

public interface PositionRepository extends CrudRepository<Position, Long> {
}
