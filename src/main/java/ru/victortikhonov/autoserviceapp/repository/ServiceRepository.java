package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;

import java.util.Optional;

public interface ServiceRepository extends CrudRepository<Service, Long> {

    Optional<Service> findByNameAndCategory_Id(String name, Long categoryId);
    Iterable<Service> findByCategoryId(Long categoryId);
}
