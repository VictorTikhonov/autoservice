package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;

import java.util.Optional;


public interface ClientRepository extends CrudRepository<Client, Long> {

    Optional<Client> findByPhoneNumber(String phoneNumber);  // Поиск клиента по номеру
}
