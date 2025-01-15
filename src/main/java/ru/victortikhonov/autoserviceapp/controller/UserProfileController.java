package ru.victortikhonov.autoserviceapp.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/my-profile")
@SessionAttributes("employeeProfile")
public class UserProfileController {
    private final EmployeeRepository employeeRepository;

    public UserProfileController(EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }


    // TODO тут должен передаватсья авторизированный сотрудник (во всех методах)
    @GetMapping
    public String showProfile(Model model) {
        Employee employee = employeeRepository.findById(10L)
                .orElseThrow(() -> new EntityNotFoundException("Работник не найден (10)"));

        model.addAttribute("employeeProfile", employee);

        return "employee-profile";
    }


    @GetMapping("/update-password")
    public String showFormUpdatePassword() {
        return "employee-update-password";
    }


    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @ModelAttribute("employeeProfile") Employee employee,
                                 Model model, RedirectAttributes redirectAttributes) {

        // Проверка на пустоту и длину пароля
        if (newPassword.isBlank() || newPassword.length() > 100) {
            model.addAttribute("errorNewPassword",
                    "Пароль не может быть пустым и должен содержать не более 100 символов");
            return "employee-update-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Пароли не совпадают");
            return "employee-update-password";
        }

        employee.getAccount().setPassword(newPassword);
        employeeRepository.save(employee);

        redirectAttributes.addFlashAttribute("success", "Пароль успешно обновлен!");

        return "redirect:/my-profile";
    }


    @GetMapping("/update-info")
    public String showFormUpdateProfile(@ModelAttribute("employeeProfile") Employee employee, Model model) {

        return "employee-update-profile";
    }


    @PostMapping("/update-info")
    public String updateProfile(@Valid @ModelAttribute("employeeProfile") Employee employee, Errors errors,
                                Model model, RedirectAttributes redirectAttributes) {

        // Создание списка ошибок
        List<String> errorMessages = new ArrayList<>();

        if (errors.hasErrors()) {
            errorMessages.add("errors");
        }

        if (employeeRepository.existsByPhoneNumberAndIdNot(employee.getPhoneNumber(), employee.getId())) {
            model.addAttribute("errorPhoneNumberUpdate", "Номер телефона уже существует");
            errorMessages.add("errorLogin");
        }

        if (!errorMessages.isEmpty()) {
            return "employee-update-profile";
        }

        employeeRepository.save(employee);

        redirectAttributes.addFlashAttribute("success", "Личная информация успешно обновлена!");

        return "redirect:/my-profile";
    }
}
