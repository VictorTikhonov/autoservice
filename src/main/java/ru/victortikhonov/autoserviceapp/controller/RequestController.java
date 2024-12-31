package ru.victortikhonov.autoserviceapp.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.service.RequestService;

@Controller
@RequestMapping("/request")
@SessionAttributes("requestForm")  // Сохраняем requestForm в сессии
@Slf4j
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @ModelAttribute("requestForm")
    public RequestForm requestForm() {
        return new RequestForm();
    }

    @GetMapping("/create")
    public String registerForm(Model model) {
        return "request-form";
    }

    @PostMapping("/create")
    public String createRequest(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                                Errors errors, Model model, SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "request-form";
        }

        requestService.save(requestForm);

        // После успешного сохранения, завершить сессию для requestForm
        sessionStatus.setComplete();

        // TODO здесь должен быть редирект на страницу со всеми заявками
        return "request-form";
    }

    @PostMapping("/create/search-client")
    public String searchClient(@ModelAttribute("requestForm") RequestForm requestForm,
                               Model model, Errors errors) {

        String phoneNumber = requestForm.getClient().getPhoneNumber();

        // Валидация номера телефона
        if (!phoneNumber.matches("\\d{11}")) {
            errors.rejectValue("client.phoneNumber", "invalid.phoneNumber",
                    "Номер телефона должен состоять из 11 цифр");
            return "request-form";
        }


        Client client = requestService.findClientByPhoneNumber(phoneNumber);

        if (client != null) {
            requestForm.setClient(client);
        } else {
            // Если клиент не найден, добавим уведомление
            model.addAttribute("clientNotFound", true); // Добавляем флаг для уведомления
        }

        return "request-form";
    }
}
