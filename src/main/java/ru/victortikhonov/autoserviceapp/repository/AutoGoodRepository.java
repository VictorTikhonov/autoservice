package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;

import java.util.Optional;

public interface AutoGoodRepository  extends CrudRepository<AutoGood, Long> {
    Optional<AutoGood> findByNameAndCategory_Id(String name, Long categoryId);
    Iterable<AutoGood> findByCategoryId(Long categoryId);
}
