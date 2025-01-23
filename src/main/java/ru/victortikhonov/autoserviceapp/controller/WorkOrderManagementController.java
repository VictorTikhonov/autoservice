package ru.victortikhonov.autoserviceapp.controller;


import jakarta.transaction.Transactional;
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
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderStatus;
import ru.victortikhonov.autoserviceapp.repository.MechanicRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;
import ru.victortikhonov.autoserviceapp.repository.WorkOrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

            // TODO расскоментить!
            //request.setRequestStatus(RequestStatus.IN_PROGRESS);

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
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {


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

            return "work-order-list";
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Поиск ЗН в соответствии с фильтрами
        Iterable<WorkOrder> filteredWorkOrders;
        if (status == WorkOrderStatus.ALL) {
            filteredWorkOrders = workOrderRepository.
                    findByMechanicIdAndDateRange(mechanic.getId(), startDateTime, endDateTime);
        } else {
            // Статус по умолчанию
            if (status == null) {
                status = WorkOrderStatus.IN_PROGRESS;
            }

            filteredWorkOrders = workOrderRepository.
                    findByMechanicIdAndStatusAndDate(this.mechanic.getId(), status, startDateTime, endDateTime);
        }

        model.addAttribute("workOrders", filteredWorkOrders);
        model.addAttribute("statuses", WorkOrderStatus.values());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("newWorkOrderId", newWorkOrderId);

        return "work-order-list";
    }
}