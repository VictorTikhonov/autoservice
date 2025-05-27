package ru.victortikhonov.autoserviceapp.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.victortikhonov.autoserviceapp.NumberGenerator;
import ru.victortikhonov.autoserviceapp.model.ClientAndCar.Car;
import ru.victortikhonov.autoserviceapp.model.ClientAndCar.Client;
import ru.victortikhonov.autoserviceapp.model.Personnel.Operator;
import ru.victortikhonov.autoserviceapp.model.Request.Request;
import ru.victortikhonov.autoserviceapp.model.Request.RequestStatus;
import ru.victortikhonov.autoserviceapp.model.RequestForm;
import ru.victortikhonov.autoserviceapp.repository.CarRepository;
import ru.victortikhonov.autoserviceapp.repository.ClientRepository;
import ru.victortikhonov.autoserviceapp.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RequestService {
    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final RequestRepository requestRepository;


    public RequestService(CarRepository carRepository, ClientRepository clientRepository,
                          RequestRepository requestRepository) {

        this.carRepository = carRepository;
        this.clientRepository = clientRepository;
        this.requestRepository = requestRepository;
    }


    public Optional<Client> findClientByPhoneNumber(String phoneNumber) {

        return clientRepository.findByPhoneNumber(phoneNumber);
    }


    public String createRequest(RequestForm requestForm, Operator operator) {

        Car car = findOrCreateCar(requestForm.getCar());
        Client client = findOrCreateClient(requestForm.getClient());

        if (requestForm.getClient().getEmail() != null && !requestForm.getClient().getEmail().isBlank()) {
            String newEmail = requestForm.getClient().getEmail();

            if (client.getEmail() == null || !client.getEmail().equals(newEmail)) {
                client.setEmail(newEmail);
                clientRepository.save(client);
            }
        }

        for (int attempts = 0; attempts < 3; attempts++) {
            String requestNumber = NumberGenerator.generateNumber();

            if (!requestRepository.existsByRequestNumber(requestNumber)) {
                Request request = new Request(client, car, operator,
                        RequestStatus.OPEN, requestForm.getComplaint(), requestNumber);
                requestRepository.save(request);
                return requestNumber;
            }

            System.out.println("Конфликт номера, попытка " + (attempts + 1));
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // восстанавливаем флаг
                throw new IllegalStateException("Поток был прерван", ie);
            }
        }

        throw new IllegalStateException("Не удалось сгенерировать номер заявки, повторите попытку позже");
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


    public Optional<Request> findRequestById(Long id) {

        return requestRepository.findById(id);
    }


    public boolean cancelRequest(Request request) {

        if (request.getRequestStatus().equals(RequestStatus.OPEN) || request.getRequestStatus().equals(RequestStatus.IN_PROGRESS)) {
            request.setRequestStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            return true;
        }

        return false;
    }


    public Page<Request> findRequests(RequestStatus status, LocalDateTime startDate, LocalDateTime endDate,
                                      Pageable pageable, Operator operator) {

        if (startDate == null || endDate == null || status == null) {
            throw new IllegalArgumentException("Все параметры должны быть переданы");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }

        if (operator != null) {
            if (status.equals(RequestStatus.ALL)) {
                return requestRepository.findRequestsByDate(startDate, endDate, operator.getId(), pageable);
            } else {
                return requestRepository.findRequestsByStatusAndDate(status, startDate, endDate, operator.getId(), pageable);
            }
        } else {
            if (status.equals(RequestStatus.ALL)) {
                return requestRepository.findRequestsByDate(startDate, endDate, pageable);
            } else {
                return requestRepository.findRequestsByStatusAndDate(status, startDate, endDate, pageable);
            }
        }
    }


    public Page<Request> findRequestsByNumberAndPhone(String searchNumber, String searchPhone, Pageable pageable) {

        return requestRepository.findByRequestNumberAndClientPhoneNumber(NumberGenerator.toEnglish(searchNumber), searchPhone, pageable);
    }


    public Page<Request> findRequestsByNumber(String searchNumber, Pageable pageable) {

        return requestRepository.findByRequestNumber(NumberGenerator.toEnglish(searchNumber), pageable);
    }


    public Page<Request> findRequestsByPhone(String searchPhone, Pageable pageable) {

        return requestRepository.findByClientPhoneNumber(searchPhone, pageable);
    }
}