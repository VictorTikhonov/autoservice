package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;


public interface OperatorRepository extends CrudRepository<Operator, Long> {

}
