package ru.victortikhonov.autoserviceapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.victortikhonov.autoserviceapp.model.Personnel.Account;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeDetails;
import ru.victortikhonov.autoserviceapp.repository.AccountRepository;
import ru.victortikhonov.autoserviceapp.repository.EmployeeRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository, AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = accountRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Аккаунт не найден"));

        Employee employee = employeeRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return new EmployeeDetails(employee);
    }
}
