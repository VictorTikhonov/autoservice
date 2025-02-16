package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;

import java.util.List;
import java.util.Optional;

public interface AutoGoodRepository extends JpaRepository<AutoGood, Long> {

    Page<AutoGood> findByCategoryIdAndRelevanceTrue(Long categoryId, Pageable pageable);

    @Query("SELECT a FROM AutoGood a WHERE a.relevance = true AND a.quantity > 0")
    Page<AutoGood> findByRelevanceTrue(Pageable pageable);

    @Query("SELECT a FROM AutoGood a WHERE a.relevance = true AND a.quantity > 0")
    Iterable<AutoGood> findByRelevanceTrue();

    Optional<AutoGood> findByNameAndCategory_Id(String name, Long categoryId);

    // Поиск по названию (с учетом актуальности)
    Page<AutoGood> findByNameContainingIgnoreCaseAndRelevanceTrue(String name, Pageable pageable);
}
