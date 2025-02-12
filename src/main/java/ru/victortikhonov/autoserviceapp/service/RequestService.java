package ru.victortikhonov.autoserviceapp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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


    public Optional<Client> findClientByPhoneNumber(String phoneNumber) {

        return clientRepository.findByPhoneNumber(phoneNumber);
    }


    public Long createRequest(RequestForm requestForm) {

        Car car = findOrCreateCar(requestForm.getCar());
        Client client = findOrCreateClient(requestForm.getClient());

        if (requestForm.getClient().getEmail() != null && !requestForm.getClient().getEmail().isBlank()) {
            String newEmail = requestForm.getClient().getEmail();

            if (client.getEmail() == null || !client.getEmail().equals(newEmail)) {
                client.setEmail(newEmail);
                clientRepository.save(client);
            }
        }

        // TODO временное решение
        Operator operator = operatorRepository.findById(10L)
                .orElseThrow(() -> new EntityNotFoundException("Оператор не найден (10)"));

        Request request = new Request(
                client,
                car,
                operator,
                RequestStatus.OPEN,
                requestForm.getComplaint());

        requestRepository.save(request);

        return request.getId();
    }


    private Car findOrCreateCar(Car carRequest) {

        List<Car> cars = carRepository.findByVin(carRequest.getVin());

        for (Car car : cars) {
            if (carRequest.getStateNumber().equals(car.getStateNumber())) {
                return car;
            }
        }

        return carRepository.save(carRequest);
    }


    private Client findOrCreateClient(Client clientRequest) {

        if (clientRequest == null) {
            throw new IllegalArgumentException("Клиент не может быть null");
        }

        if (clientRequest.getPhoneNumber() == null || clientRequest.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Номер телефона клиента обязателен");
        }

        return clientRepository.findByPhoneNumber(clientRequest.getPhoneNumber())
                .orElseGet(() -> clientRepository.save(clientRequest));
    }


    public Page<Request> findRequests(RequestStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        if (startDate == null || endDate == null || status == null) {
            throw new IllegalArgumentException("Все параметры должны быть переданы");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }

        return requestRepository.findRequestsByStatusAndDate(status, startDate, endDate, pageable);
    }


    public Optional<Request> findRequestById(Long id) {

        return requestRepository.findById(id);
    }


//    public Request saveRequest(Request request) {
//
//        if (request == null) {
//            throw new IllegalArgumentException("Заявка не может быть null");
//        }
//
//        if (request.getClient() == null || request.getCar() == null) {
//            throw new IllegalArgumentException("Заявка должна содержать клиента и машину");
//        }
//
//        return requestRepository.save(request);
//    }

    public boolean cancelRequest(Request request) {

        if (request.getRequestStatus().equals(RequestStatus.OPEN) || request.getRequestStatus().equals(RequestStatus.IN_PROGRESS)) {
            request.setRequestStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            return true;
        }

        return false;
    }
}