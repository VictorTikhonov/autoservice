package ru.victortikhonov.autoserviceapp.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.NumberGenerator;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.AutoGood;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.AutoGoodCategory;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.Service;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.ServiceCategory;
import ru.victortikhonov.autoserviceapp.model.WorkOrder.*;
import ru.victortikhonov.autoserviceapp.repository.AutoGoodCategoryRepository;
import ru.victortikhonov.autoserviceapp.repository.ServiceCategoryRepository;
import ru.victortikhonov.autoserviceapp.service.WorkOrderItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/work-order/details")
public class WorkOrderController {
    private final WorkOrderItemService workOrderItemService;
    private final AutoGoodCategoryRepository autoGoodCategoryRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;


    public WorkOrderController(WorkOrderItemService workOrderItemService,
                               AutoGoodCategoryRepository autoGoodCategoryRepository,
                               ServiceCategoryRepository serviceCategoryRepository) {

        this.workOrderItemService = workOrderItemService;
        this.autoGoodCategoryRepository = autoGoodCategoryRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
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

                Iterable<AutoGoodCategory> autoGoodCategories = autoGoodCategoryRepository.findAll();
                Iterable<ServiceCategory> serviceCategories = serviceCategoryRepository.findAll();

                model.addAttribute("availableAutoGoods", availableAutoGoods);
                model.addAttribute("availableServices", availableServices);

                model.addAttribute("autoGoodCategories", autoGoodCategories);
                model.addAttribute("serviceCategories", serviceCategories);
            }

            return "work-order-details";
        }

        // Если заказ не найден, возвращаю страницу ошибки
        model.addAttribute("errorMessage", "Заказ-наряд не найден");
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

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказ-наряд не найден");
    }


    @GetMapping("/complete")
    public String completeWorkOrder(Long workOrderId, Model model, RedirectAttributes redirectAttributes) {

        Optional<WorkOrder> workOrderOptional = workOrderItemService.findWorkOrder(workOrderId);

        if (workOrderOptional.isPresent()) {

            WorkOrder workOrder = workOrderOptional.get();

            System.out.println(workOrder.getWorkOrderStatuses().equals(WorkOrderStatus.CANCELED) + "\n\n" +
                    workOrder.getRequest().getRequestStatus().equals(RequestStatus.REJECTED) );

            if ((!workOrder.getWorkOrderStatuses().equals(WorkOrderStatus.CANCELED)
                    && !workOrder.getRequest().getRequestStatus().equals(RequestStatus.REJECTED)) &&
                    workOrder.getServices().isEmpty()) {
                redirectAttributes.addFlashAttribute("missingServiceError",
                        "Необходимо выбрать хотя бы одну услугу");

                return "redirect:/work-order/details?workOrderId=" + workOrderId + "#missingServiceError";
            }


            workOrder.setEndDate(LocalDateTime.now());
            if (workOrder.getRequest().getRequestStatus().equals(RequestStatus.REJECTED)) {
                workOrder.setWorkOrderStatuses(WorkOrderStatus.CANCELED);

                redirectAttributes.addFlashAttribute("success",
                        "Заказ-наряд №" + NumberGenerator.toRussian(workOrder.getWorkOrderNumber()) + " закрыт со статусом \"" +
                                workOrder.getWorkOrderStatuses().getDescription() + "\"");
            } else {
                workOrder.setWorkOrderStatuses(WorkOrderStatus.COMPLETED);

                redirectAttributes.addFlashAttribute("success",
                        "Заказ-наряд №" + NumberGenerator.toRussian(workOrder.getWorkOrderNumber()) + " завершен!");
            }

            workOrderItemService.saveWorkOrder(workOrder);

            return "redirect:/work-order/list?status=ALL";
        }

        model.addAttribute("errorMessage", "Заказ-наряд не найден");

        return "error-page";
    }


    @PostMapping("/delete-auto-good")
    public ResponseEntity<?> deleteAutoGood(@RequestParam Long workOrderId, @RequestParam Long autoGoodId) {

        int code = workOrderItemService.removeAutoGoodFromWorkOrder(workOrderId, autoGoodId);

        if (code == 0) {
            return ResponseEntity.ok().build(); // Успешное удаление
        } else {
            // Обработка ошибок с помощью вспомогательного метода
            return handleError(code, "Автотовар не найден в указанном заказ-наряде");
        }
    }


    @PostMapping("/delete-service")
    public ResponseEntity<?> deleteService(@RequestParam Long workOrderId, @RequestParam Long serviceId) {

        int code = workOrderItemService.removeServiceFromWorkOrder(workOrderId, serviceId);

        if (code == 0) {
            return ResponseEntity.ok().build(); // Успешное удаление
        } else {
            return handleError(code, "Услуга не найдена в указанном заказ-наряде");
        }
    }


    private ResponseEntity<?> handleError(int code, String errorMessage) {
        if (code == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        } else if (code == -2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Заказ-наряд не найден");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла непредвиденная ошибка");
        }
    }
}