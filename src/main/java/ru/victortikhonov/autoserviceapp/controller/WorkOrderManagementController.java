package ru.victortikhonov.autoserviceapp.controller;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderStatus;
import ru.victortikhonov.autoserviceapp.repository.MechanicRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;
import ru.victortikhonov.autoserviceapp.repository.WorkOrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/work-order")
public class WorkOrderManagementController {

    private final WorkOrderRepository workOrderRepository;
    private final RequestRepository requestRepository;

    // TODO временное решение
    private final MechanicRepository mechanicRepository;
    private final Mechanic mechanic;

    public WorkOrderManagementController(WorkOrderRepository workOrderRepository, RequestRepository requestRepository,
                                         MechanicRepository mechanicRepository) {

        this.workOrderRepository = workOrderRepository;
        this.requestRepository = requestRepository;

        this.mechanicRepository = mechanicRepository;
        mechanic = mechanicRepository.findById(16L).orElse(null);
    }


    @Transactional
    @PostMapping("/create")
    public String createWorkOrder(@RequestParam("requestId") Long requestId, Model model,
                                  RedirectAttributes redirectAttributes) {

        try {
            Request request = requestRepository.findById(requestId).orElseThrow(
                    () -> new IllegalArgumentException("неверное ID заявки: " + requestId));

            WorkOrder workOrder = new WorkOrder(request, this.mechanic, WorkOrderStatus.IN_PROGRESS);
            this.workOrderRepository.save(workOrder);

            request.setRequestStatus(RequestStatus.IN_PROGRESS);
            request.setWorkOrder(workOrder);
            requestRepository.save(request);

            redirectAttributes.addFlashAttribute("success",
                    "Заказ-наряд №" + workOrder.getId() + " успешно создан!");

            return "redirect:/work-order/list?newWorkOrderId=" + workOrder.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Не удалось начать работу по заявке: " + e.getMessage());

            return "error-page";
        }
    }


    @GetMapping("/list")
    public String showListWorkOrders(Model model,
                                     @RequestParam(required = false) Long newWorkOrderId,
                                     @RequestParam(required = false) WorkOrderStatus status,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {

        // Добавляю id нового ЗН,чтобы подсветить его
        if (newWorkOrderId != null) {
            model.addAttribute("newWorkOrderId", newWorkOrderId);
        }

        // Уставливаю по умолчанию фильтр дат на 7 дней
        if (startDate == null) {
            startDate = LocalDate.now().minusWeeks(1);
        }
        if (endDate == null || endDate.isAfter(LocalDate.now())) {
            endDate = LocalDate.now();
        }

        // Проверка на правильность дат
        if (startDate.isAfter(endDate)) {
            // Добавляю сообщение об ошибке в модель
            model.addAttribute("dateError", "Дата начала не может быть позже даты окончания.");
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "table-work-orders";
        }

        // Поиск ЗН в соответствии с фильтрами
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        Page<WorkOrder> workOrders = searchWorkOrders(status, pageable, startDate, endDate);

        // Проверка отмененных заказ-нарядов
        checkForCanceledWorkOrders(model);

        model.addAttribute("workOrders", workOrders);
        model.addAttribute("statuses", WorkOrderStatus.values());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("newWorkOrderId", newWorkOrderId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, workOrders.getTotalPages()));
        model.addAttribute("totalItems", workOrders.getTotalElements());

        return "table-work-orders";
    }


    private void checkForCanceledWorkOrders(Model model) {

        List<Long> canceledWorkOrdersInProgress = workOrderRepository.findRejectedRequestsWithInProgressWorkOrders(
                RequestStatus.REJECTED, WorkOrderStatus.IN_PROGRESS);

        if (!canceledWorkOrdersInProgress.isEmpty()) {
            // Преобразуем список Long в строку, разделенную запятыми
            String ids = canceledWorkOrdersInProgress.stream()
                    .map(String::valueOf)  // Преобразую Long в String
                    .collect(Collectors.joining(", "));  // Объединяем в строку с разделителем ", "

            String cancellationNotice = "Некоторые заказ-наряды были отменены. " +
                    "Пожалуйста, внесите информацию о выполненных работах, использованных автотоварах и завершите соответствующий(-ие) заказ-наряд(-ы)." +
                    "<br/>Номер(-а) заказ-нарядов: " + ids;

            model.addAttribute("cancellationNotice", cancellationNotice);
        }
    }


    private Page<WorkOrder> searchWorkOrders(WorkOrderStatus status, Pageable pageable,
                                             LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        if (status == WorkOrderStatus.ALL) {
            return workOrderRepository.
                    findByMechanicIdAndDateRange(mechanic.getId(), startDateTime, endDateTime, pageable);
        } else {
            // Статус по умолчанию
            if (status == null) {
                status = WorkOrderStatus.IN_PROGRESS;
            }

            return workOrderRepository.
                    findByMechanicIdAndStatusAndDate(this.mechanic.getId(), status, startDateTime, endDateTime, pageable);
        }
    }
}