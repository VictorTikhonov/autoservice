package ru.victortikhonov.autoserviceapp.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.ServiceCategory;
import ru.victortikhonov.autoserviceapp.repository.ServiceCategoryRepository;
import ru.victortikhonov.autoserviceapp.repository.ServiceRepository;

import java.util.ArrayList;
import java.util.Optional;


@RequestMapping("/service")
@SessionAttributes({"categories", "service"})
@Controller
public class ServiceController {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceRepository serviceRepository;


    public ServiceController(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository) {

        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {

        Iterable<ServiceCategory> categories = serviceCategoryRepository.findAll();

        model.addAttribute("service", new Service());
        model.addAttribute("categories", categories);

        if (!categories.iterator().hasNext()) {
            model.addAttribute("errorService", "Для добавления услуги необходимо указать категорию." +
                    " Пожалуйста, добавьте категорию, чтобы продолжить");
        }

        return "add-service-and-category";
    }


    @PostMapping("/create")
    @Transactional
    public String handleCreate(
            @RequestParam("action") String action,
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @ModelAttribute("categories") ArrayList<ServiceCategory> categories,
            @Valid @ModelAttribute("service") Service service, Errors errors,
            HttpSession session, Model model) {

        model.addAttribute("action", action);

        if ("addCategory".equals(action)) {
            return addCategory(categoryName, model, categories);
        } else if ("addService".equals(action)) {
            if (errors.hasErrors()) {
                return "add-service-and-category";
            }

            return addService(service, model, session);
        }

        model.addAttribute("error", "Некорректное действие");

        return "add-service-and-category";
    }


    private String addService(Service service, Model model, HttpSession session) {

        service.setName(service.getName().trim());
        if (!service.getName().isEmpty()) {
            service.setName(service.getName().substring(0, 1).toUpperCase()
                    + service.getName().substring(1).toLowerCase());
        }

        if (serviceRepository.findByNameAndCategory_Id(service.getName(),
                service.getCategory().getId()).isPresent()) {
            model.addAttribute("errorService",
                    "Услуга \"" + service.getName() + "\" с таким названием уже существует в данной категории");

            return "add-service-and-category";
        }

        serviceRepository.save(service);
        model.addAttribute("success", "Услуга \"" + service.getName() + "\" успешно добавлена" +
                " в категорию \"" + service.getCategory().getName() + "\"");

        session.removeAttribute("service");
        model.addAttribute("service", new Service());

        return "add-service-and-category";
    }


    private String addCategory(String categoryName, Model model, ArrayList<ServiceCategory> categories) {

        categoryName = categoryName.trim();
        if (!categoryName.isEmpty()) {
            categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1).toLowerCase();
        }

        if (serviceCategoryRepository.findByName(categoryName).isPresent()) {
            model.addAttribute("errorCategory", "Категория \"" + categoryName + "\" уже существует");
            model.addAttribute("categoryName", categoryName);
            return "add-service-and-category";
        }

        ServiceCategory category = new ServiceCategory(categoryName);
        serviceCategoryRepository.save(category);
        categories.add(category);
        model.addAttribute("success", "Категория \"" + categoryName + "\" успешно добавлена");

        return "add-service-and-category";
    }


    @GetMapping("/list")
    public String showTableServices(Model model, @RequestParam(required = false) Long categoryId,
                                    SessionStatus sessionStatus) {

        sessionStatus.setComplete();

        Iterable<Service> services;
        Iterable<ServiceCategory> categories = serviceCategoryRepository.findAll();

        if (categoryId != null) {
            services = serviceRepository.findByCategoryIdAndRelevanceTrue(categoryId);
        } else {
            services = serviceRepository.findByRelevanceTrue();
        }

        model.addAttribute("services", services);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);

        return "table-services";
    }


    @PostMapping("/delete")
    public String deleteService(@RequestParam("id") Long serviceId, RedirectAttributes redirectAttributes) {

        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);

        if (serviceOptional.isPresent()) {
            Service service = serviceOptional.get();

            service.setRelevance(false);
            serviceRepository.save(service);

            redirectAttributes.addFlashAttribute("success", "Услуга \"" + service.getName() + "\" удалена");
        } else {
            redirectAttributes.addFlashAttribute("error", "Услуга не найдена");
        }

        return "redirect:/service/list";
    }
}
