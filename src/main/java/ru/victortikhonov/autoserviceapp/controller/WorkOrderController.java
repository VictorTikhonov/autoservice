package ru.victortikhonov.autoserviceapp.controller;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.victortikhonov.autoserviceapp.model.Personnel.Mechanic;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrder;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderAutoGood;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderService;
import ru.victortikhonov.autoserviceapp.model.WorkOrders.WorkOrderStatus;
import ru.victortikhonov.autoserviceapp.repository.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/work-order")
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final RequestRepository requestRepository;
    private final MechanicRepository mechanicRepository;

    private final AutoGoodRepository autoGoodRepository;
    private final ServiceRepository serviceRepository;

    public WorkOrderController(WorkOrderRepository workOrderRepository, RequestRepository requestRepository,
                               MechanicRepository mechanicRepository, AutoGoodRepository autoGoodRepository,
                               ServiceRepository serviceRepository) {
        this.workOrderRepository = workOrderRepository;
        this.requestRepository = requestRepository;
        this.mechanicRepository = mechanicRepository;
        this.autoGoodRepository = autoGoodRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    @Transactional
    public String meth() {

        Request request = requestRepository.findById(6L).orElse(null);
        Mechanic mechanic = mechanicRepository.findById(16L).orElse(null);


        // Пример создания и добавления autoGoods в workOrder
        WorkOrder workOrder = new WorkOrder(request, mechanic, WorkOrderStatus.IN_PROGRESS);
        workOrderRepository.save(workOrder);


        AutoGood ag = autoGoodRepository.findById(6L).orElse(null);
        WorkOrderAutoGood autoGood = new WorkOrderAutoGood(workOrder, ag, 5, new BigDecimal("100.50"));



        Service s = serviceRepository.findById(53L).orElse(null);
        WorkOrderService service = new WorkOrderService(workOrder, s, new BigDecimal("200.00"));


        workOrder.addService(service);
        workOrder.addAutoGood(autoGood);


        // Теперь сохраняем workOrder (связанно каскадно сохранятся и autoGoods, и services)
        workOrderRepository.save(workOrder);

        System.out.println("\n\nСохраннено\n\n");

        return "work-order";
    }
}
