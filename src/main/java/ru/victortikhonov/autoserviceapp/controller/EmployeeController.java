package ru.victortikhonov.autoserviceapp.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.victortikhonov.autoserviceapp.model.Personnel.*;
import ru.victortikhonov.autoserviceapp.repository.*;

@Controller
@RequestMapping("/employee")
@SessionAttributes({"employee", "positions"})
public class EmployeeController {

    private final AccountRepository accountRepository;
    private final OperatorRepository operatorRepository;
    private final MechanicRepository mechanicRepository;
    private final PositionRepository positionRepository;

    private final EmployeeRepository employeeRepository;

    public EmployeeController(AccountRepository accountRepository, OperatorRepository operatorRepository,
                              MechanicRepository mechanicRepository, PositionRepository positionRepository,
                              EmployeeRepository employeeRepository) {

        this.accountRepository = accountRepository;
        this.operatorRepository = operatorRepository;
        this.mechanicRepository = mechanicRepository;
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/create")
    public String showEmployeeCreateForm(Model model) {
        Iterable<Position> positions = positionRepository.findAll();

        Employee employee = new Employee();
        employee.setAccount(new Account());

        model.addAttribute("employee", employee);
        model.addAttribute("positions", positions);

        return "employee-create-form";
    }


    @PostMapping("/create")
    @Transactional
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee, Errors errors,
                                 @RequestParam(value = "role", required = false) String roleString,
                                 Model model, HttpSession session) {

        if (roleString != null) {
            model.addAttribute("role", roleString);
        }

        if (errors.hasErrors() || roleString == null) {
            if (roleString == null) {
                model.addAttribute("errorRole", "Выберите роль");
            }

            return "employee-create-form";
        }

        if (employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())) {
            model.addAttribute("errorPhoneNumber", "Номер телефона уже существует");
            return "employee-create-form";
        }

        if (accountRepository.existsByLogin(employee.getAccount().getLogin())) {
            model.addAttribute("errorLogin", "Логин уже существует");
            return "employee-create-form";
        }


        employee.getAccount().setRole(Role.valueOf(roleString));

        if (roleString.equals("MECHANIC")) {

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

            mechanicRepository.save(mechanic);
        } else if (roleString.equals("OPERATOR")) {
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

            operatorRepository.save(operator);
        } else {
            model.addAttribute("error", "");
            return "employee-create-form";
        }

        String employeeName = employee.getSurname() + " "
                + employee.getName().charAt(0) + "."
                + (employee.getPatronymic() != null && !employee.getPatronymic().isEmpty()
                ? employee.getPatronymic().charAt(0) + "."
                : "");


        model.addAttribute("success", "Сотрудник \"" + employeeName + "\" успешно добавлен");

        Employee employeeNew = new Employee();
        employeeNew.setAccount(new Account());
        session.removeAttribute("employee");
        model.addAttribute("employee", employeeNew);
        model.addAttribute("role", null);

        return "employee-create-form";
    }


    @GetMapping("/list")
    public String listEmployees(@RequestParam(value = "status", required = false) EmployeeStatus status,
                                Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();

        Iterable<Employee> employees;

        if (status != null) {
            employees = employeeRepository.findByEmploymentStatus(status);
        } else {
            employees = employeeRepository.findAll();
        }
        model.addAttribute("employees", employees);
        model.addAttribute("filterStatus", status);

        return "table-employee";
    }
}
