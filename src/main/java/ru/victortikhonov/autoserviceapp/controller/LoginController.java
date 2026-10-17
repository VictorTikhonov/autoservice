package ru.victortikhonov.autoserviceapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeDetails;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @GetMapping("/login-success")
    public String handleLoginSuccess(@AuthenticationPrincipal EmployeeDetails employeeDetails, HttpSession session) {
        // Получаем роль пользователя
        String role = employeeDetails.getEmployee().getAccount().getRole().name();

        // Сохраняем роль в сессии (без префикса "ROLE_")
        session.setAttribute("userRole", role.replace("ROLE_", ""));

        // Перенаправляем на стартовую страницу в зависимости от роли
        if (role.equals("ADMIN")) {
            return "redirect:/employee/list";
        } else if (role.equals("OPERATOR")) {
            return "redirect:/request/list";
        } else if (role.equals("MECHANIC")) {
            return "redirect:/work-order/list";
        }

        return "redirect:/my-profile";
    }
}