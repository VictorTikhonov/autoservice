package ru.victortikhonov.autoserviceapp.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.*;
import ru.victortikhonov.autoserviceapp.repository.MechanicRepository;
import ru.victortikhonov.autoserviceapp.service.WorkOrderItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/work-order/details")
public class WorkOrderController {
    private final WorkOrderItemService workOrderItemService;

    // TODO временное решение
    private final MechanicRepository mechanicRepository;
    private final Mechanic mechanic;

    public WorkOrderController(MechanicRepository mechanicRepository, WorkOrderItemService workOrderItemService) {

        this.workOrderItemService = workOrderItemService;

        this.mechanicRepository = mechanicRepository;
        mechanic = mechanicRepository.findById(16L).orElse(null);
    }


    @GetMapping
    public String checkWorkOrder(@RequestParam Long workOrderId,
                                 @RequestParam(required = false, defaultValue = "false") boolean viewMode,
                                 Model model) {

        // Получаю заказ-наряд по ID
        Optional<WorkOrder> workOrderOptional = workOrderItemService.findWorkOrder(workOrderId);

        // Если заказ-наряд найден
        if (workOrderOptional.isPresent()) {
            WorkOrder workOrder = workOrderOptional.get();

            // Получаю связанные данные
            List<WorkOrderAutoGood> autoGoods = workOrder.getAutoGoods();
            List<WorkOrderService> services = workOrder.getServices();

            // Добавляю данные в модель
            model.addAttribute("workOrder", workOrder);
            model.addAttribute("autoGoods", autoGoods);
            model.addAttribute("services", services);
            model.addAttribute("viewMode", viewMode);

            if (workOrder.getWorkOrderStatuses().equals(WorkOrderStatus.IN_PROGRESS)) {
                // Получаю все доступные автотовары и услуги
                Iterable<AutoGood> availableAutoGoods = workOrderItemService.getAllAutoGoods();
                Iterable<Service> availableServices = workOrderItemService.getAllServices();

                model.addAttribute("availableAutoGoods", availableAutoGoods);
                model.addAttribute("availableServices", availableServices);
            }

            return "work-order-details";
        }

        // Если заказ не найден, возвращаю страницу ошибки
        model.addAttribute("errorMessage", "Заказ-наряд с таким ID не найден");
        return "error-page";
    }


    @PostMapping("/add-items")
    @ResponseBody
    public ResponseEntity<?> addItemsWorkOrder(@RequestBody WorkOrderItemsDTO workOrderItems) {

        Optional<WorkOrder> workOrderOptional = workOrderItemService.findWorkOrder(workOrderItems.getWorkOrderId());

        // Если заказ-наряд найден
        if (workOrderOptional.isPresent()) {
            WorkOrder workOrder = workOrderOptional.get();

            if (!workOrderItems.getAutoGoodQuantity().isEmpty()) {
                workOrderItemService.addAutoGoods(workOrderItems.getAutoGoodQuantity(), workOrder);
            }
            if (!workOrderItems.getServicePrice().isEmpty()) {
                workOrderItemService.addServices(workOrderItems.getServicePrice(), workOrder);
            }

            // Возвращаю успешный ответ
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Заказ с ID " + workOrderItems.getWorkOrderId() + " не найден");
    }


    @GetMapping("/complete")
    public String completeWorkOrder(Long workOrderId, Model model, RedirectAttributes redirectAttributes) {

        Optional<WorkOrder> workOrderOptional = workOrderItemService.findWorkOrder(workOrderId);

        if (workOrderOptional.isPresent()) {

            WorkOrder workOrder = workOrderOptional.get();

            workOrder.setEndDate(LocalDateTime.now());

            if (workOrder.getRequest().getRequestStatus().equals(RequestStatus.REJECTED)) {
                workOrder.setWorkOrderStatuses(WorkOrderStatus.CANCELED);

                redirectAttributes.addFlashAttribute("success",
                        "Заказ-наряд №" + workOrder.getId() + " закрыт со статусом \"" +
                        workOrder.getWorkOrderStatuses().getDescription() + "\"");
            } else {
                workOrder.setWorkOrderStatuses(WorkOrderStatus.COMPLETED);

                redirectAttributes.addFlashAttribute("success",
                        "Заказ-наряд №" + workOrder.getId() + " успешно завершен!");
            }

            workOrderItemService.saveWorkOrder(workOrder);

            return "redirect:/work-order/list";
        }

        model.addAttribute("errorMessage", "Заказ-наряд с таким ID не найден");
        return "error-page";
    }


    @PostMapping("/delete-auto-good")
    public ResponseEntity<?> deleteAutoGood(@RequestParam Long workOrderId, @RequestParam Long autoGoodId) {

        int code = workOrderItemService.removeAutoGoodFromWorkOrder(workOrderId, autoGoodId);

        if (code == 0) {
            return ResponseEntity.ok().build(); // Успешное удаление
        } else {
            // Обработка ошибок с помощью вспомогательного метода
            return handleError(code, "Автотовар не найден в указанном заказ-наряде.");
        }
    }


    @PostMapping("/delete-service")
    public ResponseEntity<?> deleteService(@RequestParam Long workOrderId, @RequestParam Long serviceId) {

        int code = workOrderItemService.removeServiceFromWorkOrder(workOrderId, serviceId);

        if (code == 0) {
            return ResponseEntity.ok().build(); // Успешное удаление
        } else {
            return handleError(code, "Услуга не найдена в указанном заказ-наряде.");
        }
    }


    private ResponseEntity<?> handleError(int code, String errorMessage) {
        if (code == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        } else if (code == -2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Заказ-наряд с указанным ID не найден.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла непредвиденная ошибка.");
        }
    }

}