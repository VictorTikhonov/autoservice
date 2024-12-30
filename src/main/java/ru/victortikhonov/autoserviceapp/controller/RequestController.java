package ru.victortikhonov.autoserviceapp.controller;

import jakarta.persistence.EntityNotFoundException;
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

    @GetMapping
    public String registerForm(Model model) {
        model.addAttribute("requestForm", new RequestForm());
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
        } else {
            // Если клиент найден, но почта в базе отсутствует, и она указана в форме
            if ((client.getEmail() == null || client.getEmail().isEmpty())
                    && requestForm.getClient().getEmail() != null) {
                client.setEmail(requestForm.getClient().getEmail());
                clientRepository.save(client);
            }
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
