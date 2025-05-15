package ru.victortikhonov.autoserviceapp.model;

import ru.victortikhonov.autoserviceapp.model.Personnel.Employee;

public interface Task {
    TaskStatus getStatus();
    Employee getEmployee();
}

