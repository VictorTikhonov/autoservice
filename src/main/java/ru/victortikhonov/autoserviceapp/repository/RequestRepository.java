package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;

import java.time.LocalDate;


public interface RequestRepository extends JpaRepository<Request, Long> {

    // Поиск заявки по статусу и дате
    @Query("SELECT r FROM Request r WHERE r.requestStatus = :status " +
            "AND r.submissionDate BETWEEN :startDate AND :endDate")
    Page<Request> findRequestsByStatusAndDate(@Param("status") RequestStatus status,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              Pageable pageable);

    Page<Request> findById(Long searchId, Pageable pageable);

    Page<Request> findByIdAndClientPhoneNumber(Long searchId, String searchPhone, Pageable pageable);

    Page<Request> findByClientPhoneNumber(String searchPhone, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.submissionDate BETWEEN :startDate AND :endDate")
    Iterable<Request> findRequestsByDate(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

}
