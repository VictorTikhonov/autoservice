package ru.victortikhonov.autoserviceapp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
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

@Service
public class RequestService {
    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final RequestRepository requestRepository;
    private final OperatorRepository operatorRepository;     // TODO временное решение

    public RequestService(CarRepository carRepository, ClientRepository clientRepository,
                          RequestRepository requestRepository, OperatorRepository operatorRepository) {
        this.carRepository = carRepository;
        this.clientRepository = clientRepository;
        this.requestRepository = requestRepository;
        this.operatorRepository = operatorRepository;
    }

    public Client findClientByPhoneNumber(String phoneNumber) {
        return clientRepository.findByPhoneNumber(phoneNumber);
    }


    @Transactional
    public void save(RequestForm requestForm) {

        Car car = findOrCreateCar(requestForm.getCar());

        Client client = findOrCreateClient(requestForm.getClient());

        if (requestForm.getClient().getEmail() != null && !requestForm.getClient().getEmail().isBlank()) {
            String newEmail = requestForm.getClient().getEmail();
            if (client.getEmail() == null || !client.getEmail().equals(newEmail)) {
                client.setEmail(newEmail);
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

    public Car findOrCreateCar(Car carRequest) {
        List<Car> cars = carRepository.findByVin(carRequest.getVin());
        for (Car car : cars) {
            if (carRequest.getStateNumber().equals(car.getStateNumber())) {
                return car;
            }
        }
        return carRepository.save(carRequest);
    }

    public Client findOrCreateClient(Client clientRequest) {
        Client client = clientRepository.findByPhoneNumber(clientRequest.getPhoneNumber());
        if (client == null) {
            return clientRepository.save(clientRequest);
        }
        return client;
    }
}