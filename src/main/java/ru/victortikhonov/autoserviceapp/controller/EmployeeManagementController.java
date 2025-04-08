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
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;
import ru.victortikhonov.autoserviceapp.repository.EmployeeRepository;
import ru.victortikhonov.autoserviceapp.repository.PositionRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
@SessionAttributes({"positions", "statuses", "selectedEmployee"})
public class EmployeeManagementController {

    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;


    public EmployeeManagementController(PositionRepository positionRepository, EmployeeRepository employeeRepository) {

        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
    }


    @GetMapping("/list")
    public String listEmployees(@RequestParam(value = "status", required = false) EmployeeStatus status,
                                Model model, SessionStatus sessionStatus) {

        sessionStatus.setComplete();

        // Если status равен null, задаю значения по умолчанию
        List<EmployeeStatus> defaultStatuses = List.of(EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE);
        Iterable<Employee> employees = (status != null)
                ? employeeRepository.findByEmploymentStatus(status)
                : employeeRepository.findByEmploymentStatusIn(defaultStatuses);

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
            model.addAttribute("errorDismissalDate", "Пустое поле");
            errorMessages.add("errorDismissalDate");
        }

        // Если статут НЕ "Уволен" а дата стоит
        if ((!employee.getEmploymentStatus().equals(EmployeeStatus.DISMISSED) && employee.getDismissalDate() != null)) {
            model.addAttribute("errorDismissalDate",
                    "Доступно при статусе \"Уволен\"");
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