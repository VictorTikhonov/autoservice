package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Personnel.Position;

import java.util.List;

public interface PositionRepository extends CrudRepository<Position, Long> {
    Iterable<Position> findByPositionNameNot(String positionName);
}
