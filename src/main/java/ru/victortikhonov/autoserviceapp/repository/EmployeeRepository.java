package ru.victortikhonov.autoserviceapp.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    Iterable<Employee> findByEmploymentStatus(EmployeeStatus status);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);
}
