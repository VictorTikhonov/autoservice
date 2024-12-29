package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Car;


@Repository
public interface CarRepository extends CrudRepository<Car, Long> {
    Car findByVin(String vin); // Поиск машины по VIN
}
