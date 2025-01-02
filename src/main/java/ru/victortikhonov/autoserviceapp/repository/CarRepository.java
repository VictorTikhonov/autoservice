package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Car;
import java.util.List;


public interface CarRepository extends CrudRepository<Car, Long> {
    List<Car> findByVin(String vin); // Поиск машин по VIN
}
