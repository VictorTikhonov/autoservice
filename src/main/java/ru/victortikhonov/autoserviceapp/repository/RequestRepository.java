package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.victortikhonov.autoserviceapp.model.Request.Request;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {
}
