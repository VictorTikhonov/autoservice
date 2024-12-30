package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;


public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findByPhoneNumber(String phoneNumber);
}
