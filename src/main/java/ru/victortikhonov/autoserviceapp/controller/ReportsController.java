package ru.victortikhonov.autoserviceapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.repository.OperatorRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/report")
public class ReportsController {
    private final OperatorRepository operatorRepository;
    private final RequestRepository requestRepository;

    public ReportsController(OperatorRepository operatorRepository, RequestRepository requestRepository) {
        this.operatorRepository = operatorRepository;
        this.requestRepository = requestRepository;
    }


    @GetMapping("/operator")
    public String createReportOperators(LocalDate startDate, LocalDate endDate, Model model) {
        // Проверка на пустые значения
        if (startDate == null || endDate == null) {
            model.addAttribute("errorMessage", "Выберите даты для формирования отчета");
            return "operator_report"; // Шаблон отчета
        }

        List<Operator> operators = (List<Operator>) operatorRepository.findAll();
        List<Request> requests = (List<Request>) requestRepository.findRequestsByDate(startDate, endDate);

        long totalRequestsCount = requests.size();

        if (totalRequestsCount == 0) {
            return "operator_report";
        }

        long totalOpen = 0;
        long totalInProgress = 0;
        long totalCompleted = 0;
        long totalRejected = 0;

        for (Request request : requests) {
            switch (request.getRequestStatus()) {
                case OPEN -> totalOpen++;
                case IN_PROGRESS -> totalInProgress++;
                case COMPLETED -> totalCompleted++;
                case REJECTED -> totalRejected++;
            }
        }


        Map<String, Long> operatorRequestCounts = new LinkedHashMap<>();

        for (Operator operator : operators) {
            long count = requests.stream()
                    .filter(r -> r.getOperator().equals(operator))
                    .count();

            String fullName = operator.getSurname() + " " + operator.getName();
            if (operator.getPatronymic() != null && !operator.getPatronymic().isEmpty()) {
                fullName += " " + operator.getPatronymic();
            }

            String key = fullName;
            if (operator.getEmploymentStatus().equals(EmployeeStatus.DISMISSED)) {
                key += " (У)";
            }
            key += "\n" + operator.getPhoneNumber();

            operatorRequestCounts.put(key, count);
        }

        // Сортируем Map по убыванию значений и сохраняем в LinkedHashMap
        Map<String, Long> sortedOperatorRequestCounts = operatorRequestCounts.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Сортировка по убыванию
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing, // В случае дубликатов оставляем первое значение
                        LinkedHashMap::new // Используем LinkedHashMap для сохранения порядка
                ));


        model.addAttribute("operatorRequestCounts", sortedOperatorRequestCounts);

        model.addAttribute("totalRequestsCount", totalRequestsCount);
        model.addAttribute("totalOpen", totalOpen);
        model.addAttribute("totalInProgress", totalInProgress);
        model.addAttribute("totalCompleted", totalCompleted);
        model.addAttribute("totalRejected", totalRejected);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        // Используем уникальные имена для атрибутов
        model.addAttribute("formattedDate", formattedStartDate + " - " + formattedEndDate);

        return "operator_report";
    }
}
