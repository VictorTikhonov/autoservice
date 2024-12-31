package ru.victortikhonov.autoserviceapp.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Car;
import ru.victortikhonov.autoserviceapp.model.ClientsAndCars.Client;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.repository.CarRepository;
import ru.victortikhonov.autoserviceapp.repository.ClientRepository;
import ru.victortikhonov.autoserviceapp.repository.OperatorRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;

import java.util.List;

@Controller
@RequestMapping("/request")
@Slf4j
public class RequestController {


    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final RequestRepository requestRepository;
    private final OperatorRepository operatorRepository;     // TODO временное решение

    public RequestController(CarRepository carRepository, ClientRepository clientRepository,
                             RequestRepository requestRepository, OperatorRepository operatorRepository) {
        this.carRepository = carRepository;
        this.clientRepository = clientRepository;
        this.requestRepository = requestRepository;
        this.operatorRepository = operatorRepository;
    }


    @GetMapping("/create")
    public String registerForm(Model model) {
        model.addAttribute("requestForm", new RequestForm());
        return "request-form";
    }

    @PostMapping("/create/search-client")
    public String searchClient(RequestForm requestForm, Model model, Errors errors) {

        String phoneNumber = requestForm.getClient().getPhoneNumber();

        // Валидация номера телефона
        if (!phoneNumber.matches("^[0-9]{11}$")) {
            errors.rejectValue("client.phoneNumber", "invalid.phoneNumber",
                    "Номер телефона должен состоять из 11 цифр");
            return "request-form";
        }

        Client client = clientRepository.findByPhoneNumber(phoneNumber);

        if (client != null) {
            requestForm.setClient(client);
            model.addAttribute("requestForm", requestForm);
        } else {
            // Если клиент не найден, добавим уведомление
            model.addAttribute("clientNotFound", true); // Добавляем флаг для уведомления
        }

        return "request-form";
    }


    @PostMapping("/create")
    public String createRequest(@Valid RequestForm requestForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("requestForm", requestForm);
            return "request-form";
        }

        save(requestForm);

        model.addAttribute("requestForm", new RequestForm());
        return "request-form";
    }



    @Transactional
    protected void save(RequestForm requestForm) {

        Car car = null;
        List<Car> listCars = carRepository.findByVin(requestForm.getCar().getVin());

        if (!listCars.isEmpty()) {
            for (Car c : listCars) {
                if (requestForm.getCar().getStateNumber().equals(c.getStateNumber())) {
                    car = c;
                    break;
                }
            }
        }

        // Если авто не найдено, сохранем
        if (car == null) {
            carRepository.save(requestForm.getCar());
            car = requestForm.getCar();
        }

        // Проверка существования клиента
        Client client = clientRepository.findByPhoneNumber(requestForm.getClient().getPhoneNumber());

        // Если клиент не найден, сохраняем его
        if (client == null) {
            clientRepository.save(requestForm.getClient());
            client = requestForm.getClient();
        }


        Operator operator = operatorRepository.findById(2L)
                .orElseThrow(() -> new EntityNotFoundException("Оператор не найден (2)"));


        requestRepository.save(new Request(
                client,
                car,
                operator,
                RequestStatus.OPEN,
                requestForm.getComplaint()
        ));
    }
}
