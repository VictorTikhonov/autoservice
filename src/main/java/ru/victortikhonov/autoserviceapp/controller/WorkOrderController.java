package ru.victortikhonov.autoserviceapp.controller;


import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.*;
import ru.victortikhonov.autoserviceapp.repository.MechanicRepository;
import ru.victortikhonov.autoserviceapp.service.WorkOrderItemService;

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
    public String checkWorkOrder(@RequestParam Long workOrderId, Model model) {

        // Получаю заказ-наряд по ID
        Optional<WorkOrder> workOrderOptional = workOrderItemService.findWorkOrder(workOrderId);

        // Если заказ-наряд найден
        if (workOrderOptional.isPresent()) {
            WorkOrder workOrder = workOrderOptional.get();

            // Получаю связанные данные
            List<WorkOrderAutoGood> autoGoods = workOrder.getAutoGoods();
            List<WorkOrderService> services = workOrder.getServices();

            // Получаю все доступные автотовары и услуги
            Iterable<AutoGood> availableAutoGoods = workOrderItemService.getAllAutoGoods();
            Iterable<Service> availableServices = workOrderItemService.getAllServices();

            // Добавляю данные в модель
            model.addAttribute("workOrder", workOrder);
            model.addAttribute("autoGoods", autoGoods);
            model.addAttribute("services", services);
            model.addAttribute("availableAutoGoods", availableAutoGoods);
            model.addAttribute("availableServices", availableServices);

            return "work-order-details";
        }

        // Если заказ не найден, возвращаю страницу ошибки
        model.addAttribute("errorMessage", "Заказ-наряд с таким ID не найден");
        return "error-page";
    }


    @PostMapping("/add-items")
    @ResponseBody
    @Transactional
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
    public String completeWorkOrder(Long workOrderId, Model model) {

        Optional<WorkOrder> workOrderOptional = workOrderItemService.findWorkOrder(workOrderId);

        if (workOrderOptional.isPresent()) {
            WorkOrder workOrder = workOrderOptional.get();

            workOrder.setWorkOrderStatuses(WorkOrderStatus.COMPLETED);

            workOrderItemService.saveWorkOrder(workOrder);

            return "redirect:/work-order/list";
        }

        model.addAttribute("errorMessage", "Заказ-наряд с таким ID не найден");
        return "error-page";
    }
}