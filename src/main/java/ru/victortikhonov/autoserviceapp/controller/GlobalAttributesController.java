package ru.victortikhonov.autoserviceapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalAttributesController {

    @ModelAttribute("userRole")
    public String userRole(HttpSession session) {
        return (String) session.getAttribute("userRole");
    }
}
