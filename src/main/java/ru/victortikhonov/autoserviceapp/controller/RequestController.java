package ru.victortikhonov.autoserviceapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.NumberGenerator;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeDetails;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.service.RequestService;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                                Errors errors, @AuthenticationPrincipal EmployeeDetails employeeDetails,
                                SessionStatus sessionStatus, RedirectAttributes redirectAttributes, Model model) {

        if (errors.hasErrors()) {
            return "request-form";
        }

        Employee employee = employeeDetails.getEmployee();
        if (!(employee instanceof Operator operator)) {
            model.addAttribute("errorMessage", "Только оператор может создать заявку");
            return "error-page";
        }

        try {
            String requestNumber = requestService.createRequest(requestForm, operator);

            sessionStatus.setComplete(); // Завершаю сессию для requestForm

            redirectAttributes.addFlashAttribute("success",
                    "Заявка №" + NumberGenerator.toRussian(requestNumber) + " создана!");

            return "redirect:/request/list";
        } catch (IllegalStateException e) {

            model.addAttribute("alertMessage", e.getMessage());
            return "request-form";
        }
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
                () -> model.addAttribute("alertMessage", "Клиент с таким номером телефона не найден")  // Если клиент не найден
        );

        return "request-form";
    }


    @GetMapping("/list")
    public String listRequests(@RequestParam(required = false) RequestStatus status,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "7") int size,
                               @RequestParam(required = false) String searchNumber,
                               @RequestParam(required = false) String searchPhone,
                               @RequestParam(required = false) boolean myRequests,
                               Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails) {

        // Уставливаю по умолчанию фильтр дат на 7 дней
        if (startDate == null) {
            startDate = LocalDate.now().minusWeeks(1);
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

        Operator operator = null;
        if (myRequests) {
            Employee employee = employeeDetails.getEmployee();
            if (!(employee instanceof Operator op)) {
                model.addAttribute("errorMessage", "Только оператор может просмотреть свои заявки");
                return "error-page";
            }
            operator = op;
        }

        requests = findRequestsByCriteria(searchNumber, searchPhone, pageable, status,
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), operator);

        // Добавляю в модель
        model.addAttribute("requests", requests);
        model.addAttribute("statuses", RequestStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, requests.getTotalPages()));
        model.addAttribute("totalItems", requests.getTotalElements());
        model.addAttribute("searchNumber", searchNumber);
        model.addAttribute("searchPhone", searchPhone);
        model.addAttribute("myRequests", myRequests);

        return "table-requests";
    }

    private Page<Request> findRequestsByCriteria(String searchNumber,
                                                 String searchPhone,
                                                 Pageable pageable,
                                                 RequestStatus status,
                                                 LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime,
                                                 Operator operator) {

        if (searchNumber != null && searchPhone != null && !searchPhone.isEmpty()) {
            return requestService.findRequestsByNumberAndPhone(searchNumber, searchPhone, pageable);
        } else if (searchNumber != null) {
            return requestService.findRequestsByNumber(searchNumber, pageable);
        } else if (searchPhone != null && !searchPhone.isEmpty()) {
            return requestService.findRequestsByPhone(searchPhone, pageable);
        } else {
            return requestService.findRequests(status, startDateTime, endDateTime, pageable, operator);
        }
    }


    @GetMapping("/details")
    public String checkRequest(@RequestParam("requestId") Long requestId, Model model) {

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
            redirectAttributes.addFlashAttribute("errorCancelRequest", "Заявка не найдена");
            return "redirect:/request/list";
        }

        Request request = requestOptional.get();

        if (requestService.cancelRequest(request)) {
            redirectAttributes.addFlashAttribute("success", "Заявка №" +
                    NumberGenerator.toRussian(request.getRequestNumber()) + " отклонена!");
        } else {
            redirectAttributes.addFlashAttribute("errorCancelRequest", "Заявка №" +
                    NumberGenerator.toRussian(request.getRequestNumber()) + " не может быть отменена из-за статуса");
        }

        return "redirect:/request/list";
    }
}