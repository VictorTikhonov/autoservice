package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
}
