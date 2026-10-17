package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Personnel.Account;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    boolean existsByLogin(String login);
    Optional<Account> findByLogin(String login);
}
