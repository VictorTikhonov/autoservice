package ru.victortikhonov.autoserviceapp.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.service.RequestService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
@RequestMapping("/request")
@SessionAttributes("requestForm")
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
                                Errors errors, SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            return "request-form";
        }

        // Ошибок нет, создаю и сохраняю заявку
        Long requestId = requestService.createRequest(requestForm);

        // Завершаю сессию для requestForm
        sessionStatus.setComplete();

        redirectAttributes.addFlashAttribute("success",
                "Заявка №" + requestId + " успешно создана!");

        // TODO или оставить на этой странице
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
        requestService.findClientByPhoneNumber(phoneNumber).ifPresentOrElse(
                requestForm::setClient,  // Если клиент найден
                () -> model.addAttribute("clientNotFound", true)  // Если клиент не найден
        );

        return "request-form";
    }


    @GetMapping("/list")
    public String listRequests(@RequestParam(required = false) RequestStatus status,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) Long searchId,
                               @RequestParam(required = false) String searchPhone,
                               Model model) {

        // Устанавливаю сегодняшнюю дату если она не установлена
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

            return "table-requests";
        }

        // Если статус не установлен то по умолч. ставлю "В ожидании"
        if (status == null) {
            status = RequestStatus.OPEN;
        }

        // Формирую фильтрацию
        Pageable pageable = PageRequest.of(page, size);
        Page<Request> requests;
        if (searchId != null && searchPhone != null && !searchPhone.isEmpty()) {
            requests = requestService.findRequestsByIdAndPhone(searchId, searchPhone, pageable);
        } else if (searchId != null) {
            requests = requestService.findRequestsById(searchId, pageable);
        } else if (searchPhone != null && !searchPhone.isEmpty()) {
            requests = requestService.findRequestsByPhone(searchPhone, pageable);
        } else {
            requests = requestService.findRequests(status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), pageable);
        }

        // Добавляю в модель
        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, requests.getTotalPages()));
        model.addAttribute("totalItems", requests.getTotalElements());
        model.addAttribute("searchId", searchId);
        model.addAttribute("searchPhone", searchPhone);

        return "table-requests";
    }


    @PostMapping("/check")
    public String checkRequest(@RequestParam("requestId") Long requestId, Model model) {

        // Получаю заявку
        requestService.findRequestById(requestId).ifPresent(request -> {

            model.addAttribute("request", request);
            model.addAttribute("requestStatus", request.getRequestStatus());
        });

        return "request-details";
    }


    @PostMapping("/cancel")
    public String cancelRequest(@RequestParam("requestId") Long requestId, RedirectAttributes redirectAttributes) {

        Optional<Request> requestOptional = requestService.findRequestById(requestId);

        if (requestOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCancelRequest", "Заявка №" + requestId + " не найдена");
            return "redirect:/request/list";
        }

        Request request = requestOptional.get();

        if (requestService.cancelRequest(request)) {
            redirectAttributes.addFlashAttribute("success", "Заявка №" + requestId + " успешно отклонена!");
        } else {
            redirectAttributes.addFlashAttribute("errorCancelRequest", "Заявка №" + requestId + " не может быть отменена из-за статуса");
        }

        return "redirect:/request/list";
    }
}