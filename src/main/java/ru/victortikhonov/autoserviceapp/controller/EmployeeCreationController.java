package ru.victortikhonov.autoserviceapp.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.victortikhonov.autoserviceapp.model.Personnel.*;
import ru.victortikhonov.autoserviceapp.repository.*;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/employee/create")
@SessionAttributes({"employee", "positions"})
public class EmployeeCreationController {

    private final AccountRepository accountRepository;
    private final OperatorRepository operatorRepository;
    private final MechanicRepository mechanicRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public EmployeeCreationController(AccountRepository accountRepository, OperatorRepository operatorRepository,
                                      MechanicRepository mechanicRepository, PositionRepository positionRepository,
                                      EmployeeRepository employeeRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.accountRepository = accountRepository;
        this.operatorRepository = operatorRepository;
        this.mechanicRepository = mechanicRepository;
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @GetMapping
    public String showEmployeeCreateForm(Model model) {

        Iterable<Position> positions = positionRepository.findByPositionNameNot("Администратор");

        Employee employee = new Employee();
        employee.setAccount(new Account());

        model.addAttribute("employee", employee);
        model.addAttribute("positions", positions);

        return "employee-create-form";
    }


    @PostMapping
    @Transactional
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee, Errors errors,
                                 @RequestParam(value = "role", required = false) String roleString,
                                 Model model, HttpSession session) {

        // Создание списка ошибок
        List<String> errorMessages = new ArrayList<>();

        // Проверка полей на ошибки
        if (errors.hasErrors()) {
            errorMessages.add("errors");
        }

        if (roleString == null) {
            model.addAttribute("errorRole", "Выберите роль");
            errorMessages.add("errorRole");
        } else {
            model.addAttribute("role", roleString);
        }

        if (employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())) {
            model.addAttribute("errorPhoneNumber", "Номер телефона уже существует");
            errorMessages.add("errorPhoneNumber");
        }

        if (accountRepository.existsByLogin(employee.getAccount().getLogin())) {
            model.addAttribute("errorLogin", "Логин уже существует");
            errorMessages.add("errorLogin");
        }

        // Если есть ошибки, возвращаю форму
        if (!errorMessages.isEmpty()) {
            return "employee-create-form";
        }

        // Хэширование пароля перед сохранением
        String encodedPassword = bCryptPasswordEncoder.encode(employee.getAccount().getPassword());
        employee.getAccount().setPassword(encodedPassword);

        employee.getAccount().setRole(Role.valueOf(roleString));

        // Создание соответствующего Работника
        if (roleString.equals("MECHANIC")) {
            createMechanic(employee);
        } else if (roleString.equals("OPERATOR")) {
            createOperator(employee);
        } else {
            model.addAttribute("error", "Неизвестная роль сотруднкиа");
            return "employee-create-form";
        }

        String employeeName = employee.getSurname() + " "
                + employee.getName().charAt(0) + "."
                + (employee.getPatronymic() != null && !employee.getPatronymic().isEmpty()
                ? employee.getPatronymic().charAt(0) + "."
                : "");

        model.addAttribute("success", "Сотрудник \"" + employeeName + "\" добавлен");

        // Создание пустого объекта Работника
        Employee employeeNew = new Employee();
        employeeNew.setAccount(new Account());

        session.removeAttribute("employee");

        model.addAttribute("employee", employeeNew);
        model.addAttribute("role", null);

        return "employee-create-form";
    }


    private Mechanic createMechanic(Employee employee) {
        Mechanic mechanic = new Mechanic(
                employee.getSurname(),
                employee.getName(),
                employee.getPatronymic() != null && !employee.getPatronymic().isEmpty()
                        ? employee.getPatronymic()
                        : null,
                employee.getPhoneNumber(),
                employee.getAccount(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getHireDate(),
                employee.getBirthDate());

        return mechanicRepository.save(mechanic);
    }


    private Operator createOperator(Employee employee) {
        Operator operator = new Operator(
                employee.getSurname(),
                employee.getName(),
                employee.getPatronymic() != null && !employee.getPatronymic().isEmpty()
                        ? employee.getPatronymic()
                        : null,
                employee.getPhoneNumber(),
                employee.getAccount(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getHireDate(),
                employee.getBirthDate()
        );

        return operatorRepository.save(operator);
    }
}
