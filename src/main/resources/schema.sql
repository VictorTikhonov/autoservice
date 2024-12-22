DROP TABLE IF EXISTS
    employees,
    positions,
    employee_statuses,
    accounts,
    cars,
    clients
    CASCADE;

-- �������� ������� Clients
CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,               -- ID �������, �������������
                         surname VARCHAR(50) NOT NULL,           -- �������, ������������ ����
                         name VARCHAR(50) NOT NULL,              -- ���, ������������ ����
                         patronymic VARCHAR(50),                 -- ��������, ����� ���� ������
                         phone_number VARCHAR(11) NOT NULL,      -- ����� ��������, ����� 11 ��������
                         email VARCHAR(50),                      -- �����, ����� ���� ������
                         registration_date DATE DEFAULT CURRENT_DATE,  -- ���� �����������, �� ��������� �������

    -- ����������� �� ������ ����� � ������ �������� � ������ ����� 11 ��������
                         CONSTRAINT phone_number_digits_clients CHECK (phone_number ~ '^[0-9]{11}$')
);



-- �������� ������� Cars
CREATE TABLE cars (
                      id BIGSERIAL PRIMARY KEY,                   -- ID ����������
                      state_number VARCHAR(20),                   -- ��������, ����� �� 20 ��������, ����� ������ ������������� ������
                      vin VARCHAR(17) NOT NULL,                   -- VIN �����, ����� 17 ��������
                      brand VARCHAR(50) NOT NULL,                 -- ����� ����������, ������������ ����
                      model VARCHAR(50) NOT NULL,                 -- ������ ����������, ������������ ����
                      year_of_manufacture DATE NOT NULL,          -- ��� �������, ������������ ����

    -- ��� �������, �� 1900 �� �������� ����
                      CONSTRAINT year_of_manufacture_check
                          CHECK (year_of_manufacture >= '1900-01-01' AND year_of_manufacture <= CURRENT_DATE),
    -- ����������� �� ������ ��������� (���� �������� �� ������)
                      CONSTRAINT state_number_check
                          CHECK (state_number IS NULL OR state_number ~ '^[A-Za-z�-��-�0-9]{1,20}$'),
    -- ����������� �� ������ � ������ ����� VIN ������ (17 ��������)
                      CONSTRAINT vin_format_check CHECK (vin ~ '^[A-HJ-NPR-Z0-9]{17}$')
);





-- �������� ������� accounts (������� ������)
CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,               -- ID ������� ������
                          login VARCHAR(30) NOT NULL UNIQUE,      -- �����, ���������� ����
                          password VARCHAR(100) NOT NULL          -- ������, ������������ ����
);



-- �������� ������� employee_statuses (������� �����������)
CREATE TABLE employee_statuses (
                                   id BIGSERIAL PRIMARY KEY,               -- ID ������� ����������
                                   status_name VARCHAR(35) NOT NULL UNIQUE -- ������������ ������� ����������, ������������ � ���������� ����
);



-- �������� ������� positions (���������)
CREATE TABLE positions (
                           id BIGSERIAL PRIMARY KEY,               -- ID ���������
                           position_name VARCHAR(35) NOT NULL UNIQUE -- ������������ ���������, ������������ � ���������� ����
);


-- �������� ������� employees (����������)
CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,                           -- ID ����������
                           account_id BIGINT NOT NULL,                         -- ID ������� ������, ������������ ����
                           position_id BIGINT NOT NULL,                        -- ID ���������, ������������ ����
                           employment_status_id BIGINT NOT NULL,               -- ID ��������� �������, ������������ ����
                           surname VARCHAR(50) NOT NULL,                       -- ������� ����������, ������������ ����
                           name VARCHAR(50) NOT NULL,                          -- ��� ����������, ������������ ����
                           patronymic VARCHAR(50),                             -- �������� ����������, ����� ���� ������
                           phone_number VARCHAR(11) NOT NULL,                     -- ����� ��������, ������������ ����, ����� 11
                           salary DECIMAL(10, 2) NOT NULL,                     -- ���������� �����, ������������ ����
                           hire_date DATE NOT NULL,                            -- ���� ���������������, ������������ ����
                           dismissal_date DATE,                                -- ���� ����������, ����� ���� ������
                           birth_date DATE NOT NULL,                           -- ���� ��������, ������������ ����

    -- ������� ���� �� ������� accounts
                           CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    -- ������� ���� �� ������� positions
                           CONSTRAINT fk_position FOREIGN KEY (position_id) REFERENCES positions(id),
    -- ������� ���� �� ������� employee_statuses
                           CONSTRAINT fk_employment_status FOREIGN KEY (employment_status_id) REFERENCES employee_statuses(id),
    -- ����������� �� ������ ������ ��������
                           CONSTRAINT phone_number_digits_employees CHECK (phone_number ~ '^[0-9]{11}$')
);
