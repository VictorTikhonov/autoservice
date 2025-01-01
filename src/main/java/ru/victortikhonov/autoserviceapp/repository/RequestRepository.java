package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;

import java.time.LocalDate;
import java.util.List;


public interface RequestRepository extends CrudRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.requestStatus = :status " +
            "AND r.submissionDate BETWEEN :startDate AND :endDate")
    List<Request> findRequestsByStatusAndDate(
            @Param("status") RequestStatus status,  // Используем RequestStatus, а не String
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
