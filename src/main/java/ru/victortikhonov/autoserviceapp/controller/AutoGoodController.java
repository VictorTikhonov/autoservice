package ru.victortikhonov.autoserviceapp.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGood;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGoodCategory;
import ru.victortikhonov.autoserviceapp.repository.AutoGoodCategoryRepository;
import ru.victortikhonov.autoserviceapp.repository.AutoGoodRepository;

import java.util.ArrayList;


@Controller
@RequestMapping("/auto-good")
@SessionAttributes({"categories", "autoGood"})
public class AutoGoodController {
    private final AutoGoodRepository autoGoodRepository;
    private final AutoGoodCategoryRepository autoGoodCategoryRepository;


    public AutoGoodController(AutoGoodRepository autoGoodRepository,
                              AutoGoodCategoryRepository autoGoodCategoryRepository) {

        this.autoGoodRepository = autoGoodRepository;
        this.autoGoodCategoryRepository = autoGoodCategoryRepository;
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {

        Iterable<AutoGoodCategory> categories = autoGoodCategoryRepository.findAll();

        model.addAttribute("autoGood", new AutoGood());
        model.addAttribute("categories", categories);

        if (!categories.iterator().hasNext()) {
            model.addAttribute("errorAutoGood", "Для добавления автотовара необходимо указать категорию." +
                    " Пожалуйста, добавьте категорию, чтобы продолжить");
        }

        return "add-auto-good-and-category";
    }


    @PostMapping("/create")
    @Transactional
    public String create(
            @RequestParam("action") String action,
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @ModelAttribute("categories") ArrayList<AutoGoodCategory> categories,
            @Valid @ModelAttribute("autoGood") AutoGood autoGood, Errors errors,
            HttpSession session, Model model) {

        model.addAttribute("action", action);

        if ("addCategory".equals(action)) {
            return addCategory(categoryName, model, categories);
        } else if ("addAutoGood".equals(action)) {
            if (errors.hasErrors()) {
                return "add-auto-good-and-category";
            }

            return addAutoGood(autoGood, model, session);
        }

        model.addAttribute("error", "Некорректное действие");

        return "add-auto-good-and-category";
    }


    private String addAutoGood(AutoGood autoGood, Model model, HttpSession session) {

        autoGood.setName(autoGood.getName().trim());
        if (!autoGood.getName().isEmpty()) {
            autoGood.setName(autoGood.getName().substring(0, 1).toUpperCase()
                    + autoGood.getName().substring(1).toLowerCase());
        }

        if (autoGoodRepository.findByNameAndCategory_Id(autoGood.getName(),
                autoGood.getCategory().getId()).isPresent()) {
            model.addAttribute("errorAutoGood",
                    "Автотовар с таким названием уже существует в данной категории");

            return "add-auto-good-and-category";
        }

        autoGoodRepository.save(autoGood);
        model.addAttribute("success", "Автотовар \"" + autoGood.getName() + "\" успешно добавлен" +
                " в категорию \"" + autoGood.getCategory().getName() + "\"");

        session.removeAttribute("autoGood");
        model.addAttribute("autoGood", new AutoGood());

        return "add-auto-good-and-category";
    }


    private String addCategory(String categoryName, Model model, ArrayList<AutoGoodCategory> categories) {

        categoryName = categoryName.trim();
        if (!categoryName.isEmpty()) {
            categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1).toLowerCase();
        }

        if (autoGoodCategoryRepository.findByName(categoryName).isPresent()) {
            model.addAttribute("errorCategory", "Категория с таким названием уже существует");
            model.addAttribute("categoryName", categoryName);
            return "add-auto-good-and-category";
        }

        AutoGoodCategory category = new AutoGoodCategory(categoryName);
        autoGoodCategoryRepository.save(category);
        categories.add(category);
        model.addAttribute("success", "Категория \"" + categoryName + "\" успешно добавлена");

        return "add-auto-good-and-category";
    }


    @GetMapping("/show-table")
    public String showTableAutoGoods(Model model, @RequestParam(required = false) Long categoryId,
                                     SessionStatus sessionStatus) {

        sessionStatus.setComplete();

        Iterable<AutoGood> autoGoods;
        Iterable<AutoGoodCategory> categories = autoGoodCategoryRepository.findAll();

        if (categoryId != null) {
            autoGoods = autoGoodRepository.findByCategoryId(categoryId);
        } else {
            autoGoods = autoGoodRepository.findAll();
        }

        model.addAttribute("autoGoods", autoGoods);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);

        return "table-auto-goods";
    }
}
