package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @Query("SELECT w FROM WorkOrder w WHERE w.mechanic.id = :mechanicId " +
            "AND w.workOrderStatuses = :status " +
            "AND w.startDate BETWEEN :startDate AND :endDate")
    Page<WorkOrder> findByMechanicIdAndStatusAndDate(@Param("mechanicId") Long mechanicId,
                                                     @Param("status") WorkOrderStatus status,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     Pageable pageable);

    @Query("SELECT w FROM WorkOrder w WHERE w.mechanic.id = :mechanicId " +
            "AND w.startDate BETWEEN :startDate AND :endDate")
    Page<WorkOrder> findByMechanicIdAndDateRange(@Param("mechanicId") Long mechanicId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 Pageable pageable);


    @Query("SELECT w.id FROM WorkOrder w LEFT JOIN w.request r " +
            "WHERE r.requestStatus = :rejectedStatus " +
            "AND w.workOrderStatuses = :inProgressStatus")
    List<Long> findRejectedRequestsWithInProgressWorkOrders(@Param("rejectedStatus") RequestStatus rejectedStatus,
                                                            @Param("inProgressStatus") WorkOrderStatus inProgressStatus);
}
