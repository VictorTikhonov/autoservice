package ru.victortikhonov.autoserviceapp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.Service;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.ServiceCategory;
import ru.victortikhonov.autoserviceapp.repository.*;


@RequestMapping("/service")
@SessionAttributes("categories")
@Controller
public class ServiceController {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceRepository serviceRepository;



    public ServiceController(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository) {

        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
    }



    @GetMapping("/create")
    public String showCreateServiceForm(Model model) {

        Iterable<ServiceCategory> categories = serviceCategoryRepository.findAll();

        Service service = new Service();

        model.addAttribute("service", service);
        model.addAttribute("categories", categories);

        if (!categories.iterator().hasNext()) {
            model.addAttribute("error", "Для создания услуги необходимо указать категорию." +
                    " Пожалуйста, добавьте категорию, чтобы продолжить");
            return "add-service";
        }

        return "add-service";
    }



    @PostMapping("/create")
    public String createService(@Valid @ModelAttribute("service") Service service, Errors errors,
                                Model model, SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            return "add-service";
        }

        // Проверка наличия в бд услуги
        if (serviceRepository.findByNameAndCategory_Id(service.getName(), service.getCategory().getId()).isPresent()) {
            // Если результат найден, добавляем ошибку и возвращаем форму с ошибкой
            model.addAttribute("error", "Услуга с таким названием уже существует в данной категории");
            return "add-service";
        } else {
            // Если услуга не найдена, сохраняем новую услугу
            serviceRepository.save(service);

            // Добавляем атрибут success для перенаправления
            redirectAttributes.addFlashAttribute("success", "Услуга '"
                    + service.getName() + "' добавлена в категорию '" + service.getCategory().getName() + "'");
        }

        sessionStatus.setComplete();

        return "redirect:/service/create";
    }



    @GetMapping("/category/create")
    public String showCreateCategoryForm(Model model) {

        return "create-service-category";
    }



    @PostMapping("/category/create")
    public String createServiceCategory(@RequestParam("name") String name, Model model) {

        // Убираю пробелы в начале и конце строки, и делаем первую букву заглавной
        name = name.trim();
        if (!name.isEmpty()) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        // Проверка на существование категории с таким именем
        if (serviceCategoryRepository.findByName(name).isPresent()) {

            model.addAttribute("error", "Категория с таким именем уже существует.");
            model.addAttribute("name", name);
            return "create-service-category";  // Возвращаем на страницу добавления
        }


        serviceCategoryRepository.save(new ServiceCategory(name));
        model.addAttribute("success", name);
        return "create-service-category";
    }



    @GetMapping("/show-table")
    public String showTableServices(Model model, @RequestParam(required = false) Long categoryId) {

        Iterable<Service> services;
        Iterable<ServiceCategory> categories = serviceCategoryRepository.findAll();

        if (categoryId != null) {
            services = serviceRepository.findByCategoryId(categoryId);
        } else {
            services = serviceRepository.findAll();
        }

        model.addAttribute("services", services);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);

        return "table-services";
    }
}
