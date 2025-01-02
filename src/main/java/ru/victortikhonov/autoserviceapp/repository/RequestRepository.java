package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Request.*;
import java.time.LocalDate;
import java.util.List;


public interface RequestRepository extends CrudRepository<Request, Long> {

    // Поиск заявки по статусу и дате
    @Query("SELECT r FROM Request r WHERE r.requestStatus = :status " +
            "AND r.submissionDate BETWEEN :startDate AND :endDate")
    List<Request> findRequestsByStatusAndDate(@Param("status") RequestStatus status,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}
