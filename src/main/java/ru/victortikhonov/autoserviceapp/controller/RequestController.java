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
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Car;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;
import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.repository.CarRepository;
import ru.victortikhonov.autoserviceapp.repository.ClientRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;

@Controller
@RequestMapping("/request")
@Slf4j
public class RequestController {


    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final RequestRepository requestRepository;

    public RequestController(CarRepository carRepository, ClientRepository clientRepository, RequestRepository requestRepository) {
        this.carRepository = carRepository;
        this.clientRepository = clientRepository;
        this.requestRepository = requestRepository;
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
            model.addAttribute("requestForm", requestForm);
            return "request-form";
        }

        save(requestForm);

        model.addAttribute("requestForm", new RequestForm());
        return "request-form";
    }


    // TODO сделать, чтобы не было повторного сохранения авто
    @Transactional
    protected void save(RequestForm requestForm)
    {
        carRepository.save(requestForm.getCar());

        // Проверка существования клиента
        if (clientRepository.findByPhoneNumber(requestForm.getClient().getPhoneNumber()) == null) {
            clientRepository.save(requestForm.getClient());
        }

        Request request = new Request(
                requestForm.getClient(),
                requestForm.getCar(),
                employee,
                RequestStatus.OPEN,
                requestForm.getComplaint()
        );
        requestRepository.save(request);

        log.info("Сохранение" +
                "\nАвто: " + requestForm.getCar()  +
                "\nКлиент: " + requestForm.getCar() +
                "\nЗаявка: " + request);

        System.out.println("Сохранение" +
                "\nАвто: " + requestForm.getCar()  +
                "\nКлиент: " + requestForm.getCar() +
                "\nЗаявка: " + request);
    }
}
