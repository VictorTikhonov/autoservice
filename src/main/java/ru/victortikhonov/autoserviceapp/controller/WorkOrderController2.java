package ru.victortikhonov.autoserviceapp.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.AutoGoodQuantity;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderAutoGood;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderService;
import ru.victortikhonov.autoserviceapp.repository.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/work-order/details")
@SessionAttributes("workOrder")
public class WorkOrderController2 {

    private final WorkOrderRepository workOrderRepository;
    private final RequestRepository requestRepository;
    private final AutoGoodRepository autoGoodRepository;
    private final ServiceRepository serviceRepository;

    // TODO временное решение
    private final MechanicRepository mechanicRepository;
    private final Mechanic mechanic;

    public WorkOrderController2(WorkOrderRepository workOrderRepository, RequestRepository requestRepository,
                                MechanicRepository mechanicRepository, AutoGoodRepository autoGoodRepository,
                                ServiceRepository serviceRepository) {
        this.workOrderRepository = workOrderRepository;
        this.requestRepository = requestRepository;
        this.autoGoodRepository = autoGoodRepository;
        this.serviceRepository = serviceRepository;

        this.mechanicRepository = mechanicRepository;
        mechanic = mechanicRepository.findById(16L).orElse(null);
    }

    @GetMapping
    public String checkWorkOrder(@RequestParam Long workOrderId, Model model) {
        // Получаем заказ-наряд по ID
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ-наряд не найден с ID: " + workOrderId));

        // Получаем связанные данные
        List<WorkOrderAutoGood> autoGoods = workOrder.getAutoGoods();
        List<WorkOrderService> services = workOrder.getServices();


        // Получаем все доступные автотовары и услуги
        Iterable<AutoGood> availableAutoGoods = autoGoodRepository.findAll();
        Iterable<Service> availableServices = serviceRepository.findAll();

        // Добавляем данные в модель
        model.addAttribute("workOrder", workOrder);
        model.addAttribute("autoGoods", autoGoods);
        model.addAttribute("services", services);
        model.addAttribute("availableAutoGoods", availableAutoGoods);
        model.addAttribute("availableServices", availableServices);

        return "work-order-details";
    }



    @PostMapping("/add-auto-goods")
    @ResponseStatus(HttpStatus.OK)
    public void addAutoGoods(@RequestBody List<AutoGoodQuantity> autoGoods,
                             @ModelAttribute("workOrder") WorkOrder workOrder) {

        for (AutoGoodQuantity autoGoodQuantity : autoGoods) {
            AutoGood autoGood = autoGoodRepository.findById(autoGoodQuantity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Товар с ID " + autoGoodQuantity.getId() + " не найден"));

            WorkOrderAutoGood workOrderAutoGood = new WorkOrderAutoGood(workOrder, autoGood,
                    autoGoodQuantity.getQuantity(), autoGood.getPriceOneUnit());

            autoGood.minusQuantity(autoGoodQuantity.getQuantity());

            workOrder.addAutoGood(workOrderAutoGood);
        }
        workOrderRepository.save(workOrder);
    }
}
