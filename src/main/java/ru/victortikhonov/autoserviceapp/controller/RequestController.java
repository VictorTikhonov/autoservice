package ru.victortikhonov.autoserviceapp.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.*;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.repository.CarRepository;
import ru.victortikhonov.autoserviceapp.repository.ClientRepository;

@Controller
@RequestMapping("/request")
@Slf4j
public class RequestController {


    private final CarRepository carRepository;
    private final ClientRepository clientRepository;

    public RequestController(CarRepository carRepository, ClientRepository clientRepository) {
        this.carRepository = carRepository;
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public String registerForm(Model model)
    {
        model.addAttribute("requestForm", new RequestForm());
        return "request-form";
    }

    @PostMapping("/create-request")
    public String createRequest(@Valid RequestForm requestForm, Errors errors, Model model)
    {
        if(errors.hasErrors())
        {
            model.addAttribute("requestForm", new RequestForm());
            return "request-form";
        }

        save(requestForm);

        log.info("Сохранение" +
                "\nАвто: " + requestForm.getCar()  +
                "\nКлиент: " + requestForm.getCar());

        return "request-form";
    }

    @Transactional
    protected void save(RequestForm requestForm)
    {
        carRepository.save(requestForm.getCar());
        clientRepository.save(requestForm.getClient());
    }
}
