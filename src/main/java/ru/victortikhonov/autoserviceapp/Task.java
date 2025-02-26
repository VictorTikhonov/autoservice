package ru.victortikhonov.autoserviceapp;

import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;

public interface Task {
    TaskStatus getStatus();
    Employee getEmployee();
}

