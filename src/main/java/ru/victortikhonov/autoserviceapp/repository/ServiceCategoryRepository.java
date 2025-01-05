package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.ServiceCategory;

import java.util.Optional;

public interface ServiceCategoryRepository extends CrudRepository<ServiceCategory, Long> {
    Optional<ServiceCategory> findByName(String name);
}
