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
import ru.victortikhonov.autoserviceapp.model.Request.*;
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
                                Errors errors, SessionStatus sessionStatus) {

        if (errors.hasErrors()) {
            return "request-form";
        }

        // Ошибок нет, сохраняю заявку
        requestService.save(requestForm);

        // Завершаю сессию для requestForm
        sessionStatus.setComplete();

        return "redirect:/request/list";
    }



    @PostMapping("/create/search-client")
    public String searchClient(@ModelAttribute("requestForm") RequestForm requestForm,
                               Model model, Errors errors) {

        String phoneNumber = requestForm.getClient().getPhoneNumber();

        // Проверяю номера телефона
        if (!phoneNumber.matches("\\d{11}")) {
            errors.rejectValue("client.phoneNumber", "invalid.phoneNumber",
                    "Номер телефона должен состоять из 11 цифр");
            return "request-form";
        }

        // Ищю клиента по номеру
        Client client = requestService.findClientByPhoneNumber(phoneNumber);

        if (client != null) {
            requestForm.setClient(client);
        } else {
            // Если клиент не найден, добавляю уведомление
            model.addAttribute("clientNotFound", true);
        }

        return "request-form";
    }



    @GetMapping("/list")
    public String listRequests(@RequestParam(required = false) RequestStatus status,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               Model model) {

        // Устанавливаю сегодняшнюю дату если она не установлена
        // и, если endDate имеет значение более чем сегодняшняя дата
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null || endDate.isAfter(LocalDate.now())) {
            endDate = LocalDate.now();
        }


        // Проверка на правильность дат
        if (startDate.isAfter(endDate)) {
            // Добавляем сообщение об ошибке в модель
            model.addAttribute("dateError", "Дата начала не может быть позже даты окончания.");

            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "request-list";
        }

        // Если статус не установлен то пор умолч. ставлю "В ожидании"
        if (status == null) {
            status = RequestStatus.OPEN;
        }

        // Получаю отфильтрованные заявки
        List<Request> filteredRequests =
                requestService.findRequests(status, startDate, endDate.plusDays(1));

        // Добавляю в модель
        model.addAttribute("requests", filteredRequests);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "request-list";
    }



    @PostMapping("/check")
    public String checkRequest(@RequestParam Long requestId, Model model) {

        // Получаю заявку
        Request request = requestService.findRequestById(requestId).orElse(null);

        if (request != null) {

            // Форматирование
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedDate = request.getSubmissionDate() != null ?
                    request.getSubmissionDate().format(formatter) : "Дата отсутствует";

            model.addAttribute("request", request);
            model.addAttribute("formattedDate", formattedDate);
            model.addAttribute("requestStatus", request.getRequestStatus().getDescription());
        }

        return "request-details";
    }
}