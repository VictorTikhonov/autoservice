package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.Service;

import java.util.Optional;

public interface ServiceRepository extends CrudRepository<Service, Long> {

    Optional<Service> findByNameAndCategory_Id(String name, Long categoryId);
    
    Iterable<Service> findByCategoryIdAndRelevanceTrue(Long categoryId);

    Iterable<Service> findByRelevanceTrue();
}
