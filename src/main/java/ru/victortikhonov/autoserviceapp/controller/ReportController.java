package ru.victortikhonov.autoserviceapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.victortikhonov.autoserviceapp.model.Task;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Personnel.EmployeeStatus;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.WorkOrder;
import ru.victortikhonov.autoserviceapp.repository.MechanicRepository;
import ru.victortikhonov.autoserviceapp.repository.OperatorRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;
import ru.victortikhonov.autoserviceapp.repository.WorkOrderRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/report")
public class ReportController {
    private final OperatorRepository operatorRepository;
    private final RequestRepository requestRepository;
    private final MechanicRepository mechanicRepository;

    private final WorkOrderRepository workOrderRepository;


    public ReportController(OperatorRepository operatorRepository, RequestRepository requestRepository,
                            MechanicRepository mechanicRepository, WorkOrderRepository workOrderRepository) {
        this.operatorRepository = operatorRepository;
        this.requestRepository = requestRepository;
        this.mechanicRepository = mechanicRepository;
        this.workOrderRepository = workOrderRepository;
    }


    @GetMapping("/operator")
    public String createReportOperators(LocalDate startDate, LocalDate endDate, Model model) {

        // Проверка на пустые значения
        if (startDate == null || endDate == null) {
            model.addAttribute("errorMessage", "Выберите даты для формирования отчета");
            return "report-request";
        }

        List<Operator> operators = (List<Operator>) operatorRepository.findAll();
        List<Request> requests = (List<Request>) requestRepository.findRequestsByDate(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        long totalRequestsCount = requests.size();

        if (totalRequestsCount == 0) {
            model.addAttribute("errorMessage", "За выбранный период не найдено ни одной заявки");
            return "report-request";
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

        Map<String, Long> sortedOperatorRequestCounts = createReport(operators, requests);

        model.addAttribute("operatorRequestCounts", sortedOperatorRequestCounts);
        model.addAttribute("totalRequestsCount", totalRequestsCount);
        model.addAttribute("totalOpen", totalOpen);
        model.addAttribute("totalInProgress", totalInProgress);
        model.addAttribute("totalCompleted", totalCompleted);
        model.addAttribute("totalRejected", totalRejected);
        setDateRangeAttributes(model, startDate, endDate);

        return "report-request";
    }


    @GetMapping("/mechanic")
    public String createReportMechanic(LocalDate startDate, LocalDate endDate, Model model) {

        // Проверка на пустые значения
        if (startDate == null || endDate == null) {
            model.addAttribute("errorMessage", "Выберите даты для формирования отчета");
            return "report-work-order";
        }

        List<Mechanic> mechanics = (List<Mechanic>) mechanicRepository.findAll();
        List<WorkOrder> workOrders = (List<WorkOrder>) workOrderRepository.findWorkOrdersByDate(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        long totalWorkOrdersCount = workOrders.size();

        if (totalWorkOrdersCount == 0) {
            return "report-work-order";
        }

        long totalInProgress = 0; // В процессе
        long totalCompleted = 0; // Завершен
        long totalCanceled = 0; // Отменен

        for (WorkOrder workOrder : workOrders) {
            switch (workOrder.getWorkOrderStatuses()) {
                case IN_PROGRESS -> totalInProgress++;
                case COMPLETED -> totalCompleted++;
                case CANCELED -> totalCanceled++;
            }
        }

        long totalOpen = requestRepository.countRequestsByDateAndStatus(
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), RequestStatus.OPEN);

        // Сортируем Map по убыванию значений и сохраняем в LinkedHashMap
        Map<String, Long> sortedMechanicWorkOrderCounts = createReport(mechanics, workOrders);

        model.addAttribute("mechanicWorkOrderCounts", sortedMechanicWorkOrderCounts);
        model.addAttribute("totalOpen", totalOpen);
        model.addAttribute("totalWorkOrdersCount", totalWorkOrdersCount);
        model.addAttribute("totalInProgress", totalInProgress);
        model.addAttribute("totalCompleted", totalCompleted);
        model.addAttribute("totalCanceled", totalCanceled);
        setDateRangeAttributes(model, startDate, endDate);

        return "report-work-order";
    }


    private void setDateRangeAttributes(Model model, LocalDate startDate, LocalDate endDate) {

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        model.addAttribute("formattedDate", formattedStartDate + " - " + formattedEndDate);
    }


    private <T extends Employee, U extends Task> LinkedHashMap<String, Long> createReport(
            List<T> employees, List<U> tasks) {

        Map<String, Long> employeeWorkCounts = new LinkedHashMap<>();

        for (T employee : employees) {
            long count = tasks.stream()
                    .filter(task -> {
                        if (task instanceof WorkOrder && employee instanceof Mechanic) {
                            return ((WorkOrder) task).getMechanic().getId().equals(((Mechanic) employee).getId());
                        } else if (task instanceof Request && employee instanceof Operator) {
                            return ((Request) task).getOperator().equals(employee);
                        }
                        return false;
                    })
                    .count();

            String fullName = employee.getSurname() + " " + employee.getName();
            if (employee.getPatronymic() != null && !employee.getPatronymic().isEmpty()) {
                fullName += " " + employee.getPatronymic();
            }

            String key = fullName;
            if (employee.getEmploymentStatus().equals(EmployeeStatus.DISMISSED)) {
                key += " (У)";
            }
            key += "\n" + employee.getPhoneNumber();

            employeeWorkCounts.put(key, count);
        }

        // Сортирую Map по убыванию значений и сохраняем в LinkedHashMap
        return employeeWorkCounts.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Сортировка по убыванию
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing, // В случае дубликатов оставляю первое значение
                        LinkedHashMap::new // Использую LinkedHashMap для сохранения порядка
                ));
    }
}
