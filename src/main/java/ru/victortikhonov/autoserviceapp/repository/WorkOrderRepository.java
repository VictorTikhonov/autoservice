package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.WorkOrder;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.startDate BETWEEN :startDate AND :endDate")
    Iterable<WorkOrder> findWorkOrdersByDate(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

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


    @Query("SELECT w FROM WorkOrder w WHERE w.workOrderStatuses = :status " +
            "AND w.startDate BETWEEN :startDate AND :endDate")
    Page<WorkOrder> findByStatusAndDate(@Param("status") WorkOrderStatus status,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    @Query("SELECT w FROM WorkOrder w WHERE w.startDate BETWEEN :startDate AND :endDate")
    Page<WorkOrder> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);


    @Query("SELECT w.workOrderNumber FROM WorkOrder w LEFT JOIN w.request r " +
            "WHERE r.requestStatus = :rejectedStatus " +
            "AND w.workOrderStatuses = :inProgressStatus")
    List<String> findRejectedRequestsWithInProgressWorkOrders(@Param("rejectedStatus") RequestStatus rejectedStatus,
                                                              @Param("inProgressStatus") WorkOrderStatus inProgressStatus);



    // Поиск по номеру заказа-наряда
    Page<WorkOrder> findByWorkOrderNumber(String workOrderNumber, Pageable pageable);


    // Поиск по номеру заказа-наряда с механиком
    @Query("SELECT w FROM WorkOrder w WHERE w.workOrderNumber = :workOrderNumber AND w.mechanic.id = :mechanicId")
    Page<WorkOrder> findByWorkOrderNumber(@Param("workOrderNumber") String workOrderNumber,
                                          @Param("mechanicId") Long mechanicId,
                                          Pageable pageable);


    // Поиск по номеру заявки без механика
    Page<WorkOrder> findByRequest_RequestNumber(String requestNumber, Pageable pageable);


    // Поиск по номеру заявки с механиком
    @Query("SELECT w FROM WorkOrder w WHERE w.request.requestNumber = :requestNumber AND w.mechanic.id = :mechanicId")
    Page<WorkOrder> findByRequestNumberAndMechanicId(@Param("requestNumber") String requestNumber,
                                                     @Param("mechanicId") Long mechanicId,
                                                     Pageable pageable);

    boolean existsByWorkOrderNumber(String numberWorkOrder);
}
