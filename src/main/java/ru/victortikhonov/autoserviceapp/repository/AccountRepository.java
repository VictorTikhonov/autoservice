package ru.victortikhonov.autoserviceapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.victortikhonov.autoserviceapp.model.Personnel.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {
    boolean existsByLogin(String login);
}
