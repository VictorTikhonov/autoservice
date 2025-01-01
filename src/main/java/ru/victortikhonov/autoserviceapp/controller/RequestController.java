package ru.victortikhonov.autoserviceapp.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.service.RequestService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/request")
@SessionAttributes("requestForm")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }


    @GetMapping("/create")
    public String registerForm(Model model) {

        model.addAttribute("requestForm", new RequestForm());
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

        return "redirect:/request/list";
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


    @GetMapping("/list")
    public String listRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model, SessionStatus sessionStatus) {

        //sessionStatus.setComplete();

        // Проверка на правильность дат
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            // Добавляем сообщение об ошибке в модель
            model.addAttribute("dateError", "Дата начала не может быть позже даты окончания.");
            return "request-list";
        }

        if (endDate == null || endDate.isAfter(LocalDate.now())) {
            endDate = LocalDate.now();
        }

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (status == null) {
            status = RequestStatus.OPEN;
        }


        // Получаем отфильтрованные заявки
        List<Request> filteredRequests =
                requestService.findRequests(status, startDate,  endDate.plusDays(1));

        // Добавляем в модель
        model.addAttribute("requests", filteredRequests);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "request-list";
    }


    @PostMapping("/check")
    public String checkRequest(@RequestParam Long requestId, Model model) {
        Request request = requestService.findRequestById(requestId).orElse(null);
        if (request != null) {
            // Форматирование
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedDate = request.getSubmissionDate() != null ?
                    request.getSubmissionDate().format(formatter) : "Дата отсутствует";
            model.addAttribute("formattedDate", formattedDate);

            model.addAttribute("request", request);
            model.addAttribute("requestStatus", request.getRequestStatus().getDescription());
        }
        return "request-details";
    }
}
