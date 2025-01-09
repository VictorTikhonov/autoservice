package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGoodCategory;

import java.util.Optional;

public interface AutoGoodCategoryRepository extends CrudRepository<AutoGoodCategory, Long> {
    Optional<AutoGoodCategory> findByName(String name);
}