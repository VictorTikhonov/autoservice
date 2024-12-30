package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Request.Request;


public interface RequestRepository extends CrudRepository<Request, Long> {
}
