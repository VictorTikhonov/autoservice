package ru.victortikhonov.autoserviceapp.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    @Query("select e from Employee e where e.employmentStatus in :statuses and TYPE(e) <> Admin")
    Iterable<Employee> findByEmploymentStatusInExcludeAdmin(@Param("statuses") List<EmployeeStatus> statuses);

    @Query("select e from Employee e where e.employmentStatus = :status and TYPE(e) <> Admin")
    Iterable<Employee> findByEmploymentStatusExcludeAdmin(@Param("status") EmployeeStatus status);

    Optional<Employee> findByAccountId(Long id);
}
