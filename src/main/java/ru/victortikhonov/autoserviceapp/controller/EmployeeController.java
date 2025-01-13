package ru.victortikhonov.autoserviceapp.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Personnel.*;
import ru.victortikhonov.autoserviceapp.repository.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
@SessionAttributes({"employee", "positions", "statuses", "selectedEmployee"})
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

        model.addAttribute("success", "Сотрудник \"" + employeeName + "\" успешно добавлен");

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


    @GetMapping("/profile/{id}")
    public String checkProfileEmployee(@PathVariable Long id, Model model,
                                       @RequestParam(value = "action", required = false) String action,
                                       HttpSession httpSession) {

        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            Hibernate.initialize(employee.getAccount());
            Hibernate.initialize(employee.getPosition());
        }

        if (action != null && action.equals("cancel")) {
            httpSession.removeAttribute("selectedEmployee");
            model.addAttribute("selectedEmployee", employee);
            return "employee-details";
        }

        model.addAttribute("selectedEmployee", employee);
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("statuses", EmployeeStatus.values());

        return "employee-details";
    }


    @PostMapping("/profile/update")
    @Transactional
    public String updateEmployee(@Valid @ModelAttribute("selectedEmployee") Employee employee, Errors errors,
                                 RedirectAttributes redirectAttributes, SessionStatus sessionStatus, Model model) {

        // Создание списка ошибок
        List<String> errorMessages = new ArrayList<>();

        if (errors.hasErrors()) {
            errorMessages.add("errors");
        }

        // Если установлен статут "Уволен" а дата не стоит
        if ((employee.getEmploymentStatus().equals(EmployeeStatus.DISMISSED) && employee.getDismissalDate() == null)) {
            model.addAttribute("errorDismissalDate", "Дата увольнения не может быть пустой");
            errorMessages.add("errorDismissalDate");
        }

        // Если статут НЕ "Уволен" а дата стоит
        if ((!employee.getEmploymentStatus().equals(EmployeeStatus.DISMISSED) && employee.getDismissalDate() != null)) {
            model.addAttribute("errorDismissalDate", "Дату увольнения можно указать только при статусе \"Уволен\"");
            errorMessages.add("errorLogin");
        }

        if (employeeRepository.existsByPhoneNumberAndIdNot(employee.getPhoneNumber(), employee.getId())) {
            model.addAttribute("errorPhoneNumberUpdate", "Номер телефона уже существует");
            errorMessages.add("errorLogin");
        }

        if (!errorMessages.isEmpty()) {
            model.addAttribute("editMode", true);
            return "employee-details";
        }

        employeeRepository.save(employee);

        redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены");
        model.addAttribute("editMode", false);
        sessionStatus.setComplete();

        return "redirect:/employee/profile/" + employee.getId();
    }
}